package com.uplog.uplog.domain.team.application;

import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.CreateTeamRequest;
import com.uplog.uplog.domain.team.dto.TeamDTO.TeamInfoDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.mail.MailDTO;
import com.uplog.uplog.global.mail.MailDTO.EmailRequest;
import com.uplog.uplog.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.uplog.uplog.global.mail.MailDTO.EmailRequest.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final ProductRepository productRepository;
    private final MemberTeamService memberTeamService;
    private final MailService mailService;

    @Transactional
    public Long saveTeam(CreateTeamRequest createTeamRequest) throws Exception {
        //제품을 만드는 순간 팀이 만들어짐(제품 만들 때 마스터와 제품의 이름 받음)-> 즉 팀은 기업이 생성하는 것 -> 그 다음이 마스터가 리더 초대.
        //팀은 제품 생성때 한번만 생성됨. 그뒤에는 따로 호출될 일이 없음.
        //Product product = productRepository.findById(saveTeamRequest.getProductId()).orElseThrow(NotFoundIdException::new);
        //MemberTeam -> team을 만든 다음에 인원추가해야함.
        //MemberTeam memberTeam = memberTeamService.saveMemberTeam()
        Team team = createTeamRequest.toEntity();
        teamRepository.save(team);


        //팀이생성될 때 들어가는 멤버는 무조건 마스터임.
        CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
                .teamId(team.getId())
                .memberEmail(createTeamRequest.getMemberEmail())
                .powerType(PowerType.MASTER)
                .link(createTeamRequest.getLink())
                .build();
        memberTeamService.createMemberTeam(createMemberTeamRequest);
        EmailRequest emailRequest = builder()
                .email(createMemberTeamRequest.getMemberEmail())
                .type(2)
                .powerType(PowerType.MASTER)
                .link(createTeamRequest.getLink())
                .build();
        mailService.sendSimpleMessage(emailRequest);
        //마스터 멤버 호출

        //TeamInfoDTO teamInfoDTO = team.toTeamInfoDTO();
        return team.getId();
    }

    @Transactional
    public String deleteTeam(){
        return "DELETE";
    }

}
