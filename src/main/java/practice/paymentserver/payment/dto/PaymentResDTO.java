package practice.paymentserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PaymentResDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SuccessDTO{
//        private String paymentKey;
        private String orderId;
        private String orderName;
        private int amount;
        private String method;
        private String status;
        private String successUrl;
        private LocalDateTime approvedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FailDTO {
        private String paymentKey;
        private String orderId;
        private String orderName;
        private String method;
        private String status;
        private String failUrl;
        private String failReason;
        private boolean cancelYN;
        private String cancelReason;
        private LocalDateTime approvedAt;
    }
}
