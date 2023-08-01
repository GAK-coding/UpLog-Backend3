package com.uplog.uplog.domain.project.exception.handler;

import com.uplog.uplog.domain.project.exception.DuplicateVersionNameException;
import com.uplog.uplog.domain.project.exception.ExistProcessProjectExeption;
import com.uplog.uplog.domain.project.exception.NotFoundProjectException;
import com.uplog.uplog.global.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectExceptionHandler {

    @ExceptionHandler(ExistProcessProjectExeption.class)
    protected final ResponseEntity<ErrorResponse> ExistProcessProjectExeptionHandler(ExistProcessProjectExeption e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(NotFoundProjectException.class)
    protected final ResponseEntity<ErrorResponse> NotFoundProjectExceptionHandler(NotFoundProjectException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(DuplicateVersionNameException.class)
    protected final ResponseEntity<ErrorResponse> DuplicateVersionNameExceptionHandler(DuplicateVersionNameException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }
}
