package practice.paymentserver.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.paymentserver.member.entity.Member;

import java.time.OffsetDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;     // 고유 결제키
    private String orderId;        // 주문 ID
    private String orderName;      // 결제명
    private String method;         // 결제 방식 (카드/토스페이 등)
    private String status;         // DONE / CANCELED / FAILED
    private int totalAmount;       // 총 결제금액
    private boolean paySuccessYN;
    private OffsetDateTime approvedAt; // 결제 승인 시각

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
