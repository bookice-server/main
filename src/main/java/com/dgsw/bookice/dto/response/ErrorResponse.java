package com.dgsw.bookice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<FieldError> fieldErrors;

    /**
     * 일반 에러 응답 생성
     */
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                null
        );
    }

    /**
     * 필드 유효성 검증 에러 응답 생성
     */
    public static ErrorResponse of(int status, String error, String message, List<FieldError> fieldErrors) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                fieldErrors
        );
    }

    /**
     * 필드 에러 정보
     */
    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
