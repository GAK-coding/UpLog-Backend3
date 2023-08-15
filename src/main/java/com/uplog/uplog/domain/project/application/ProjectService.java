package com.uplog.uplog.domain.project.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.changedIssue.exception.NotFoundPowerByMemberException;
import com.uplog.uplog.domain.comment.exception.MemberAuthorizedException;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.menu.application.MenuService;
import com.uplog.uplog.domain.menu.dto.MenuDTO.SimpleMenuInfoDTO;
import com.uplog.uplog.domain.product.dao.ProductMemberRepository;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.product.model.ProductMember;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.exception.DuplicateVersionNameException;
import com.uplog.uplog.domain.project.exception.ExistProcessProjectExeption;
import com.uplog.uplog.domain.project.exception.NotFoundProjectException;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.project.model.QProject;
import com.uplog.uplog.domain.team.application.MemberTeamService;
import com.uplog.uplog.domain.team.application.TeamService;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.SimpleTeamInfoDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.QMemberTeam;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.DuplicatedNameException;
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

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final ProductRepository productRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final MemberRepository memberRepository;
    private final ProductMemberRepository productMemberRepository;

    private final MenuService menuService;
    private final TeamService projectTeamService;
    private final MemberTeamService memberTeamService;


    @Transactional
    public ProjectInfoDTO createProject(Long memberId,CreateProjectRequest createProjectRequest, Long productId) throws Exception {
//        Member member = memberRepository.findMemberById(memberId).orElseThrow(NotFoundIdException::new);
        ProductMember memberProduct = productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, productId).orElseThrow(NotFoundIdException::new);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProjectException(productId));

        //진행 중 프로젝트 있을 시 제한
        checkProcessProject(productId);

        //프로젝트 이름 중복되면 예외처리
        for(Project p : product.getProjectList()){
            if(createProjectRequest.getVersion().equals(p.getVersion()))
                throw new DuplicatedNameException("제품 내에서 프로젝트 버전이 중복됩니다.");
        }

        //Member가 마스터인,리더인지 확인해야함. -> 스프링 시큐리티! -> 이건 제품에서 확인
        if(memberProduct.getPowerType() == PowerType.CLIENT || memberProduct.getPowerType()== PowerType.DEFAULT){
            throw new AuthorityException("프로젝트 생성 권한이 없습니다.");
        }

        //새로 생성 되는 프로젝트는 진행 중 고정 값.
        Project project = createProjectRequest.toEntity(product);
        projectRepository.save(project);

        //menu 생성
        List<SimpleMenuInfoDTO> simpleMenuInfoDTOList = menuService.createDefaultMenu(project.getId());

        //팀 생성.
        //TODO Link 부분 상의
        Team team = Team.builder()
                .memberTeamList(new ArrayList<>())
                .project(project)
                .parentTeam(null)
                .name(createProjectRequest.getVersion())
                .depth(0)
                .build();
        teamRepository.save(team);
        log.info(team.getId()+"id");


        //현재 멤버를 넣어줘야함.
        List<MemberTeam> memberTeamList = new ArrayList<>();
        for (ProductMember mp : product.getProductMemberList()) {

            CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
                    .memberId(mp.getMember().getId())
                    .teamId(team.getId())
                    .powerType(mp.getPowerType() == PowerType.MASTER || mp.getPowerType() == PowerType.LEADER ? PowerType.LEADER : mp.getPowerType())
                    .link(createProjectRequest.getLink())
                    .build();

            memberTeamService.createMemberTeam(createMemberTeamRequest);
//            MemberTeam memberTeam = memberTeamRepository.findMemberTeamById(memberTeamId).orElseThrow(NotFoundIdException::new);
//            team.getMemberTeamList().add(memberTeam);
        }

        List<Long> projectTeamIdList = new ArrayList<>();
        projectTeamIdList.add(team.getId());

        project.getTeamList().add(team);


        return project.toProjectInfoDTO(simpleMenuInfoDTOList, projectTeamIdList);
    }

    //====================read=============================
    //프로젝트에 속한 팀 모두 출력 -> 부모, 자식 구분 안함.
    @Transactional(readOnly = true)
    public List<SimpleTeamInfoDTO> findTeamsByProjectId(Long projectId){
        List<Team> teamList = teamRepository.findTeamsByProjectId(projectId);
        List<SimpleTeamInfoDTO> simpleTeamInfoDTOList = new ArrayList<>();

        for(Team t : teamList){
            simpleTeamInfoDTOList.add(t.toSimpleTeamInfoDTO());
        }

        return simpleTeamInfoDTOList;
    }
    @Transactional(readOnly = true)
    public requestProjectAllInfo readProject(Long memberId, Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException(projectId));


        PowerType powerType = checkMemberType(memberId, projectId);


        requestProjectAllInfo requestProjectAllInfo = project.toRequestProjectAllInfo(powerType,
                project.getProduct().getName(),
                project.getProduct().getCompany());

        return requestProjectAllInfo;
    }

    //제품에 해당하는 프로젝트들 찾기
    @Transactional(readOnly = true)
    public List<VerySimpleProjectInfoDTO> findProjectsByProductId(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(NotFoundIdException::new);

        List<Project> projectList = projectRepository.findProjectsByProductId(productId);
        List<VerySimpleProjectInfoDTO> verySimpleProjectInfoDTOList = new ArrayList<>();

        for(Project p : projectList){
            verySimpleProjectInfoDTOList.add(p.toVerySimpleProjectInfoDTO());
        }
        return verySimpleProjectInfoDTOList;
    }

    @Transactional(readOnly = true)
    public requestProjectInfo readProjectSimple(Long memberId, Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException(projectId));


        PowerType powerType = checkMemberType(memberId, projectId);

        requestProjectInfo requestProjectInfo = project.toRequestProjectInfo(powerType,
                project.getProduct().getName(),
                project.getProduct().getCompany());

        return requestProjectInfo;


    }
