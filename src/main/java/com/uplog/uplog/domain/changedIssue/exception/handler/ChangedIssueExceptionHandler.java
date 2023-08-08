package com.uplog.uplog.domain.changedIssue.exception.handler;

import com.uplog.uplog.domain.changedIssue.exception.NotFoundIssueByProjectException;
import com.uplog.uplog.domain.changedIssue.exception.NotFoundIssueException;
import com.uplog.uplog.domain.changedIssue.exception.NotFoundPowerByMemberException;
import com.uplog.uplog.global.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ChangedIssueExceptionHandler {
    @ExceptionHandler(NotFoundIssueException.class)
    protected final ResponseEntity<ErrorResponse> notFoundIssueExceptionHandler(NotFoundIssueException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(NotFoundIssueByProjectException.class)
    protected final ResponseEntity<ErrorResponse> notFoundIssueByProjectExceptionHandler(NotFoundIssueByProjectException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(NotFoundPowerByMemberException.class)
    protected final ResponseEntity<ErrorResponse> notFoundPowerByMemberExceptionHandler(NotFoundPowerByMemberException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

}
