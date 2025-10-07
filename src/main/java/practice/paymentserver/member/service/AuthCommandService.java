package practice.paymentserver.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import practice.paymentserver.member.dto.AuthReqDTO;
import practice.paymentserver.member.dto.AuthResDTO;
import practice.paymentserver.member.entity.Member;


public interface AuthCommandService {
    AuthResDTO.SignupResponseDTO signUp(AuthReqDTO.SignupRequestDTO dto);
    AuthResDTO.LoginResponseDTO login(AuthReqDTO.LoginRequestDTO dto);
    void logout(HttpServletRequest request,  HttpServletResponse response);
    AuthResDTO.ReissueResponseDTO reissue(AuthReqDTO.ReissueRequestDTO dto);
    Member findById(Long memberId);
    Member findByUsername(String username);
}
