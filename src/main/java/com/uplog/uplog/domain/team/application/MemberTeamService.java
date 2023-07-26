//package com.uplog.uplog.domain.team.application;
//
//import com.uplog.uplog.domain.member.dao.MemberRepository;
//import com.uplog.uplog.domain.member.dto.MemberDTO;
//import com.uplog.uplog.domain.member.dto.MemberDTO.SaveMemberRequest;
//import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
//import com.uplog.uplog.domain.member.model.Member;
//import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
//import com.uplog.uplog.domain.team.dto.TeamDTO;
//import com.uplog.uplog.domain.team.dto.TeamDTO.SaveTeamRequest;
//import com.uplog.uplog.domain.team.dto.memberTeamDTO;
//import com.uplog.uplog.domain.team.dto.memberTeamDTO.SaveMemberTeamRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class MemberTeamService {
//    private final MemberTeamRepository memberTeamRepository;
//    private final MemberRepository memberRepository;
//
//    @Transactional
//    public String saveMemberTeam(SaveMemberTeamRequest saveMemberTeamRequest) {
//        Member master = memberRepository.findMemberByEmail(saveMemberTeamRequest.getMemberEmail()).orElseThrow(NotFoundMemberByEmailException::new);
//
//
//    }
//
//
//}