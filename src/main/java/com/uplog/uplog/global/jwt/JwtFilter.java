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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
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
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();

        String token="";
        if(httpServletRequest.getCookies()==null)
            //throw new ExpireAccessTokenException();
            token=null;
        else {
           token = Arrays.stream(httpServletRequest.getCookies())
                    .filter(c -> c.getName().equals("Access"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }
            String jwt = resolveToken(token);

        //Authentication authentication1 = tokenProvider.getAuthentication(jwt);

        if(!requestURI.equals("/members/refresh")){

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt,httpServletRequest,"ACCESS")) {

                String isLogout = (String) redisTemplate.opsForValue().get(jwt);

                if(isLogout==null) {
                    Authentication authentication = tokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
                    //System.out.println("Expire Time : "+tokenProvider.getExpiration(jwt));
                }


            } else {

                logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    private String resolveToken(String token) {
        //String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        String bearerToken = token;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(6);
        }

        return null;
    }
}
