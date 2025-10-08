package practice.paymentserver.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.paymentserver.payment.enums.OrderStatus;

public class PaymentResDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrepareDTO {
        private String orderId;
        private String orderName;
        private int totalAmount;
        private OrderStatus status;     // 결제상태
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    //토스 응답중에서 서비스에 사용하는 값만 사용하게끔, dto에 없는 필드 무시
    public static class TossResponseDTO {
        private String paymentKey;
        private String orderId;
        private String orderName;
        private String method;
        private String status;
        private Integer totalAmount;
        private String approvedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApproveDTO {
        private String orderId;
        private String orderName;
        private int totalAmount;
        private OrderStatus status;
    }
}
