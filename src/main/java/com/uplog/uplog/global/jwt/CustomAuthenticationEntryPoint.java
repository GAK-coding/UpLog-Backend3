package com.uplog.uplog.global.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String Access="access_expiration";
    private static final String Refresh="refresh_expiration";
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Object exception = request.getAttribute("exception");
        //ErrorCode errorCode;

        log.debug("log: exception: {} ", exception);

        /**
         * 토큰 없는 경우
         */
//        if(exception == null) {
//            errorCode = ErrorCode.NON_LOGIN;
//            setResponse(response, errorCode);
//            return;
//        }

        /**
         * 토큰 만료된 경우
         */
        if(exception.equals(CustomHttpStatus.ACCESS_EXPIRED.getStatus())) {
            setResponse(response,Access,exception);
            return;
        }

//        if(exception.equals(CustomHttpStatus.REFRESH_EXPIRED.value())){
//            setResponse(response,Refresh,exception);
//            return;
//        }

        /**
         * 토큰 시그니처가 다른 경우
         */
//        if(exception.equals(ErrorCode.INVALID_TOKEN.getCode())) {
//            errorCode = ErrorCode.INVALID_TOKEN;
//            setResponse(response, errorCode);
//        }
    }

    /**
     * 한글 출력을 위해 getWriter() 사용
     */
    private void setResponse(HttpServletResponse response,String message,Object code) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(Integer.parseInt(code.toString()));
        response.getWriter().println("{ \"message\" : \"" +message
                + "\", \"code\" : \"" +Integer.parseInt(code.toString())
                + "\", \"status\" : "
                + ", \"errors\" : [ ] }");
    }

}
