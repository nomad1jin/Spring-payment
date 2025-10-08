package practice.paymentserver.payment.enums;

public enum OrderStatus {
    READY,                  // 결제 대기
    DONE,                   // 결제 승인
    CANCELED,               // 승인된 결제 취소
    ABORTED,                // 결제 승인 실패
    EXPIRED,                // 결제 유효 기간이 지나 거래 취소
    REFUND_REQUESTED,       // 환불 요청
    REFUND_FAILED,          // 환불 실패
    REFUNDED                // 환불 완료
}
