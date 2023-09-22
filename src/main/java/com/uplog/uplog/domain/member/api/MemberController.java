package com.uplog.uplog.domain.member.api;

import com.uplog.uplog.domain.member.application.CustomUserDetailsService;
import com.uplog.uplog.domain.member.application.MemberService;
//import com.uplog.uplog.domain.member.dao.RefreshTokenRepository;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dao.RefreshTokenRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO.*;
import com.uplog.uplog.domain.member.dto.TokenDTO;
import com.uplog.uplog.domain.member.dto.TokenRequestDTO;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
//import com.uplog.uplog.domain.member.model.RefreshToken;
import com.uplog.uplog.global.jwt.JwtFilter;
import com.uplog.uplog.global.jwt.TokenProvider;
import com.uplog.uplog.global.mail.MailDTO;
import com.uplog.uplog.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Optional;

//TODO API 다시 경로 다시 설정!!!! 지금은 급해서 이렇게 올림
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private int AccessTokenValidityInMilliseconds = (int)Duration.ofMinutes(10).toSeconds();//만료시간 30분
    private int RefreshTokenValidityInMilliseconds=(int)Duration.ofDays(14).toSeconds(); //만료시간 2주
    //=============================create=======================================
    @PostMapping(value = "/members")
    public ResponseEntity<MemberInfoDTO> createMember(@RequestBody @Validated CreateMemberRequest createMemberRequest){
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);
        return new ResponseEntity<>(memberInfoDTO, HttpStatus.CREATED);
    }

    //로그인
    //security 로직 추가
    @PostMapping(value = "/members/login")
    public ResponseEntity longin(HttpServletResponse response,@RequestBody @Validated LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());

        Authentication authentication=authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        response =tokenProvider.createToken(response,authentication);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/members/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response,
                             @RequestBody @Validated TokenRequestDTO tokenRequestDTO) {

        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        SecurityContextHolder.clearContext();
        memberService.logout(tokenRequestDTO);

        return "logout success";
    }
    //리프레시 토큰 만료 시
    @PostMapping("/members/refresh")
    public ResponseEntity refresh(HttpServletRequest request,HttpServletResponse response,@RequestBody TokenRequestDTO tokenRequestDto) {
        //return ResponseEntity.ok(memberService.refresh(request,response,tokenRequestDto));
        response=memberService.refresh(request,response,tokenRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 토큰 Role user,admin
    @GetMapping("/members/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Member> getMyUserInfo(){
        Optional<String> d= SecurityUtil.getCurrentUsername();
        return ResponseEntity.ok(memberService.getMyUserWithAuthorities().get());
    }
    // 토큰 Role admin
    @GetMapping("/members/user/{user-email}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Member> getMyUserInfo(@PathVariable String email){
        return ResponseEntity.ok(memberService.getUserWithAuthorities(email).get());
    }


    //=============================read======================================
    @GetMapping(value = "/members")
    public ResponseEntity<MemberInfoDTO> findMemberById(){
        Long memberId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        MemberInfoDTO memberInfoDTO = memberService.findMemberById(memberId);
        return new ResponseEntity<>(memberInfoDTO, HttpStatus.OK);
    }

    //이메일은 pathVariable로 넘기지 않고 DTO로 넘김
    @GetMapping(value = "/members/email-request")
    public ResponseEntity<MemberInfoDTO> findMemberByEmail(@RequestBody @Validated MailDTO.EmailRequest emailRequest){
        MemberInfoDTO memberInfoDTO = memberService.findMemberByEmail(emailRequest.getEmail());
        return ResponseEntity.ok(memberInfoDTO);
    }

    //member 전체 조회
    @GetMapping(value = "/members/all")
    public ResponseEntity<FindMembersDTO> findTotalMember(){
        FindMembersDTO findMembersDTO = memberService.findAllMembers();
        return new ResponseEntity<>(findMembersDTO, HttpStatus.OK);
    }

    //============================update===================================
    @PatchMapping(value = "/members/information")
    public ResponseEntity<VerySimpleMemberInfoDTO> updateMemberName(@RequestBody @Validated UpdateMemberRequest updateMemberRequest){
        Long memberId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        VerySimpleMemberInfoDTO verySimpleMemberInfoDTO = memberService.updateMember(memberId, updateMemberRequest);
        return ResponseEntity.ok(verySimpleMemberInfoDTO);
    }

    @PatchMapping(value = "members/password")
    public ResponseEntity<SimpleMemberInfoDTO> udpateMemberPassword(@RequestBody @Validated UpdatePasswordRequest updatePasswordRequest){
        Long memberId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        SimpleMemberInfoDTO simpleMemberInfoDTO = memberService.updateMemberPassword(memberId, updatePasswordRequest);
        return ResponseEntity.ok(simpleMemberInfoDTO);
    }

    @PatchMapping(value = "members/position")
    public ResponseEntity<SimpleMemberInfoDTO> updateMemberPosition(@RequestBody @Validated UpdatePositionRequest updatePositionRequest){
        Long memberId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        SimpleMemberInfoDTO simpleMemberInfoDTO = memberService.updateMemberPostion(memberId, updatePositionRequest);
        return ResponseEntity.ok(simpleMemberInfoDTO);
    }

    //========================delete==================
    @DeleteMapping(value = "members")
    public ResponseEntity<String> deleteMember(@RequestBody @Validated DeleteMemberRequest deleteMemberRequest){
        Long memberId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        String m = memberService.deleteMember(memberId, deleteMemberRequest);
        return ResponseEntity.ok(m);
    }





}