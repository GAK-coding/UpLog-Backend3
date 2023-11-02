package com.uplog.uplog.domain.team.exception.handler;

import com.uplog.uplog.domain.team.exception.CanNotDeleteMemberToTeam;
import com.uplog.uplog.domain.team.exception.CanNotDeleteTeamException;
import com.uplog.uplog.domain.team.exception.NotFoundMemberInTeamException;
import com.uplog.uplog.global.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class TeamExceptionHandler {

    @ExceptionHandler(NotFoundMemberInTeamException.class)
    protected final ResponseEntity<ErrorResponse> notFoundMemberInTeamExceptionHandler(NotFoundMemberInTeamException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();

        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(CanNotDeleteMemberToTeam.class)
    protected final ResponseEntity<ErrorResponse> canNotDeleteMemberToTeam(CanNotDeleteMemberToTeam e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(e.getMessage())
                .build();

        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(CanNotDeleteTeamException.class)
    protected final ResponseEntity<ErrorResponse> canNotDeleteTeamExceptionHandler(CanNotDeleteTeamException e, WebRequest webRequest){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(errorResponse);
    }
}
