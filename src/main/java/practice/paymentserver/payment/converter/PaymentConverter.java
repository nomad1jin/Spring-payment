package practice.paymentserver.payment.converter;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import practice.paymentserver.member.entity.Member;
import practice.paymentserver.payment.dto.PaymentReqDTO;
import practice.paymentserver.payment.dto.PaymentResDTO;
import practice.paymentserver.payment.entity.Payment;

import java.time.LocalDateTime;

@Component
public class PaymentConverter {
    public Payment toPayment(PaymentReqDTO dto, Member member) {
        return Payment.builder()
                .orderName(dto.getOrderName())
                .orderId(dto.getOrderId())
                .totalAmount(dto.getAmount())
                .status("READY")
                .paySuccessYN(false)
                .member(member)
                .build();
    }

    public PaymentResDTO.SuccessDTO toPaymentResDTO() {
    }
}
