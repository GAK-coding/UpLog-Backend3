//package com.uplog.uplog.domain.team.application;
//
//import com.uplog.uplog.domain.project.dao.ProjectRepository;
//import com.uplog.uplog.domain.project.model.Project;
//import com.uplog.uplog.domain.team.dao.ProjectTeamRepository;
//import com.uplog.uplog.domain.team.dto.ProjectTeamDTO;
//import com.uplog.uplog.domain.team.dto.ProjectTeamDTO.CreateProjectTeamRequest;
//import com.uplog.uplog.domain.team.dto.ProjectTeamDTO.SaveProjectTeamRequest;
//import com.uplog.uplog.domain.team.dto.ProjectTeamDTO.SimpleProjectTeamInfoDTO;
//import com.uplog.uplog.domain.team.dto.memberTeamDTO;
//import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
//import com.uplog.uplog.domain.team.model.MemberTeam;
//import com.uplog.uplog.global.exception.NotFoundIdException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.DiscriminatorValue;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//@DiscriminatorValue("project_team")
//public class ProjectTeamService {
//    private final ProjectTeamRepository projectTeamRepository;
//    private final ProjectRepository projectRepository;
//
//    private final MemberTeamService memberTeamService;
//
//    //memberTeam은 멤버 아이디를 받아서 새로 memberTeam을 만들어줘야함.
//    @Transactional
//    public SimpleProjectTeamInfoDTO createProjectTeam(CreateProjectTeamRequest createProjectTeamRequest){
//        Project project = projectRepository.findById(createProjectTeamRequest.getProjectTeamId()).orElseThrow(NotFoundIdException::new);
//        List<MemberTeam> memberTeamList = new ArrayList<>();
//        for(Long mt : createProjectTeamRequest.getMemberIdList()){
//            //멤버 팀 서비스 호출할때 존재하는 멤버인지 확인 안함 -> memberTeam서비스 로직 내에 들어가 있음 굳이 두번할 이유 없음.
//            CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
//                    .
//                    .build();
//            memberTeamService.createMemberTeam()
//        }
//    }
//
//}
