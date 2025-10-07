package practice.paymentserver.payment.converter;

import org.springframework.stereotype.Component;
import practice.paymentserver.member.entity.Member;
import practice.paymentserver.payment.dto.PaymentReqDTO;
import practice.paymentserver.payment.dto.PaymentResDTO;
import practice.paymentserver.payment.entity.Payment;

import java.time.LocalDateTime;

@Component
public class PaymentConverter {
    public Payment toPayment(PaymentResDTO.PaymentDTO dto, Member member) {
        return Payment.builder()
                .paymentKey(dto.getPaymentKey())
                .orderId(dto.getOrderId())
                .orderName(dto.getOrderName())
                .totalAmount(dto.getTotalAmount())
                .method(dto.getMethod())
                .status(dto.getStatus())
                .member(member)
                .approvedAt(dto.getApprovedAt())
                .build();
    }

    public PaymentResDTO.PaymentDTO toPaymentResDTO(Payment payment) {
        return PaymentResDTO.PaymentDTO.builder()
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getOrderId())
                .orderName(payment.getOrderName())
                .totalAmount(payment.getTotalAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .build();
    }
}
