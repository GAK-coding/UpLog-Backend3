package com.uplog.uplog.global.jwt;

//import com.uplog.uplog.domain.member.dao.RedisDao;
import com.uplog.uplog.domain.member.dao.RedisDao;
import com.uplog.uplog.domain.member.dao.RefreshTokenRepository;
import com.uplog.uplog.domain.member.dto.TokenDTO;
import com.uplog.uplog.global.error.ErrorResponse;
import com.uplog.uplog.global.exception.ExpireAccessTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.ErrorMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

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
    private final String secret;
    private long AccessTokenValidityInMilliseconds =Duration.ofMinutes(30).toMillis();//만료시간 30분
    //Duration.ofMinutes(30).toMillis()
    private long RefreshTokenValidityInMilliseconds=Duration.ofDays(14).toMillis(); //만료시간 2주

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

    public TokenDTO createToken(Authentication authentication) {
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



        return TokenDTO.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(validity.getTime())
                .refreshToken(refreshToken)
                .build();
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

    public boolean validateToken(String token) {
        try {

            String jwt= String.valueOf(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token));
            System.out.println("만료 시간!: "+token);
            System.out.println("만료 시간!234: "+ jwt);
            logger.debug("만료 시간: "+getExpiration(token));
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}