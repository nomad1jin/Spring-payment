package practice.paymentserver.payment.converter;

import org.springframework.stereotype.Component;
import practice.paymentserver.item.Item;
import practice.paymentserver.member.entity.Member;
import practice.paymentserver.payment.dto.PaymentResDTO;
import practice.paymentserver.payment.entity.Payment;
import practice.paymentserver.payment.enums.OrderStatus;

@Component
public class PaymentConverter {
    public Payment toPayment(String orderId, String orderName, Item item, Member member) {
        return Payment.builder()
                .orderId(orderId)
                .orderName(orderName)
                .totalAmount(item.getPrice())
                .status(OrderStatus.READY)
                .member(member)
                .build();
    }

    public PaymentResDTO.PrepareDTO toPrepareDTO(Payment payment) {
        return PaymentResDTO.PrepareDTO.builder()
                .orderId(payment.getOrderId())
                .orderName(payment.getOrderName())
                .totalAmount(payment.getTotalAmount())
                .status(payment.getStatus())
                .build();
    }

    public PaymentResDTO.ApproveDTO toApproveDTO(PaymentResDTO.TossResponseDTO responseDto, Payment payment) {
        return PaymentResDTO.ApproveDTO.builder()
                .orderId(responseDto.getOrderId())
                .orderName(responseDto.getOrderName())
                .totalAmount(responseDto.getTotalAmount())
                .status(payment.getStatus())
                .build();
    }
}
