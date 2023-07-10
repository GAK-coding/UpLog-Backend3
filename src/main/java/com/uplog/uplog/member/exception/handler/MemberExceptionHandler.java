package com.uplog.uplog.member.exception.handler;

import com.uplog.uplog.global.error.ErrorResponse;
import com.uplog.uplog.member.exception.NotFoundMemberByEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class MemberExceptionHandler {
    @ExceptionHandler(NotFoundMemberByEmailException.class)
    protected final ResponseEntity<ErrorResponse> NotFoundEmailExceptionHandler(NotFoundMemberByEmailException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }


}
