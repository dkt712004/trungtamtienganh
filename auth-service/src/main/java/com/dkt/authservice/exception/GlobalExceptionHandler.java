package com.dkt.authservice.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    /**
     * Bắt và xử lý lỗi BadCredentialsException (sai email/mật khẩu).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        String errorMessage = messageSource.getMessage("error.login.credentials", null, LocaleContextHolder.getLocale());
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Bắt và xử lý các lỗi RuntimeException chung khác nếu cần.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // Dịch các thông báo lỗi chung khác nếu cần
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}