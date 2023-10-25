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

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Object exception = request.getAttribute("exception");
        log.debug("log: exception: {} ", exception);

        /**
         * 토큰 없는 경우
         */
        if(exception == null) {
            exception=CustomHttpStatus.NON_LOGIN.getStatus();
            setResponse(response, CustomHttpStatus.NON_LOGIN.getDescription(),exception,CustomHttpStatus.NON_LOGIN);
            return;
        }
        /**
         * 토큰 만료된 경우
         */
        if(exception.equals(CustomHttpStatus.ACCESS_EXPIRED.getStatus())) {
            setResponse(response,CustomHttpStatus.ACCESS_EXPIRED.getDescription(),exception,CustomHttpStatus.ACCESS_EXPIRED);
            return;
        }

        /**
         * 토큰 값이 적절하지 않은 경우
         */
        if(exception.equals(CustomHttpStatus.INVALID_TOKEN.getStatus())) {
            setResponse(response,CustomHttpStatus.INVALID_TOKEN.getDescription(), exception,CustomHttpStatus.INVALID_TOKEN);
            return;
        }

        /**
         * 지원되지 않는 토큰
         */
        if(exception.equals(CustomHttpStatus.NON_SUPPORT_TOKEN.getStatus())) {
            setResponse(response,CustomHttpStatus.NON_SUPPORT_TOKEN.getDescription(), exception,CustomHttpStatus.NON_SUPPORT_TOKEN);
            return;
        }
        /**
         * 인자 값이 적합하지 않거나 적절하지 않은 경우
         */
        if(exception.equals(CustomHttpStatus.ILLEGAL_ARG_TOKEN.getStatus())) {
            setResponse(response,CustomHttpStatus.ILLEGAL_ARG_TOKEN.getDescription(), exception,CustomHttpStatus.ILLEGAL_ARG_TOKEN);
            return;
        }

//        if(exception.equals(CustomHttpStatus.REFRESH_EXPIRED.getStatus())){
//            setResponse(response,Refresh,exception,CustomHttpStatus.REFRESH_EXPIRED);
//            return;
//        }


    }

    /**
     * 한글 출력을 위해 getWriter() 사용
     */
    private void setResponse(HttpServletResponse response,String message,Object exception,CustomHttpStatus customHttpStatus) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(Integer.parseInt(exception.toString()));
        response.getWriter().println("{ \"httpstatus\" : \"" + customHttpStatus.toString()
                + "\", \"message\" : \""+message
                + "\", \"status\" : "+Integer.parseInt(exception.toString())
               );
    }

}
