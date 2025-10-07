package practice.paymentserver.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.paymentserver.global.apiPayload.code.CustomException;
import practice.paymentserver.global.apiPayload.code.ErrorCode;
import practice.paymentserver.member.entity.Member;
import practice.paymentserver.member.repository.MemberRepository;
import practice.paymentserver.payment.dto.PaymentReqDTO;
import practice.paymentserver.payment.entity.Payment;
import practice.paymentserver.payment.converter.PaymentConverter;
import practice.paymentserver.payment.repository.PaymentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandServiceImpl {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final PaymentConverter paymentConverter;



    @Transactional
    public void savePayment(PaymentReqDTO dto, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));

        Payment payment = paymentConverter.toPayment(dto, member);
        Payment savedPayment = paymentRepository.save(payment);
        log.info("[ Payment Saved ] ");

        paymentConverter.toPaymentResDTO();
    }
}
