package com.uplog.uplog.global.jwt;

import com.uplog.uplog.domain.member.dao.RedisDao;
import com.uplog.uplog.global.exception.ExpireAccessTokenException;
import com.uplog.uplog.global.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.GenericFilterBean;


import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final RedisTemplate redisTemplate;
    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider,RedisTemplate redisTemplate) {

        this.tokenProvider = tokenProvider;
        this.redisTemplate=redisTemplate;

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("login8: "+SecurityUtil.getCurrentUsername());
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);
        System.out.println("login test: "+ jwt);
        //Authentication authentication1 = tokenProvider.getAuthentication(jwt);
        String requestURI = httpServletRequest.getRequestURI();
        System.out.println("login9: "+SecurityUtil.getCurrentUsername());
        System.out.println("login1241: "+jwt);
//        if(requestURI.equals("/members/login")&&jwt!=null){
//            tokenProvider.validateToken(jwt);
//        }
        if(!requestURI.equals("/members/refresh")){
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                System.out.println("login10: "+SecurityUtil.getCurrentUsername());

                String isLogout = (String) redisTemplate.opsForValue().get(jwt);
                System.out.println("login101: "+isLogout);
                if(isLogout==null) {
                    Authentication authentication = tokenProvider.getAuthentication(jwt);
                    System.out.println("login11: "+SecurityUtil.getCurrentUsername());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("login12: "+SecurityUtil.getCurrentUsername());
                    logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
                    System.out.println("Expire Time : "+tokenProvider.getExpiration(jwt));
                }


            } else {

                logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    private String resolveToken(HttpServletRequest request) {
        System.out.println("login13: "+SecurityUtil.getCurrentUsername());
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        System.out.println("login14: "+SecurityUtil.getCurrentUsername());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            System.out.println("login15: "+SecurityUtil.getCurrentUsername());
            return bearerToken.substring(7);
        }

        return null;
    }
}
