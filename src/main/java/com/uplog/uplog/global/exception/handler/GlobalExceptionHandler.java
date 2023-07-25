package com.uplog.uplog.global.exception.handler;

import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorityException.class)
    protected final ResponseEntity<ErrorResponse> authorityExceptionHandler(AuthorityException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(NotFoundIdException.class)
    protected final ResponseEntity<ErrorResponse> notFoundIdExceptionHandler(NotFoundIdException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

}
