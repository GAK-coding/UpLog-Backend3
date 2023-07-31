package com.uplog.uplog.domain.project.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.changedIssue.exception.notFoundPowerByMemberException;
import com.uplog.uplog.domain.comment.exception.MemberAuthorizedException;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.project.exception.DuplicateVersionNameException;
import com.uplog.uplog.domain.project.exception.ExistProcessProjectExeption;
import com.uplog.uplog.domain.project.exception.NotFoundProjectException;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.project.model.QProject;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.QMemberTeam;
import com.uplog.uplog.domain.team.model.Team;
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


    //TODO 여기서 member(group) 처리해야 하나?
    @Transactional
    public CreateInitInfo createInit(CreateInitInfo createInitInfo, Long prodId){

        //새로 생성 되는 프로젝트는 진행 중 고정 값.
        Project project=createInitInfo.toEntity(ProjectStatus.PROGRESS_IN);

        projectRepository.save(project);

        CreateInitInfo createInitInfo1=project.toCreateInitChangedIssueInfo();

        return createInitInfo1;
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

        if(powerType==PowerType.DEFAULT || powerType==PowerType.CLIENT){

            throw new MemberAuthorizedException(memberId);
        }

        return powerType;

    }

}