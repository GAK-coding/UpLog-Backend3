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
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    //=============================create=======================================
    @PostMapping(value = "/members")
    public ResponseEntity<MemberInfoDTO> createMember(@RequestBody @Validated CreateMemberRequest createMemberRequest){
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);
        return new ResponseEntity<>(memberInfoDTO, HttpStatus.CREATED);
    }


    //로그인
    //security 로직 추가
    @PostMapping(value = "/members/login")
    public ResponseEntity<MemberInfoDTO> longin(@RequestBody @Validated LoginRequest loginRequest){

        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());

        Authentication authentication=authenticationManagerBuilder.getObject().authenticate(authenticationToken);


        UserDetails userDetails=customUserDetailsService.loadUserByUsername(loginRequest.getEmail());

        //String password=(String)authentication.getCredentials();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDTO tokenDTO =tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER,tokenDTO.getGrantType()+tokenDTO.getAccessToken());
        MemberInfoDTO memberInfoDTO = memberService.login(loginRequest);
        memberInfoDTO.addTokenToMemberInfoDTO(tokenDTO.getAccessToken(),tokenDTO.getRefreshToken());

        return new ResponseEntity<>(memberInfoDTO,httpHeaders,HttpStatus.OK);
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
    public ResponseEntity<TokenDTO> refresh(@RequestBody TokenRequestDTO tokenRequestDto) {
        return ResponseEntity.ok(memberService.refresh(tokenRequestDto));
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
    @GetMapping(value = "/members/{member-id}")
    public ResponseEntity<MemberInfoDTO> findMemberById(@PathVariable(name = "member-id") Long id){
        MemberInfoDTO memberInfoDTO = memberService.findMemberById(id);
        return new ResponseEntity<>(memberInfoDTO, HttpStatus.OK);
    }

    //이메일은 pathVariable로 넘기지 않고 DTO로 넘김
    @GetMapping(value = "/members/email-request")
    public ResponseEntity<MemberInfoDTO> findMemberByEmail(@RequestBody @Validated MailDTO.EmailRequest emailRequest){
        MemberInfoDTO memberInfoDTO = memberService.findMemberByEmail(emailRequest.getEmail());
        return ResponseEntity.ok(memberInfoDTO);
    }

    //member 전체 조회
    @GetMapping(value = "/members")
    public ResponseEntity<FindMembersDTO> findTotalMember(){
        FindMembersDTO findMembersDTO = memberService.findAllMembers();
        return new ResponseEntity<>(findMembersDTO, HttpStatus.OK);
    }

    //============================update===================================
    @PatchMapping(value = "/members/{member-id}/name")
    public ResponseEntity<SimpleMemberInfoDTO> updateMemberName(@PathVariable(name="member-id") Long id, @RequestBody @Validated UpdateNameRequest updateNameRequest){
        SimpleMemberInfoDTO simpleMemberInfoDTO = memberService.updateMemberName(id, updateNameRequest);
        return ResponseEntity.ok(simpleMemberInfoDTO);
    }

    @PatchMapping(value = "members/{member-id}/nickname")
    public ResponseEntity<SimpleMemberInfoDTO> updateMemberNickname(@PathVariable(name = "member-id") Long id, @RequestBody @Validated UpdateNicknameRequest updateNicknameRequest){
        SimpleMemberInfoDTO simpleMemberInfoDTO = memberService.updateMemberNickname(id, updateNicknameRequest);
        return ResponseEntity.ok(simpleMemberInfoDTO);
    }

    @PatchMapping(value = "members/{member-id}/password")
    public ResponseEntity<SimpleMemberInfoDTO> udpateMemberPassword(@PathVariable(name = "member-id") Long id, @RequestBody @Validated UpdatePasswordRequest updatePasswordRequest){
        SimpleMemberInfoDTO simpleMemberInfoDTO = memberService.updateMemberPassword(id, updatePasswordRequest);
        return ResponseEntity.ok(simpleMemberInfoDTO);
    }

    @PatchMapping(value = "members/{member-id}/position")
    public ResponseEntity<SimpleMemberInfoDTO> updateMemberPosition(@PathVariable(name = "member-id") Long id, @RequestBody @Validated UpdatePositionRequest updatePositionRequest){
        SimpleMemberInfoDTO simpleMemberInfoDTO = memberService.updateMemberPostion(id, updatePositionRequest);
        return ResponseEntity.ok(simpleMemberInfoDTO);
    }

    //========================delete==================
    @DeleteMapping(value = "members/{member-id}")
    public ResponseEntity<String> deleteMember(@PathVariable(name = "member-id") Long id, @RequestBody @Validated DeleteMemberRequest deleteMemberRequest){
        String m = memberService.deleteMember(id, deleteMemberRequest);
        return ResponseEntity.ok(m);
    }





}
