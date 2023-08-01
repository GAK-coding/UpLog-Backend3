package com.uplog.uplog.domain.comment.exception.handler;


import com.uplog.uplog.domain.comment.exception.MemberAuthorizedException;
import com.uplog.uplog.domain.comment.exception.NotFoundCommentByPostException;
import com.uplog.uplog.domain.comment.exception.NotFoundCommentException;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.global.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CommentExceptionHandler {

    @ExceptionHandler(NotFoundCommentByPostException.class)
    protected final ResponseEntity<ErrorResponse> notFoundCommentByPostExceptionHandler(NotFoundCommentByPostException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(NotFoundCommentException.class)
    protected final ResponseEntity<ErrorResponse> notFoundCommentExceptionHandler(NotFoundCommentException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(MemberAuthorizedException.class)
    protected final ResponseEntity<ErrorResponse> MemberAuthorizedExceptionHandler(MemberAuthorizedException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }
}
