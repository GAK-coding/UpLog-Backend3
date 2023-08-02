package com.uplog.uplog.domain.project.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.changedIssue.exception.notFoundPowerByMemberException;
import com.uplog.uplog.domain.comment.exception.MemberAuthorizedException;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.exception.DuplicateVersionNameException;
import com.uplog.uplog.domain.project.exception.ExistProcessProjectExeption;
import com.uplog.uplog.domain.project.exception.NotFoundProjectException;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.project.model.QProject;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.QMemberTeam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


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


    //TODO 여기서 member(group) 처리해야 하나?
    @Transactional
    public CreateInitInfo createInit(CreateInitInfo createInitInfo,Long productId){
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new NotFoundProjectException(productId));

        //새로 생성 되는 프로젝트는 진행 중 고정 값.
        Project project=createInitInfo.toEntity(ProjectStatus.PROGRESS_IN,product);

        projectRepository.save(project);

        CreateInitInfo createInitInfo1=project.toCreateInitChangedIssueInfo();

        return createInitInfo1;
    }

    @Transactional(readOnly = true)
    public requestProjectAllInfo readProject(Long projectId, Long memberId){

        Project project=projectRepository.findById(projectId)
                .orElseThrow(()->new NotFoundProjectException(projectId));


        PowerType powerType=checkMemberType(memberId);


        requestProjectAllInfo requestProjectAllInfo =project.toRequestProjectAllInfo(powerType,
                project.getProduct().getName(),
                project.getProduct().getCompany());

        return requestProjectAllInfo;
    }

    @Transactional(readOnly = true)
    public requestProjectInfo readProjectSimple(Long projectId, Long memberId){

        Project project=projectRepository.findById(projectId)
                .orElseThrow(()->new NotFoundProjectException(projectId));


        PowerType powerType=checkMemberType(memberId);

        requestProjectInfo requestProjectInfo=project.toRequestProjectInfo(powerType,
                project.getProduct().getName(),
                project.getProduct().getCompany());

        return requestProjectInfo;


    }

    @Transactional
    public UpdateProjectInfo updateProject(UpdateProjectStatus updateProjectStatus,Long projectId){

        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        QProject project=QProject.project;

        Project project1=projectRepository.findById(projectId)
                .orElseThrow(()->new NotFoundProjectException(projectId));

        //프로젝트 명 중복일 시 에러처리
        List<Project> projectList=query
                .selectFrom(project)
                .where(project.product.id.eq(project1.getProduct().getId()))
                .fetch();

        for(Project proj_tmp: projectList){

            if(proj_tmp.getVersion().equals(updateProjectStatus.getVersion())){
                throw new DuplicateVersionNameException(proj_tmp.getVersion());
            }
        }

        project1.updateProjectStatus(updateProjectStatus);

        UpdateProjectInfo updateProjectInfo=project1.toUpdateProjectInfo();

        return updateProjectInfo;
    }

    @Transactional
    public String deleteProject(Long projectId){

        Project project=projectRepository.findById(projectId)
                .orElseThrow(()->new NotFoundProjectException(projectId));
        projectRepository.delete(project);
        return "Delete Ok";

    }

    public PowerType checkMemberType(Long memberId){

        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        QMemberTeam memberTeam=QMemberTeam.memberTeam;

        PowerType powerType =query
                .select(memberTeam.powerType)
                .from(memberTeam)
                .where(memberTeam.member.id.eq(memberId))
                .fetchOne();

        if(powerType==null){
            throw new notFoundPowerByMemberException(memberId);
        }

            return powerType;
    }


    //진행 중 project가 있으면 접근 제한
    public void checkProcessProject(Long productId){

        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        QProject project=QProject.project;
        List<Project> projectList=query
                .selectFrom(project)
                .where(project.product.id.eq(productId))
                .fetch();


        for(Project project1: projectList){


            if(project1.getProjectStatus()== ProjectStatus.PROGRESS_IN){

                throw new ExistProcessProjectExeption(project1.getId());
            }
        }

    }

    //권한 확인
    public PowerType powerValidate(Long memberId ){

        PowerType powerType=checkMemberType(memberId);

        if(powerType==PowerType.DEFAULT || powerType==PowerType.CLIENT){

            throw new MemberAuthorizedException(memberId);
        }

        return powerType;

    }

}