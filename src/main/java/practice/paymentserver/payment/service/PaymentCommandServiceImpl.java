package practice.paymentserver.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import practice.paymentserver.global.apiPayload.code.CustomException;
import practice.paymentserver.global.apiPayload.code.ErrorCode;
import practice.paymentserver.member.entity.Member;
import practice.paymentserver.member.repository.MemberRepository;
import practice.paymentserver.payment.PaymentConfig;
import practice.paymentserver.payment.converter.PaymentConverter;
import practice.paymentserver.payment.dto.PaymentReqDTO;
import practice.paymentserver.payment.dto.PaymentResDTO;
import practice.paymentserver.payment.entity.Payment;
import practice.paymentserver.payment.repository.PaymentRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandServiceImpl {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final PaymentConverter paymentConverter;
    private final RestTemplate restTemplate;

    @Value("${toss.secret-key}")
    private String secretKey;

    /**
     * 결제 승인 및 처리 (통합)
     */
    @Transactional
    public PaymentResDTO.PaymentDTO confirmAndProcessPayment(PaymentReqDTO dto, Long memberId) {

        // 1. 토스페이먼츠 결제 승인 API 호출
        PaymentResDTO.PaymentDTO tossResponse = confirmPayment(dto);

        // 2. 결제 성공 후 처리
        processPaymentSuccess(tossResponse, memberId);

        return tossResponse;
    }

    /**
     * 토스페이먼츠 결제 승인 API 호출
     */
    public PaymentResDTO.PaymentDTO confirmPayment(PaymentReqDTO dto) {

        String url = PaymentConfig.URL;

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        String encodeSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodeSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("paymentKey", dto.getPaymentKey());
        requestBody.put("orderId", dto.getOrderId());
        requestBody.put("amount", dto.getAmount());

        try {
            // 토스페이먼츠 API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, headers),
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            log.info("Toss payment confirmed - paymentKey: {}, amount: {}",
                    dto.getPaymentKey(), dto.getAmount());

            // JSON → DTO 변환
            return PaymentResDTO.PaymentDTO.builder()
                    .paymentKey(root.path("paymentKey").asText())
                    .orderId(root.path("orderId").asText())
                    .orderName(root.path("orderName").asText())
                    .method(root.path("method").asText())
                    .status(root.path("status").asText())
                    .requestedAt(parseOffsetDateTime(root.path("requestedAt").asText()))
                    .approvedAt(parseOffsetDateTime(root.path("approvedAt").asText()))
                    .totalAmount(root.path("totalAmount").asInt())
                    .build();

        } catch (HttpClientErrorException e) {
            log.error("Toss payment confirmation failed: {}", e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.PAYMENT_CONFIRMATION_FAILED);
        } catch (Exception e) {
            log.error("Unexpected error during payment confirmation", e);
            throw new CustomException(ErrorCode.PAYMENT_PROCESSING_ERROR);
        }
    }

    /**
     * 결제 성공 후 DB 저장 및 포인트 충전
     */
    @Transactional
    public void processPaymentSuccess(PaymentResDTO.PaymentDTO tossResponse, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));

        Payment payment = paymentConverter.toPayment(tossResponse, member);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("[ Payment saved - id: {}, amount: {} ]", savedPayment.getId(), savedPayment.getTotalAmount());

        // 4. 포인트 충전
        member.chargePoint(tossResponse.getTotalAmount());
        memberRepository.save(member);
    }
    private OffsetDateTime parseOffsetDateTime(String dateStr) {
        try {
            return OffsetDateTime.parse(dateStr);
        } catch (DateTimeParseException e) {
            log.warn("Invalid date format from Toss API: {}", dateStr);
            return null;
        }
    }
}
