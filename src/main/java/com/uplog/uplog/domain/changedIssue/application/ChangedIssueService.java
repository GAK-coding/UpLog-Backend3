package com.uplog.uplog.domain.changedIssue.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.changedIssue.dao.ChangedIssueRepository;
import com.uplog.uplog.domain.changedIssue.dto.ChangedIssueDTO;
import com.uplog.uplog.domain.changedIssue.exception.ExistProcessProjectExeption;
import com.uplog.uplog.domain.changedIssue.exception.notFoundPowerByMemberException;
import com.uplog.uplog.domain.changedIssue.model.AccessProperty;
import com.uplog.uplog.domain.changedIssue.model.ChangedIssue;
import com.uplog.uplog.domain.comment.exception.MemberAuthorizedException;
import com.uplog.uplog.domain.comment.model.QComment;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.model.QProduct;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.exception.NotFoundProjectException;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.project.model.QProject;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.QMemberTeam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.uplog.uplog.domain.changedIssue.dto.ChangedIssueDTO.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangedIssueService {

    @PersistenceContext
    private EntityManager entityManager;
    private final ChangedIssueRepository changedIssueRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;


    //todo 완료 눌러서 상태 업데이트는 project 단에서 진행.
    //권한(마스터, 리더)가 아닌 사용자 제외
    @Transactional(readOnly = true)
    public String checkMemberPower(Long memberId){
        PowerType powerType=powerValidate(memberId);

        return AccessProperty.ACCESS_OK.toString();
    }
    @Transactional
    public createInitChangedIssueInfo createInitIssue(createInitChangedIssueInfo createInitChangedIssueInfo,
                                                      Long projId, Long memberId,Long productId){


        //진행 중 project가 있으면 접근 제한
        checkProcessProject(productId);

        Project project=projectRepository.findById(projId)
                .orElseThrow(()->new NotFoundProjectException(projId));
        Member member=memberRepository.findMemberById(memberId)
                .orElseThrow(NotFoundMemberByEmailException::new);
        //수정, 삭제 권한 -> memberId로 memberTeam 쿼리


        //PowerType powerType=powerValidate(memberId);
        //System.out.println("memberPower : "+powerType.toString());

        ChangedIssue changedIssue=createInitChangedIssueInfo.toEntity(member,project);
        changedIssueRepository.save(changedIssue);

        createInitChangedIssueInfo IssueData=changedIssue.toCreateInitChangedIssueInfo();

        return IssueData;


    }

    //업데이트 관련 된 정보만 받아서 값이 있는 컬럼만 업데이트 시킴.
    @Transactional
    public ChangedIssueDTO.updateChangedIssue updateChangedIssue(updateChangedIssue updateChangedIssue,Long issueId){


        ChangedIssue changedIssue=changedIssueRepository.findById(issueId)
                .orElseThrow(()->new NotFoundProjectException(issueId));


        changedIssue.updateChangedIssue(updateChangedIssue);

        return changedIssue.toUpdateChangedIssueInfo();
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

    //진행 완료 된 프로젝트에 변경사항 추가 누를 시 접근 제한
    public String checkProjectProgress(Long memberId,Long projectId){

        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        QProject project=QProject.project;
        //우선 접근 권한 있는 사용자인지 확인
        powerValidate(memberId);

        ProjectStatus projectStatus=query
                .select(project.projectStatus)
                .from(project)
                .where(project.id.eq(projectId))
                .fetchOne();

        if(projectStatus==ProjectStatus.PROGRESS_COMPLETE){
            throw new ExistProcessProjectExeption(projectId,projectStatus);
        }



        return AccessProperty.ACCESS_OK.toString();
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
}
