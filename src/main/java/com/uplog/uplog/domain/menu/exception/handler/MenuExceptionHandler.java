package com.uplog.uplog.domain.menu.exception.handler;

import com.uplog.uplog.domain.menu.exception.*;
import com.uplog.uplog.global.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class MenuExceptionHandler {
    @ExceptionHandler(DuplicatedMenuNameInProjectException.class)
    public final ResponseEntity<ErrorResponse> handleDuplicatedMenuNameInProjectException(DuplicatedMenuNameInProjectException ex, WebRequest webRequest) {
        final ErrorResponse errorResponse=ErrorResponse.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(ExceededMaxMenuCountException.class)
    public final ResponseEntity<ErrorResponse> handleExceededMaxMenuCountException(ExceededMaxMenuCountException ex,WebRequest webRequest) {
        final ErrorResponse errorResponse=ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(MenuUpdateNotAllowedException.class)
    public final ResponseEntity<ErrorResponse> handleMenuUpdateNotAllowedException(MenuUpdateNotAllowedException ex,WebRequest webRequest) {
        final ErrorResponse errorResponse=ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }
}
