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
    public ResponseEntity<MemberInfoDTO> createMember(@RequestBody @Validated SaveMemberRequest saveMemberRequest){
        MemberInfoDTO memberInfoDTO = memberService.saveMember(saveMemberRequest);
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
    public ResponseEntity<MemberInfoDTO> readMemberById(@PathVariable(name = "member-id") Long id){
        MemberInfoDTO memberInfoDTO = memberService.findMemberById(id);
        return new ResponseEntity<>(memberInfoDTO, HttpStatus.OK);
    }

    //이메일은 pathVariable로 넘기지 않고 DTO로 넘김
    @GetMapping(value = "/members/email-request")
    public ResponseEntity<MemberInfoDTO> readMemberByEmail(@RequestBody @Validated MailDTO.EmailRequest emailRequest){
        MemberInfoDTO memberInfoDTO = memberService.findMemberByEmail(emailRequest.getEmail());
        return ResponseEntity.ok(memberInfoDTO);
    }

    //member 전체 조회
    @GetMapping(value = "/members")
    public ResponseEntity<ReadMembersDTO> readTotalMember(){
        ReadMembersDTO readMembersDTO = memberService.findAllMembers();
        return new ResponseEntity<>(readMembersDTO, HttpStatus.OK);
    }

    //============================update===================================
    @PatchMapping(value = "/members/{member-id}/name")
    public ResponseEntity<SimpleMemberInfoDTO> changeMemberName(@PathVariable(name="member-id") Long id, @RequestBody @Validated ChangeNameRequest changeNameRequest){
        SimpleMemberInfoDTO simpleMemberInfoDTO = memberService.changeMemberName(id, changeNameRequest);
        return ResponseEntity.ok(simpleMemberInfoDTO);
        }

    @PatchMapping(value = "members/{member-id}/nickname")
    public ResponseEntity<SimpleMemberInfoDTO> changeMemberNickname(@PathVariable(name = "member-id") Long id, @RequestBody @Validated ChangeNicknameRequest changeNicknameRequest){
        SimpleMemberInfoDTO simpleMemberInfoDTO = memberService.changeMemberNickname(id, changeNicknameRequest);
        return ResponseEntity.ok(simpleMemberInfoDTO);
    }

    @PatchMapping(value = "members/{member-id}/password")
    public ResponseEntity<SimpleMemberInfoDTO> changeMemberPassword(@PathVariable(name = "member-id") Long id, @RequestBody @Validated ChangePasswordRequest changePasswordRequest){
        SimpleMemberInfoDTO simpleMemberInfoDTO = memberService.changeMemberPassword(id, changePasswordRequest);
        return ResponseEntity.ok(simpleMemberInfoDTO);
    }

    @PatchMapping(value = "members/{member-id}/position")
    public ResponseEntity<SimpleMemberInfoDTO> changeMemberPosition(@PathVariable(name = "member-id") Long id, @RequestBody @Validated ChangePositionRequest changePositionRequest){
        SimpleMemberInfoDTO simpleMemberInfoDTO = memberService.changeMemberPostion(id, changePositionRequest);
        return ResponseEntity.ok(simpleMemberInfoDTO);
    }

    //========================delete==================
    @DeleteMapping(value = "members/{member-id}")
    public ResponseEntity<String> deleteMember(@PathVariable(name = "member-id") Long id, @RequestBody @Validated DeleteMemberRequest deleteMemberRequest){
        String m = memberService.deleteMember(id, deleteMemberRequest);
        return ResponseEntity.ok(m);
    }





}
