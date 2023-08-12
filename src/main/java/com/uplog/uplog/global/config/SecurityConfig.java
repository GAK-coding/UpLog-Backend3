package com.uplog.uplog.global.config;

import com.uplog.uplog.global.jwt.JwtAccessDeniedHandler;
import com.uplog.uplog.global.jwt.JwtAuthenticationEntryPoint;
import com.uplog.uplog.global.jwt.JwtSecurityConfig;
import com.uplog.uplog.global.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity //기본적인 웹 보안을 활성화 하겠다는 어노테이션.
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final RedisTemplate redisTemplate;
    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler,
            RedisTemplate redisTemplate
    ){
        this.tokenProvider=tokenProvider;
        this.jwtAuthenticationEntryPoint=jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler=jwtAccessDeniedHandler;
        this.redisTemplate=redisTemplate;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring()
                .antMatchers("/favicon.ico","/swagger-ui.html","/swagger-ui/**",
                        "/swagger-resources/**","/api-docs/**");
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //token 사용하는 방식이기 때문에 csrf을 disable
        http
                .csrf().disable()
                .cors(cors -> cors.disable());
        ;

        http
                .authorizeRequests()
                .antMatchers("/members/**").permitAll() // 해당 Request는 허용한다.
                .antMatchers("/api/v2/**","/health","/swagger-ui.html","/swagger/**",
                        "/swagger-resources/**","/webjars/**","/api-docs/**",
                        "/swagger-ui/**","/api/login").permitAll()
                .anyRequest().authenticated();

        http
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler);

        //세션을 사용하지 않기 떄문에 STATELESS로 설정
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .apply(new JwtSecurityConfig(tokenProvider,redisTemplate));

        http
                .logout()
                .logoutSuccessUrl("/") // 로그아웃 성공시 리다이렉트 주소
                .invalidateHttpSession(true); // 로그아웃 이후 세션 전체 삭제 여부
        return http.build();
    }

}