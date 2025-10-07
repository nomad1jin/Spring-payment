package practice.postserver.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import practice.postserver.global.apiPayload.code.CustomException;
import practice.postserver.global.apiPayload.code.ErrorCode;
import practice.postserver.global.jwt.JwtUtil;
import practice.postserver.global.redis.service.RedisAuthService;
import practice.postserver.member.converter.AuthConverter;
import practice.postserver.member.dto.AuthReqDTO;
import practice.postserver.member.dto.AuthResDTO;
import practice.postserver.member.entity.Member;
import practice.postserver.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthConverter authConverter;
    private final RedisAuthService redisAuthService;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResDTO.SignupResponseDTO signUp(AuthReqDTO.SignupRequestDTO dto) {

        // 1. 유효성, 중복성 체크
        if (memberRepository.existsByLoginId(dto.getLoginId())) {
            throw new CustomException(ErrorCode.DUPLICATED_LOGINID);
        }
        if (memberRepository.existsByNickname(dto.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }
        if (!dto.getPassword().equals(dto.getPassword2())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        // 2. 비밀번호 인코딩
        String encoded = passwordEncoder.encode(dto.getPassword());

        // 3. 컨버터 매핑 (dto -> entity)
        Member entity = authConverter.toSignupEntity(dto, encoded);

        // 4. 저장
        Member saved = memberRepository.save(entity);

        // 5. 응답dto 변환
        return authConverter.toSignupResponseDTO(saved);
    }

    @Override
    public AuthResDTO.LoginResponseDTO login(AuthReqDTO.LoginRequestDTO dto) {
        //DB안에 회원이 없으면 not found 예외
        Member member = memberRepository.findByLoginId(dto.getLoginId()).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_404));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED);
        }
        //걸리는게 없으면 로그인 시 유저 정보로 토큰 만들기
        return createLoginToken(member);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        // 1. 헤더에서 accessToken 꺼내기
        String accessToken = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
            log.info("[ access ]: {}", accessToken);
        }
        if (accessToken == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 2. 블랙리스트 처리된 액세스 토큰인지 검증
        if (redisAuthService.isBlacklisted(accessToken)) {
            throw new CustomException(ErrorCode.BLACKLISTED);
        }

        // 3. 액세스 토큰 블랙리스트 처리
        redisAuthService.addBlacklist(accessToken);

        // 4. 리프레시 토큰 삭제
        Long id = jwtUtil.getId(accessToken);
        redisAuthService.deleteRefreshToken(id);

        log.info("[ 로그아웃 완료 ]");
    }

    // 클라이언트가 refresh를 주면 access 토큰 재발행
    @Override
    public AuthResDTO.ReissueResponseDTO reissue(AuthReqDTO.ReissueRequestDTO dto) {

        // 유효한 리프레시토큰이면 새로운 액세스 토큰을 발급해주기
        if(!jwtUtil.isExpired(dto.getRefreshToken())) {
            Member member = memberRepository.findById(dto.getId()).orElseThrow(() ->
                    new CustomException(ErrorCode.NOT_FOUND_404));
            log.info("[ 액세스토큰 재발급 완료 ]");
            return reissueAccessToken(member);
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }


    public AuthResDTO.LoginResponseDTO createLoginToken(Member member) {
        return AuthResDTO.LoginResponseDTO.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .accessToken(jwtUtil.createAccessToken(member))
                .refreshToken(jwtUtil.createRefreshToken(member))
                .build();
    }

    public AuthResDTO.ReissueResponseDTO reissueAccessToken(Member member) {
        return AuthResDTO.ReissueResponseDTO.builder()
                .id(member.getId())
                .accessToken(jwtUtil.createAccessToken(member))
                .build();
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));
    }
}
