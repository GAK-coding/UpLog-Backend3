package com.uplog.uplog.member.exception.handler;

import com.uplog.uplog.global.error.ErrorResponse;
import com.uplog.uplog.member.exception.DuplicatedMemberException;
import com.uplog.uplog.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.member.exception.NotMatchPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class MemberExceptionHandler {
    @ExceptionHandler(NotFoundMemberByEmailException.class)
    protected final ResponseEntity<ErrorResponse> notFoundEmailExceptionHandler(NotFoundMemberByEmailException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(DuplicatedMemberException.class)
    protected final ResponseEntity<ErrorResponse> duplicatedMemberExceptionHandler(DuplicatedMemberException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_ACCEPTABLE)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(NotMatchPasswordException.class)
    protected final ResponseEntity<ErrorResponse> notMatchPasswordExceptionHandler(NotMatchPasswordException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_ACCEPTABLE)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }


}
