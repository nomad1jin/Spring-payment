package practice.paymentserver.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.paymentserver.global.apiPayload.CustomResponse;
import practice.paymentserver.global.apiPayload.code.SuccessCode;
import practice.paymentserver.global.jwt.CustomUserDetails;
import practice.paymentserver.payment.dto.PaymentReqDTO;
import practice.paymentserver.payment.dto.PaymentResDTO;
import practice.paymentserver.payment.entity.Payment;
import practice.paymentserver.payment.service.PaymentCommandServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentCommandServiceImpl paymentCommandService;

    @PostMapping
    public CustomResponse<PaymentResDTO> requestTossPayment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestBody PaymentReqDTO dto) {
        PaymentResDTO resDTO = paymentCommandService.savePayment(dto, userDetails.getId());
        return CustomResponse.onSuccess(SuccessCode.OK, resDTO);
    }
}
