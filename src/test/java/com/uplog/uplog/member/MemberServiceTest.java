package com.uplog.uplog.member;

import com.uplog.uplog.domain.member.application.MemberService;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.SaveMemberRequest;
import com.uplog.uplog.domain.member.exception.DuplicatedMemberException;
import com.uplog.uplog.domain.member.exception.NotMatchPasswordException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.global.error.ErrorResponse;
import org.assertj.core.api.Assertions;
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

    public SaveMemberRequest saveMemberRequest(){
        return SaveMemberRequest.builder()
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
        SaveMemberRequest saveMemberRequest = saveMemberRequest();

        //when
        MemberDTO.MemberInfoDTO memberInfoDTO = memberService.saveMember(saveMemberRequest);

        //then
        Assertions.assertThat(memberRepository.existsById(memberInfoDTO.getId())).isEqualTo(true);
    }

    @Test
    @DisplayName("member save 실패 테스트 : 이미 존재하는 멤버")
    public void failRegisterTest(){
        //given
        SaveMemberRequest saveMemberRequest = saveMemberRequest();

        memberService.saveMember(saveMemberRequest);

        //when & then
        org.junit.jupiter.api.Assertions.assertThrows(DuplicatedMemberException.class, () -> {
            MemberDTO.MemberInfoDTO memberInfoDTO = memberService.saveMember(saveMemberRequest);
        });
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 멤버 이름 변경")
    public void successChangeNameTest(){
        //given
        SaveMemberRequest saveMemberRequest = saveMemberRequest();
        MemberDTO.MemberInfoDTO memberInfoDTO = memberService.saveMember(saveMemberRequest);

        //when
        MemberDTO.ChangeNameRequest changeNameRequest = MemberDTO.ChangeNameRequest.builder()
                .newName("김감자")
                .build();
        memberService.changeMemberName(memberInfoDTO.getId(), changeNameRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getName()).isEqualTo("김감자");
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 멤버 닉네임 변경")
    public void successChangeNicknameTest(){
        //given
        SaveMemberRequest saveMemberRequest = saveMemberRequest();
        MemberDTO.MemberInfoDTO memberInfoDTO = memberService.saveMember(saveMemberRequest);

        //when
        MemberDTO.ChangeNicknameRequest changeNicknameRequest = MemberDTO.ChangeNicknameRequest.builder()
                .newNickname("옹심이")
                .build();
        memberService.changeMemberNickname(memberInfoDTO.getId(), changeNicknameRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getNickname()).isEqualTo("옹심이");
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 비밀번호 변경")
    public void successChangePwdTest(){
        //given
        SaveMemberRequest saveMemberRequest = saveMemberRequest();
        MemberDTO.MemberInfoDTO memberInfoDTO = memberService.saveMember(saveMemberRequest);

        //when
        MemberDTO.ChangePasswordRequest changePasswordRequest = MemberDTO.ChangePasswordRequest.builder()
                .password("12345678")
                .newPassword("13579973")
                .build();
        memberService.changeMemberPassword(memberInfoDTO.getId(), changePasswordRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getPassword()).isEqualTo("13579973");
    }

    @Test
    @DisplayName("member 수정 실패 테스트 : 비밀번호 변경 실패")
    public void failChangePwdTest(){
        //given
        SaveMemberRequest saveMemberRequest = saveMemberRequest();
        MemberDTO.MemberInfoDTO memberInfoDTO = memberService.saveMember(saveMemberRequest);

        //when
        MemberDTO.ChangePasswordRequest changePasswordRequest = MemberDTO.ChangePasswordRequest.builder()
                .password("12345677")
                .newPassword("13579973")
                .build();

        //then
        org.junit.jupiter.api.Assertions.assertThrows(NotMatchPasswordException.class, () ->{
            memberService.changeMemberPassword(memberInfoDTO.getId(), changePasswordRequest);
        });
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 포지션 변경")
    public void successChangePositionTest(){
        //given
        SaveMemberRequest saveMemberRequest = saveMemberRequest();
        MemberDTO.MemberInfoDTO memberInfoDTO = memberService.saveMember(saveMemberRequest);

        //when
        MemberDTO.ChangePositionRequest changePositionRequest = MemberDTO.ChangePositionRequest.builder()
                .newPosition(Position.COMPANY)
                .build();
        memberService.changeMemberPostion(memberInfoDTO.getId(), changePositionRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getPosition()).isEqualTo(Position.COMPANY);
    }

}