//=================================update======================================가
    @Transactional
    public UpdateProjectInfo updateProject(Long memberId,UpdateProjectStatus updateProjectStatus, Long projectId) {
        //마스터와 리더만 프로젝트를 수정할 수 있음.
        //권한 확인
        powerValidate(memberId, projectId);

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
        Team team = teamRepository.findByProjectIdAndName(projectId, project1.getVersion()).orElseThrow(NotFoundIdException::new);
        team.updateName(updateProjectStatus.getVersion());
        project1.updateProjectStatus(updateProjectStatus);


        UpdateProjectInfo updateProjectInfo = project1.toUpdateProjectInfo();

        return updateProjectInfo;
    }
    //======================method======================================
    public PowerType checkMemberType(Long memberId, Long projectId) {

        //JPAQueryFactory query = new JPAQueryFactory(entityManager);
        Project project = projectRepository.findById(projectId).orElseThrow(NotFoundIdException::new);
        ProductMember productMember = productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, project.getProduct().getId()).orElseThrow(NotFoundIdException::new);
        //QMemberTeam memberTeam = QMemberTeam.memberTeam;


//        PowerType powerType = query
//                .select(memberTeam.powerType)
//                .from(memberTeam)
//                .where(memberTeam.member.id.eq(memberId))
//                .fetchOne();
//
//        if (powerType == null) {
//            throw new NotFoundPowerByMemberException(memberId);
//        }

        return productMember.getPowerType();
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
    public PowerType powerValidate(Long memberId, Long projectId) {

        PowerType powerType = checkMemberType(memberId, projectId);

        if (powerType == PowerType.DEFAULT || powerType == PowerType.CLIENT) {

            throw new MemberAuthorizedException(memberId);
        }

        return powerType;

    }
    //===============================delete===========================
    @Transactional
    public String deleteProject(Long memberId, Long projectId) {
        //권한 확인
        powerValidate(memberId, projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundProjectException(projectId));
        projectRepository.delete(project);
        return "Delete Ok";

    }


}