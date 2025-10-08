package practice.paymentserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class PaymentReqDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrepareDTO {
        private Long itemId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApproveDTO {
        //토스 결제 시 아래 3개 필수
        private String orderId;
        private String paymentKey;
        private Integer finalAmount;
    }

}
