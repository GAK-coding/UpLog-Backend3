package com.uplog.uplog.domain.member.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.DuplicatedMemberException;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.exception.NotMatchPasswordException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.domain.member.dto.MemberDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
닉네임, 이름 중복 가능. 고유값은 email 하나뿐.
탈퇴시에 해당 멤버 정보는 싹 날리고 리스트나 기록에서는 닉네임대신에 알수없음(이름)이런식으로!!
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    //================================Member Create=====================================
    /*
    멤버 생성시 고려해야할 부분.
    email만 유일한 값.
    닉네임, 이름, 중복가능.
    이메일 보내기. 인증번호는 영어, 숫자, 특수문자를 조합한 6자리 인증번호.
    */

    @Transactional
    public MemberInfoDTO saveMember(SaveMemberRequest saveMemberRequest){
        //이메일이 존재하는 멤버인지 확인. 존재하면 이미 존재한다고 예외처리
        if(!memberRepository.existsByEmail(saveMemberRequest.getEmail())){
            Member member = saveMemberRequest.toMemberEntity();
            memberRepository.save(member);
            return member.toMemberInfoDTO();
        }
        else{//이미 존재하는 회원
            throw new DuplicatedMemberException("이미 존재하는 회원입니다.");
        }
    }
    //로그인
    @Transactional(readOnly = true)
    public MemberInfoDTO login(LoginRequest loginRequest){
        Member member = memberRepository.findMemberByEmail(loginRequest.getEmail()).orElseThrow(NotFoundMemberByEmailException::new);
        return member.toMemberInfoDTO();
    }

    //인증 이메일 전송


    //===========================Member Read===============================================
    //멤버 아이디로 조회
    @Transactional(readOnly = true)
    public MemberInfoDTO findMemberById(Long id){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        return member.toMemberInfoDTO();
    }

    //멤버 이메일로 조회
    @Transactional(readOnly = true)
    public MemberInfoDTO findMemberByEmail(String email){
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(NotFoundIdException::new);
        return member.toMemberInfoDTO();
    }

    //==========================Member Update====================================================
    //TODO postman으로 변경사항 잘 반영되어 들어오는지 확인할것!!!!!!! + transaction공부
    //이름 변경
    @Transactional(readOnly = true)
    public SimpleMemberInfoDTO changeMemberName(Long id,ChangeNameRequest changeNameRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        member.changeName(changeNameRequest.getNewName());
        return member.simpleMemberInfoDTO();
    }
    //닉네임 변경
    @Transactional(readOnly = true)
    public SimpleMemberInfoDTO changeMemberNickname(Long id,ChangeNicknameRequest changeNicknameRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        member.changeNickname(changeNicknameRequest.getNewNickname());
        return member.simpleMemberInfoDTO();
    }
    //비밀번호 변경
    @Transactional(readOnly = true)
    public SimpleMemberInfoDTO changeMemberPassword(Long id,ChangePasswordRequest changePasswordRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        //기존 비밀번호를 모르면 비밀번호 변경 불가
        if(member.getPassword().equals(changePasswordRequest.getPassword())) {
            member.changePassword(changePasswordRequest.getNewPassword());
            return member.simpleMemberInfoDTO();
        }
        else{
            throw new NotMatchPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }
    //position 변경(있을지 모르겠지만 혹시 모르니까)
    @Transactional(readOnly = true)
    public SimpleMemberInfoDTO changeMemberPostion(Long id, ChangePositionRequest changePositionRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        member.changePosition(changePositionRequest.getNewPosition());
        return member.simpleMemberInfoDTO();
    }


    //=========================Member Delete=================================================
    @Transactional
    public String deleteMember(Long id){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        //TODO SpringSecurity 하고 나서 getCurrentMember 하고나서 로직 수정 필요함.
        //TODO 닉네임(이름)되어있는 것을 -> (알수없음)(이름)으로 바꾸는 과정 있어야함. -> 나중에 프론트와 상의가 필요할 수도 있음.
        //TODO 비밀번호 확인
        memberRepository.delete(member);
        return "DELETE";
    }


}
