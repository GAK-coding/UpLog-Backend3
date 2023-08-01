package com.uplog.uplog.domain.member.api;

import com.uplog.uplog.domain.member.application.MemberService;
import com.uplog.uplog.domain.member.dto.MemberDTO.*;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.global.mail.MailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//TODO API 다시 경로 다시 설정!!!! 지금은 급해서 이렇게 올림
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class MemberController {
    private final MemberService memberService;

    //=============================create=======================================
    @PostMapping(value = "/members")
    public ResponseEntity<MemberInfoDTO> createMember(@RequestBody @Validated CreateMemberRequest createMemberRequest){
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);
        return new ResponseEntity<>(memberInfoDTO, HttpStatus.CREATED);
    }

    //로그인
    @PostMapping(value = "/members/login")
    public ResponseEntity<MemberInfoDTO> longin(@RequestBody @Validated LoginRequest loginRequest){
        MemberInfoDTO memberInfoDTO = memberService.login(loginRequest);
        return ResponseEntity.ok(memberInfoDTO);
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
