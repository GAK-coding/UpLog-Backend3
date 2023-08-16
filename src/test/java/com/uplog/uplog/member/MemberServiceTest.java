package com.uplog.uplog.member;

import com.uplog.uplog.domain.member.application.MemberService;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.*;
import com.uplog.uplog.domain.member.exception.DuplicatedMemberException;
import com.uplog.uplog.domain.member.exception.NotMatchPasswordException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.global.error.ErrorResponse;
import org.assertj.core.api.Assertions;
import org.hibernate.sql.Update;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    public CreateMemberRequest createMemberRequest(){
        return CreateMemberRequest.builder()
                .email("jjung@naver.com")
                .name("김짱")
                .nickname("짱")
                .password("12345678")
                .position(Position.INDIVIDUAL)
                .build();
    }

    @Test
    @DisplayName("member save 성공 테스트")
    public void successRegisterTest(){
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();

        //when
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //then
        Assertions.assertThat(memberRepository.existsById(memberInfoDTO.getId())).isEqualTo(true);
    }

    @Test
    @DisplayName("member save 실패 테스트 : 이미 존재하는 멤버")
    public void failRegisterTest(){
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();

        memberService.createMember(createMemberRequest);

        //when & then
        org.junit.jupiter.api.Assertions.assertThrows(DuplicatedMemberException.class, () -> {
            MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);
        });
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 멤버 이름 변경")
    public void successChangeNameTest(){
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //when
        UpdateMemberRequest updateNameRequest = UpdateMemberRequest.builder()
                .newName("김감자")
                .build();
        memberService.updateMember(memberInfoDTO.getId(), updateNameRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getName()).isEqualTo("김감자");
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 멤버 닉네임 변경")
    public void successChangeNicknameTest(){
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //when
        UpdateMemberRequest updateNicknameRequest = UpdateMemberRequest.builder()
                .newNickname("옹심이")
                .build();
        memberService.updateMember(memberInfoDTO.getId(), updateNicknameRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getNickname()).isEqualTo("옹심이");
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 비밀번호 변경")
    public void successChangePwdTest(){
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //when
        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .password("12345678")
                .newPassword("13579973")
                .build();
        memberService.updateMemberPassword(memberInfoDTO.getId(), updatePasswordRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getPassword()).isEqualTo("13579973");
    }

    @Test
    @DisplayName("member 수정 실패 테스트 : 비밀번호 변경 실패")
    public void failChangePwdTest(){
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //when
        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .password("12345677")
                .newPassword("13579973")
                .build();

        //then
        org.junit.jupiter.api.Assertions.assertThrows(NotMatchPasswordException.class, () ->{
            memberService.updateMemberPassword(memberInfoDTO.getId(), updatePasswordRequest);
        });
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 포지션 변경")
    public void successChangePositionTest(){
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //when
        UpdatePositionRequest updatePositionRequest = UpdatePositionRequest.builder()
                .newPosition(Position.COMPANY)
                .build();
        memberService.updateMemberPostion(memberInfoDTO.getId(), updatePositionRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getPosition()).isEqualTo(Position.COMPANY);
    }

}
