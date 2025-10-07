package practice.paymentserver.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.paymentserver.global.apiPayload.code.CustomException;
import practice.paymentserver.global.apiPayload.code.ErrorCode;
import practice.paymentserver.member.enums.Gender;
import practice.paymentserver.member.enums.Type;
import practice.paymentserver.payment.entity.Payment;

import java.util.List;


@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private int old;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    private int boneBalance;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<Payment> payments;

    public void chargePoint(int amount) {
        if (amount < 1000) {
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        this.boneBalance += amount;
    }
}
