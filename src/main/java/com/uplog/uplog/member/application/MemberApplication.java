package com.uplog.uplog.member.application;

import com.uplog.uplog.global.Exception.NotFoundIdException;
import com.uplog.uplog.member.dao.MemberRepository;
import com.uplog.uplog.member.dto.MemberDTO;
import com.uplog.uplog.member.dto.MemberDTO.MemberInfoDTO;
import com.uplog.uplog.member.dto.MemberDTO.SaveMemberRequest;
import com.uplog.uplog.member.exception.DuplicatedMemberException;
import com.uplog.uplog.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/*
닉네임, 이름 중복 가능. 고유값은 email 하나뿐.
탈퇴시에 해당 멤버 정보는 싹 날리고 리스트나 기록에서는 닉네임대신에 알수없음(이름)이런식으로!!
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberApplication {
    private final MemberRepository memberRepository;

    //================================Member Create=====================================
    /*
    멤버 생성시 고려해야할 부분.
    email만 유일한 값.
    닉네임, 이름, 중복가능.
    이메일 보내기. 인증번호는 영어, 숫자, 특수문자를 조합한 6자리 인증번호.
    */

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

    //인증 이메일 전송


    //===========================Member Read===============================================
    //멤버 아이디로 조회
    public MemberInfoDTO findMemberById(Long id){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        return member.toMemberInfoDTO();
    }

    //멤버 이메일로 조회
    public MemberInfoDTO findMemberById(String email){
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(NotFoundIdException::new);
        return member.toMemberInfoDTO();
    }

    //==========================Member Update====================================================





}
