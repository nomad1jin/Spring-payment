package practice.paymentserver.global.apiPayload.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {

    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
