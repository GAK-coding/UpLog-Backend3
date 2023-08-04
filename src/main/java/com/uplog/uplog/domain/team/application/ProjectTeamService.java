package com.uplog.uplog.domain.team.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.ProjectTeamRepository;
import com.uplog.uplog.domain.team.dto.ProjectTeamDTO;
import com.uplog.uplog.domain.team.dto.ProjectTeamDTO.CreateProjectTeamRequest;
import com.uplog.uplog.domain.team.dto.ProjectTeamDTO.SimpleProjectTeamInfoDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.UpdateMemberPowerTypeRequest;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.ProjectTeam;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.DiscriminatorValue;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@DiscriminatorValue("project_team")
public class ProjectTeamService {
    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectRepository projectRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final MemberRepository memberRepository;

    private final MemberTeamService memberTeamService;

    //memberTeam은 멤버 아이디를 받아서 새로 memberTeam을 만들어줘야함.
    //제일 처음에는 프로덕트에서 그대로 받아온 멤버가 들어감.
    @Transactional
    public Long createProjectTeam(Long projectId, CreateProjectTeamRequest createProjectTeamRequest) throws Exception {
        Project project = projectRepository.findById(createProjectTeamRequest.getProjectId()).orElseThrow(NotFoundIdException::new);
        List<MemberTeam> memberTeamList = new ArrayList<>();
        for(Long mt : createProjectTeamRequest.getMemberIdList()){
            //여기서 조회하는게 최선. memberTeam에서는 프로덕트의 아이디를 알 수 없음. 단방향이라서.
            MemberTeam memberTeam = memberTeamRepository.findMemberTeamByMemberAndTeamId(mt, project.getProduct().getId()).orElseThrow(NotFoundIdException::new);
            //멤버 팀 서비스 호출할때 존재하는 멤버인지 확인 안함 -> memberTeam서비스 로직 내에 들어가 있음 굳이 두번할 이유 없음.
            //이메일, 아이디로 각각 프로젝트. 프로덕트 구분하기
            CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
                    .memberId(mt)
                    .teamId(projectId)
                    .powerType(memberTeam.getPowerType())
                    .mailType(2)
                    .build();
            Long memberTeamId = memberTeamService.createMemberTeam(createMemberTeamRequest);
            MemberTeam memberProjectTeam = memberTeamRepository.findMemberTeamById(memberTeamId).orElseThrow(NotFoundIdException::new);
            memberTeamList.add(memberProjectTeam);
        }
        //TODO 프로덕트 팀에서 전체 멤버가 프로젝트 멤버가 되는게 루트임. -> 이부분은 프로젝트생성시 들어가는게 맞을 듯. 널값이 들어가면 무조건 널포인트 예외 터짐.
        if(projectTeamRepository.findProjectTeamsByProjectId(projectId)==null){
            ProjectTeam projectTeam = createProjectTeamRequest.toEntity(memberTeamList,project,null);
            projectTeamRepository.save(projectTeam);
            return projectTeam.getId();
        }
        else {
            //부모팀을 넣어주지 았다면 자동으로 루트로 들어가게함. -> 자식이 있는 거임.
            if(createProjectTeamRequest.getParentProjectTeamId()==null){
                ProjectTeam parentProjectTeam = projectTeamRepository.findByProjectIdAndName(projectId, project.getProduct().getName()).orElseThrow(NotFoundIdException::new);
                ProjectTeam projectTeam = createProjectTeamRequest.toEntity(memberTeamList, project, parentProjectTeam);
                projectTeamRepository.save(projectTeam);
                parentProjectTeam.getChildTeamList().add(projectTeam);
                return projectTeam.getId();
            }
            else {
                ProjectTeam parentProjectTeam = projectTeamRepository.findById(createProjectTeamRequest.getParentProjectTeamId()).orElseThrow(NotFoundIdException::new);
                ProjectTeam projectTeam = createProjectTeamRequest.toEntity(memberTeamList, project, parentProjectTeam);
                projectTeamRepository.save(projectTeam);
                parentProjectTeam.getChildTeamList().add(projectTeam);
                return projectTeam.getId();
            }
        }
    }

//    @Transactional
//    public Long updateMemberPowerType(UpdateMemberPowerTypeRequest updateMemberPowerTypeRequest){
//        Member member = memberRepository.findMemberById(updateMemberPowerTypeRequest.getMemberId()).orElseThrow(NotFoundIdException::new);
//        ProjectTeam projectTeam = projectTeamRepository.findProjectTeamBy
//    }
}
