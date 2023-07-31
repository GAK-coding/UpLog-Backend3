package com.uplog.uplog.domain.team.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.PowerMemberInfoDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.SaveMemberRequest;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.SaveTeamRequest;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerListDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberTeamInfoDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.SaveMemberTeamRequest;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberTeamService {
    private final MemberTeamRepository memberTeamRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public MemberTeamInfoDTO saveMemberTeam(SaveMemberTeamRequest saveMemberTeamRequest){
        Member member = memberRepository.findMemberByEmail(saveMemberTeamRequest.getMemberEmail()).orElseThrow(NotFoundMemberByEmailException::new);
        Team team= teamRepository.findById(saveMemberTeamRequest.getTeamId()).orElseThrow(NotFoundIdException::new);
        MemberTeam memberTeam = saveMemberTeamRequest.toMemberTeam(team, member, saveMemberTeamRequest.getPowerType());

        memberTeamRepository.save(memberTeam);
        team.getMemberTeamList().add(memberTeam);

        MemberTeamInfoDTO memberTeamInfoDTO = memberTeam.toMemberTeamInfoDTO();

        return memberTeamInfoDTO;

    }

//    @Transactional(readOnly = true)
//    public MemberPowerListDTO memberPowerList(Long teamId){
//        MemberTeam master;
//        List<Long> leaderList = new ArrayList<>();
//        List<Long> clientList = new ArrayList<>();
//
//        if()
//    }

}