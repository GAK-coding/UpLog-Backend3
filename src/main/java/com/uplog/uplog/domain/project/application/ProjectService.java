package com.uplog.uplog.domain.project.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.changedIssue.exception.notFoundPowerByMemberException;
import com.uplog.uplog.domain.comment.exception.MemberAuthorizedException;
import com.uplog.uplog.domain.menu.application.MenuService;
import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.menu.dto.MenuDTO.SimpleMenuInfoDTO;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.exception.DuplicateVersionNameException;
import com.uplog.uplog.domain.project.exception.ExistProcessProjectExeption;
import com.uplog.uplog.domain.project.exception.NotFoundProjectException;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.project.model.QProject;
import com.uplog.uplog.domain.team.application.MemberTeamService;
import com.uplog.uplog.domain.team.application.ProjectTeamService;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.ProjectTeamRepository;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.ProjectTeam;
import com.uplog.uplog.domain.team.model.QMemberTeam;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import java.util.ArrayList;
import java.util.List;

import static com.uplog.uplog.domain.project.dto.ProjectDTO.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ProjectRepository projectRepository;
    private final ProductRepository productRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final ProjectTeamRepository projectTeamRepository;

    private final MenuService menuService;
    private final ProjectTeamService projectTeamService;
    private final MemberTeamService memberTeamService;


    //TODO 여기서 member(group) 처리해야 하나?
    //그룹처리 메뉴에 대한 추가도 필요함.
    @Transactional
    public ProjectInfoDTO createProject(CreateProjectRequest createProjectRequest, Long productId) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProjectException(productId));

        //진행 중 프로젝트 있을 시 제한
        checkProcessProject(productId);
        //TODO Member가 마스터인지 확인해야함. -> 스프링 시큐리티!

        //새로 생성 되는 프로젝트는 진행 중 고정 값.
        Project project = createProjectRequest.toEntity(product);
        projectRepository.save(project);

        //menu 생성
        List<SimpleMenuInfoDTO> simpleMenuInfoDTOList = menuService.createDefaultMenu(project.getId());

        //생성되고 바로 전체 프로젝트팀 만들어져야함.
        //프로덕트 멤버 가져오기
        /*
         private List<Long> memberIdList;
        private String name;
        private Long projectId;
        private Long parentProjectTeamId;
         */
        //TODO Link 부분 상의
        List<MemberTeam> memberTeamList = new ArrayList<>();
        for (MemberTeam mt : product.getTeam().getMemberTeamList()) {

            CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
                    .memberId(mt.getMember().getId())
                    .teamId(project.getId())
                    .powerType(mt.getPowerType() == PowerType.MASTER || mt.getPowerType() == PowerType.LEADER ? PowerType.LEADER : mt.getPowerType())
                    .link("")
                    .build();

            Long memberTeamId = memberTeamService.createMemberTeam(createMemberTeamRequest);
            MemberTeam memberProjectTeam = memberTeamRepository.findMemberTeamById(memberTeamId).orElseThrow(NotFoundIdException::new);
            memberTeamList.add(memberProjectTeam);
        }
        ProjectTeam projectTeam = ProjectTeam.projectTeamBuilder()
                .project(project)
                .parentTeam(null)
                .memberTeamList(memberTeamList)
                .name(createProjectRequest.getVersion())
                .build();
        projectTeamRepository.save(projectTeam);

        List<Long> projectTeamIdList = new ArrayList<>();
        projectTeamIdList.add(projectTeam.getId());

//        project.getProjectTeamList().add(projectTeam);


        return project.toProjectInfoDTO(simpleMenuInfoDTOList, projectTeamIdList);
    }

    @Transactional(readOnly = true)
    public requestProjectAllInfo readProject(Long projectId, Long memberId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException(projectId));


        PowerType powerType = checkMemberType(memberId);


        requestProjectAllInfo requestProjectAllInfo = project.toRequestProjectAllInfo(powerType,
                project.getProduct().getName(),
                project.getProduct().getCompany());

        return requestProjectAllInfo;
    }

    @Transactional(readOnly = true)
    public requestProjectInfo readProjectSimple(Long projectId, Long memberId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException(projectId));


        PowerType powerType = checkMemberType(memberId);

        requestProjectInfo requestProjectInfo = project.toRequestProjectInfo(powerType,
                project.getProduct().getName(),
                project.getProduct().getCompany());

        return requestProjectInfo;


    }

    @Transactional
    public UpdateProjectInfo updateProject(UpdateProjectStatus updateProjectStatus, Long projectId) {

        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QProject project = QProject.project;

        Project project1 = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException(projectId));

        //프로젝트 명 중복일 시 에러처리
        List<Project> projectList = query
                .selectFrom(project)
                .where(project.product.id.eq(project1.getProduct().getId()))
                .fetch();

        for (Project proj_tmp : projectList) {

            if (proj_tmp.getVersion().equals(updateProjectStatus.getVersion())) {
                throw new DuplicateVersionNameException(proj_tmp.getVersion());
            }
        }

        project1.updateProjectStatus(updateProjectStatus);

        UpdateProjectInfo updateProjectInfo = project1.toUpdateProjectInfo();

        return updateProjectInfo;
    }

    @Transactional
    public String deleteProject(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException(projectId));
        projectRepository.delete(project);
        return "Delete Ok";

    }

    public PowerType checkMemberType(Long memberId) {

        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QMemberTeam memberTeam = QMemberTeam.memberTeam;

        PowerType powerType = query
                .select(memberTeam.powerType)
                .from(memberTeam)
                .where(memberTeam.member.id.eq(memberId))
                .fetchOne();

        if (powerType == null) {
            throw new notFoundPowerByMemberException(memberId);
        }

        return powerType;
    }


    //진행 중 project가 있으면 접근 제한
    public void checkProcessProject(Long productId) {

        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QProject project = QProject.project;
        List<Project> projectList = query
                .selectFrom(project)
                .where(project.product.id.eq(productId))
                .fetch();


        for (Project project1 : projectList) {


            if (project1.getProjectStatus() == ProjectStatus.PROGRESS_IN) {

                throw new ExistProcessProjectExeption(project1.getId());
            }
        }

    }

    //권한 확인
    public PowerType powerValidate(Long memberId) {

        PowerType powerType = checkMemberType(memberId);

        if (powerType == PowerType.DEFAULT || powerType == PowerType.CLIENT) {

            throw new MemberAuthorizedException(memberId);
        }

        return powerType;

    }

}