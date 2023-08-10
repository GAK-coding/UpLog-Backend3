package com.uplog.uplog.domain.task.exception.handler;

import com.uplog.uplog.domain.task.exception.NotFoundTaskByIdException;
import com.uplog.uplog.global.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

public class TaskExceptionHandler {
    @ExceptionHandler(NotFoundTaskByIdException.class)
    protected final ResponseEntity<ErrorResponse> handleNotFoundTaskByIdException(NotFoundTaskByIdException e, WebRequest webRequest) {
        final ErrorResponse errorResponse= ErrorResponse.builder()
                .httpStatus((HttpStatus.NOT_FOUND))
                .message(e.getMessage())
                .build();

        return ResponseEntity.ok(errorResponse);
    }

}
