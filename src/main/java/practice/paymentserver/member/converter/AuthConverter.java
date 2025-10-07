package practice.postserver.member.converter;

import org.springframework.stereotype.Component;
import practice.postserver.member.dto.AuthReqDTO;
import practice.postserver.member.dto.AuthResDTO;
import practice.postserver.member.entity.Member;

@Component
public class AuthConverter {

    public Member toSignupEntity(AuthReqDTO.SignupRequestDTO dto, String encodedPassword) {
        return Member.builder()
                .username(dto.getUsername())
                .nickname(dto.getNickname())
                .gender(dto.getGender())
                .old(dto.getOld())
                .address(dto.getAddress())
                .phoneNumber(String.valueOf(dto.getPhoneNumber()))
                .type(dto.getType())
                .loginId(dto.getLoginId())
                .password(encodedPassword) // ← 인코딩된 비번을 주입
                .email(dto.getEmail())
                .build();
    }

    public AuthResDTO.SignupResponseDTO toSignupResponseDTO(Member member) {
        return AuthResDTO.SignupResponseDTO.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .build();
    }
}
