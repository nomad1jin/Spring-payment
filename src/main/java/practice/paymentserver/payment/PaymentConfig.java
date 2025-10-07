package practice.paymentserver.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PaymentConfig {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Value("${toss.client-key}")
    private String clientKey;

    @Value("${toss.success_url}")
    private String successUrl;

    @Value("${toss.fail_url}")
    private String failUrl;

    public static final String URL = "https://api.tosspayments.com/v1/payments/confirm";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
