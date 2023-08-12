package com.uplog.uplog.domain.team.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.PowerMemberInfoDTO;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerListDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberTeamInfoDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.UpdateMemberPowerTypeRequest;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.mail.MailDTO;
import com.uplog.uplog.global.mail.MailDTO.EmailRequest;
import com.uplog.uplog.global.mail.MailService;
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

    private final MailService mailService;

    @Transactional
    public Long createMemberTeam(CreateMemberTeamRequest createMemberTeamRequest) throws Exception {
        Member member = memberRepository.findMemberByIdOrEmail(createMemberTeamRequest.getMemberId(), createMemberTeamRequest.getMemberEmail()).orElseThrow(NotFoundMemberByEmailException::new);
        Team team = teamRepository.findById(createMemberTeamRequest.getTeamId()).orElseThrow(NotFoundIdException::new);
        MemberTeam memberTeam = createMemberTeamRequest.toMemberTeam(team, member, createMemberTeamRequest.getPowerType());

        memberTeamRepository.save(memberTeam);
        //TODO Null point 확인
//        log.info(team.getName());
//        log.info(team.getMemberTeamList()+"null?");
//        log.info(memberTeam+"what null");
        team.getMemberTeamList().add(memberTeam);

        //MemberTeamInfoDTO memberTeamInfoDTO = memberTeam.toMemberTeamInfoDTO();
        EmailRequest emailRequest = EmailRequest.builder()
                .email(member.getEmail())
                .type(2)
                .link(createMemberTeamRequest.getLink())
                .powerType(createMemberTeamRequest.getPowerType())
                .build();
        mailService.sendSimpleMessage(emailRequest);

        return memberTeam.getId();


    }

//    @Transactional(readOnly = true)
//    public MemberPowerListDTO memberPowerList(Long teamId){
//        MemberTeam master;
//        List<Long> leaderList = new ArrayList<>();
//        List<Long> clientList = new ArrayList<>();
//
//        if()
//    }

    @Transactional
    public Long updateMemberPowerType(UpdateMemberPowerTypeRequest updateMemberPowerTypeRequest) {
        MemberTeam memberTeam = memberTeamRepository.findMemberTeamByMemberAndTeamId(updateMemberPowerTypeRequest.getMemberId(), updateMemberPowerTypeRequest.getTeamId()).orElseThrow(NotFoundIdException::new);
        memberTeam.updatePowerType(updateMemberPowerTypeRequest.getNewPowerType());
        return memberTeam.getId();
    }

}