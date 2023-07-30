package com.uplog.uplog.domain.changedIssue.exception.handler;

import com.uplog.uplog.domain.changedIssue.exception.notFoundIssueByProjectException;
import com.uplog.uplog.domain.changedIssue.exception.notFoundIssueException;
import com.uplog.uplog.domain.comment.exception.NotFoundCommentByPostException;
import com.uplog.uplog.global.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ChangedIssueExceptionHandler {
    @ExceptionHandler(notFoundIssueException.class)
    protected final ResponseEntity<ErrorResponse> notFoundIssueExceptionHandler(notFoundIssueException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(notFoundIssueByProjectException.class)
    protected final ResponseEntity<ErrorResponse> notFoundIssueByProjectExceptionHandler(notFoundIssueByProjectException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }
}
