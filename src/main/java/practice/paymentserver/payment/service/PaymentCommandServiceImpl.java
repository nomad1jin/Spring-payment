package practice.paymentserver.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import practice.paymentserver.global.apiPayload.code.CustomException;
import practice.paymentserver.global.apiPayload.code.ErrorCode;
import practice.paymentserver.item.Item;
import practice.paymentserver.item.ItemRepository;
import practice.paymentserver.member.entity.Member;
import practice.paymentserver.member.repository.MemberRepository;
import practice.paymentserver.payment.PaymentConfig;
import practice.paymentserver.payment.converter.PaymentConverter;
import practice.paymentserver.payment.dto.PaymentReqDTO;
import practice.paymentserver.payment.dto.PaymentResDTO;
import practice.paymentserver.payment.entity.Payment;
import practice.paymentserver.payment.enums.OrderStatus;
import practice.paymentserver.payment.repository.PaymentRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandServiceImpl {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final PaymentConverter paymentConverter;
    private final RestTemplate restTemplate;
    private final PaymentConfig paymentConfig;

    public PaymentResDTO.PrepareDTO preparePayment(PaymentReqDTO.PrepareDTO dto, Long memberId) {
        log.info("[ 결제 준비 시작 - 회원ID: {}, ItemId: {} ]", memberId, dto.getItemId());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOTFOUND));

        String orderId = memberId + "order-" + UUID.randomUUID();
        String orderName = item.getItemName() + " 1건";
        Payment payment = paymentConverter.toPayment(orderId, orderName, item, member);
        Payment savedPayment = paymentRepository.save(payment);
        log.info("[ 결제 준비 완료 - 회원ID: {}, orderId: {}, 결제금액: {} ]", memberId, orderId, savedPayment.getTotalAmount());

        return paymentConverter.toPrepareDTO(savedPayment);
    }

    public PaymentResDTO.ApproveDTO approvePayment(PaymentReqDTO.ApproveDTO dto, Long memberId) {
        log.info("[ 결제 승인 시작 - 회원ID: {}, orderId: {} ]", memberId, dto.getOrderId());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));
        Payment payment = paymentRepository.findByOrderId(dto.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOTFOUND));

        // 결제 금액 확인
        if(!dto.getFinalAmount().equals(payment.getTotalAmount())) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 결제 승인 확인
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("paymentKey", dto.getPaymentKey());
            body.put("orderId", dto.getOrderId());
            body.put("amount", dto.getFinalAmount());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
            ResponseEntity<PaymentResDTO.TossResponseDTO> responseEntity
                    = restTemplate.postForEntity(paymentConfig.getURL(), entity, PaymentResDTO.TossResponseDTO.class);
            PaymentResDTO.TossResponseDTO responseDto = getPaymentResponseDto(responseEntity);

            log.info("[ 결제 승인 완료 - 회원ID: {}, orderId: {}, 결제금액: {} ]", memberId, dto.getOrderId(), responseDto.getTotalAmount());
            payment.updateStatus(OrderStatus.DONE);
            paymentRepository.save(payment);
            member.chargePoint(responseDto.getTotalAmount());
            memberRepository.save(member);

            return paymentConverter.toApproveDTO(responseDto, payment);

        } catch (HttpClientErrorException e) {
            log.error("[TOSS 4xx 오류] {}", e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.PAYMENT_ABORTED);
        } catch (HttpServerErrorException e) {
            log.error("[TOSS 5xx 서버 오류] {}", e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.PAYMENT_UNSPECIFIED_ERROR);
        } catch (CustomException e) {
            log.warn("[비즈니스 예외] {}", e.getCode().getMessage());
            throw e;
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(paymentConfig.getSecretKey(), "");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return headers;
    }

    private PaymentResDTO.TossResponseDTO getPaymentResponseDto(ResponseEntity<PaymentResDTO.TossResponseDTO> responseEntity) {
        PaymentResDTO.TossResponseDTO responseDto = responseEntity.getBody();

        if (responseDto == null) {
            throw new CustomException(ErrorCode.PAYMENT_PROCESSING_ERROR);
        }

        // 상태가 DONE이 아닌 경우 결제된 상태 X
        if(!responseDto.getStatus().equals("DONE"))  {
            switch (responseDto.getStatus()) {
                case "WAITING_FOR_DEPOSIT":
                    throw new CustomException(ErrorCode.PAYMENT_WAITING_FOR_DEPOSIT);
                case "IN_PROGRESS":
                    throw new CustomException(ErrorCode.PAYMENT_IN_PROGRESS);
                case "CANCELED":
                    throw new CustomException(ErrorCode.PAYMENT_CANCELED);
                case "PARTIAL_CANCELED":
                    throw new CustomException(ErrorCode.PAYMENT_PARTIAL_CANCELED);
                case "ABORTED":
                    throw new CustomException(ErrorCode.PAYMENT_ABORTED);
                case "EXPIRED":
                    throw new CustomException(ErrorCode.PAYMENT_EXPIRED);
                default:
                    throw new CustomException(ErrorCode.PAYMENT_UNSPECIFIED_ERROR);
            }
        }
        return responseDto;
    }

}
