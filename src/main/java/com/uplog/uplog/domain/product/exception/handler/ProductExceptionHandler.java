package com.uplog.uplog.domain.product.exception.handler;

import com.uplog.uplog.domain.product.exception.DuplicatedProductNameException;
import com.uplog.uplog.domain.product.exception.MasterException;
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
public class ProductExceptionHandler {

    @ExceptionHandler(DuplicatedProductNameException.class)
    protected final ResponseEntity<ErrorResponse> duplicatedProductNameExceptionHandler(DuplicatedProductNameException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(MasterException.class)
    protected final ResponseEntity<ErrorResponse> MasterExceptionHandler(MasterException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }
}
