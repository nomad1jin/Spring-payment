package practice.paymentserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class PaymentResDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentDTO {
        private String paymentKey;
        private String orderId;
        private String orderName;
        private int totalAmount;
        private String method;          // 결제수단
        private String status;          // 결제상태
        private OffsetDateTime requestedAt;     // 결제 요청 시각
        private OffsetDateTime approvedAt;      // 결제 승인 시각
    }
}
