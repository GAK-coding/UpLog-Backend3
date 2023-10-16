package com.uplog.uplog.global.jwt;

//import com.uplog.uplog.domain.member.dao.RedisDao;
import com.uplog.uplog.domain.member.dao.RedisDao;
import com.uplog.uplog.domain.member.dao.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {


    private final RefreshTokenRepository refreshTokenRepository;
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final String Access_token="ACCESS";
    private static final String Refresh_token="REFRESH";
    private final String secret;
    private long AccessTokenValidityInMilliseconds =Duration.ofMinutes(1).toMillis();//만료시간 30분
    //Duration.ofMinutes(30).toMillis()
    private long RefreshTokenValidityInMilliseconds=Duration.ofMinutes(1).toMillis(); //만료시간 2주

    private final RedisDao redisDao;

    private Key key;
    private long seconds=10000;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            RefreshTokenRepository refreshTokenRepository,
            RedisDao redisDao) {
        this.secret = secret;
        this.refreshTokenRepository=refreshTokenRepository;
        this.redisDao=redisDao;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public HttpServletResponse createToken(HttpServletResponse response,Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        //long now = (new Date()).getTime();
        Date validity = new Date(System.currentTimeMillis() + this.AccessTokenValidityInMilliseconds);

        String accessToken=Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();

        String refreshToken=Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(new Date(System.currentTimeMillis()+ RefreshTokenValidityInMilliseconds))
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        System.out.println("REfresh: "+refreshToken);
        redisDao.setValues("RT:"+authentication.getName(),refreshToken,Duration.ofSeconds(seconds));

        Cookie cookie_access= new Cookie("Access","Bearer"+accessToken);
        Cookie cookie_refresh= new Cookie("Refresh","Bearer"+refreshToken);


        cookie_access.setPath("/");
        cookie_access.setHttpOnly(true);
        cookie_access.setMaxAge((int)1200);

        cookie_refresh.setPath("/");
        cookie_refresh.setHttpOnly(true);
        cookie_refresh.setMaxAge((int)1200);
        response.addCookie(cookie_access);
        response.addCookie(cookie_refresh);

        return response;
//        return TokenDTO.builder()
//                .grantType(BEARER_TYPE)
//                .accessToken(accessToken)
//                .accessTokenExpiresIn(validity.getTime())
//                .refreshToken(refreshToken)
//                .build();
    }

    public Authentication getAuthentication(String token) {

        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    public Long getExpiration(String token){

        Date date=Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getExpiration();
        Long now = new Date().getTime();
        return (date.getTime() - now);



    }

    public boolean validateToken(String token, HttpServletRequest request,String name) {
        try {

            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
            System.out.println("print: "+CustomHttpStatus.ACCESS_EXPIRED.value()+" "+CustomHttpStatus.ACCESS_EXPIRED +" ");
            if(name.equals(Access_token)) {
                request.setAttribute("exception", CustomHttpStatus.ACCESS_EXPIRED.value());
            }
            else if(name.equals(Refresh_token))
            {
                request.setAttribute("exception",CustomHttpStatus.REFRESH_EXPIRED.value());
            }

        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
