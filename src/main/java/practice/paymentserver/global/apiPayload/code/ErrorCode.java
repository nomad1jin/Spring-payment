package practice.postserver.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode implements BaseCode {

    BAD_REQUEST_400(HttpStatus.BAD_REQUEST,
            "COMMON400",
            "잘못된 요청입니다"),
    UNAUTHORIZED_401(HttpStatus.UNAUTHORIZED,
            "COMMON401",
            "인증이 필요합니다"),
    FORBIDDEN_403(HttpStatus.FORBIDDEN,
            "COMMON403",
            "접근이 금지되었습니다"),
    NOT_FOUND_404(HttpStatus.NOT_FOUND,
            "COMMON404",
            "요청한 자원을 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR_500(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500",
            "서버 내부 오류가 발생했습니다"),

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID400_0", "잘못된 파라미터 입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN401" ,"유효한 토큰이 아닙니다." ),
    NULL_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "ACCESS_NULL", "액세스토큰이 비어있습니다."),
    DUPLICATED_LOGINID(HttpStatus.CONFLICT, "DUPLICATED_LOGINID" , "중복된 로그인 아이디입니다."),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "DUPLICATED_NICKNAME", "중복된 닉네임입니다."),
    INCORRECT_PASSWORD(HttpStatus.CONFLICT, "INCORRECT_PASSWORD" , "비밀번호가 일치하지 않습니다."),

    BLACKLISTED(HttpStatus.FORBIDDEN, "BLACKLISTED", "블랙리스트 처리된 액세스토큰입니다."),

    MEMBER_NOTFOUND(HttpStatus.NOT_FOUND, "MEMBER_404", "등록된 회원이 없습니다."),
    PET_NOTFOUND(HttpStatus.NOT_FOUND, "PET_404", "등록된 펫이 없습니다."),
    STORY_NOTFOUND(HttpStatus.NOT_FOUND, "STORY_404", "등록된 일상 일지가 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
