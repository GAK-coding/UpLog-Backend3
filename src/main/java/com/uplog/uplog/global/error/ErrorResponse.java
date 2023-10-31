package com.uplog.uplog.global.error;

import com.uplog.uplog.global.jwt.CustomHttpStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {
    private HttpStatus httpStatus;
    private String message;

    @Builder
    public ErrorResponse(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
    @Getter
    public static class CustomErrorResponse{


        private CustomHttpStatus httpStatus;
        private String message;
        private int status;

        @Builder(builderMethodName = "custom")
        public CustomErrorResponse(int status, String message, CustomHttpStatus httpStatus){

            this.httpStatus =httpStatus;
            this.message =message;
            this.status =status;
        }
    }

}
