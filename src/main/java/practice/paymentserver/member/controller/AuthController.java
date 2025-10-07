package practice.postserver.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.postserver.global.apiPayload.CustomResponse;
import practice.postserver.global.apiPayload.code.SuccessCode;
import practice.postserver.member.dto.AuthReqDTO;
import practice.postserver.member.dto.AuthResDTO;
import practice.postserver.member.service.AuthCommandService;

@Tag(name = "회원가입/로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "회원가입", description = "회원가입합니다.")
    @PostMapping("/signup")
    public CustomResponse<AuthResDTO.SignupResponseDTO> signup(@RequestBody AuthReqDTO.SignupRequestDTO dto) {
        AuthResDTO.SignupResponseDTO signupResponseDTO = authCommandService.signUp(dto);
        return CustomResponse.onSuccess(SuccessCode.CREATED, signupResponseDTO);
    }

    @Operation(summary = "로그인", description = "로그인합니다.")
    @PostMapping("/login")
    public CustomResponse<AuthResDTO.LoginResponseDTO> login(@RequestBody AuthReqDTO.LoginRequestDTO dto) {
        AuthResDTO.LoginResponseDTO loginResponseDTO = authCommandService.login(dto);
        return CustomResponse.onSuccess(SuccessCode.OK, loginResponseDTO);
    }

    @Operation(summary = "로그아웃", description = "로그아웃합니다.")
    @PostMapping("/logout")
    public CustomResponse<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authCommandService.logout(request, response);
        return CustomResponse.onSuccess(SuccessCode.OK);
    }

    @Operation(summary = "리이슈", description = "액세스 토큰을 재발행합니다. id와 refresh 필요")
    @PostMapping("/reissue")
    public CustomResponse<AuthResDTO.ReissueResponseDTO> reissue(@RequestBody AuthReqDTO.ReissueRequestDTO dto) {
        AuthResDTO.ReissueResponseDTO reissue = authCommandService.reissue(dto);
        return CustomResponse.onSuccess(SuccessCode.OK, reissue);
    }

}
