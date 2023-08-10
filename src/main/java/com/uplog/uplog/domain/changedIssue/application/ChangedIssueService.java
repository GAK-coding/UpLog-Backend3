package com.uplog.uplog.domain.changedIssue.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.changedIssue.dao.ChangedIssueRepository;
import com.uplog.uplog.domain.changedIssue.dto.ChangedIssueDTO;
import com.uplog.uplog.domain.changedIssue.exception.notFoundIssueException;
import com.uplog.uplog.domain.changedIssue.exception.notFoundPowerByMemberException;
import com.uplog.uplog.domain.changedIssue.model.AccessProperty;
import com.uplog.uplog.domain.changedIssue.model.ChangedIssue;
import com.uplog.uplog.domain.changedIssue.model.QChangedIssue;
import com.uplog.uplog.domain.comment.exception.MemberAuthorizedException;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.exception.ExistProcessProjectExeption;
import com.uplog.uplog.domain.project.exception.NotFoundProjectException;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.project.model.QProject;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.QMemberTeam;
import com.uplog.uplog.domain.team.model.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

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
    private final MemberTeamRepository memberTeamRepository;
    private final ProductRepository productRepository;
    private final TeamRepository teamRepository;


    //todo 완료 눌러서 상태 업데이트는 project 단에서 진행.
    //권한(마스터, 리더)가 아닌 사용자 제외
    @Transactional
    public String checkMemberPower(Long memberId){
        PowerType powerType=powerValidate(memberId);

        return AccessProperty.ACCESS_OK.toString();
    }
    @Transactional
    public createInitChangedIssueInfo createInitIssue(createInitChangedIssueInfo createInitChangedIssueInfo,
                                                      Long projId, Long memberId,Long productId){


        //진행 중 project가 아니면 접근 제한
        checkProjectProgress(memberId,projId);

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

    @Transactional(readOnly = true)
    public ChangedIssueDTO.issueInfo readIssueInfo(Long issueId){

        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        QChangedIssue changedIssue=QChangedIssue.changedIssue;

        ChangedIssue changedIssue1=query
                .selectFrom(changedIssue)
                .where(changedIssue.id.eq(issueId))
                .fetchOne();

        if(changedIssue1==null){
            throw new notFoundIssueException(issueId);
        }

        ChangedIssueDTO.issueInfo issueInfo=changedIssue1.toIssueInfo();

        return issueInfo;
    }

    //업데이트 관련 된 정보만 받아서 값이 있는 컬럼만 업데이트 시킴.
    @Transactional
    public ChangedIssueDTO.updateChangedIssue updateChangedIssue(updateChangedIssue updateChangedIssue,Long issueId){


        ChangedIssue changedIssue=changedIssueRepository.findById(issueId)
                .orElseThrow(()->new NotFoundProjectException(issueId));


        changedIssue.updateChangedIssue(updateChangedIssue);

        return changedIssue.toUpdateChangedIssueInfo();
    }

    @Transactional
    public String deleteChangedIssue(Long issueId){


        ChangedIssue changedIssue=changedIssueRepository.findById(issueId)
                .orElseThrow(()->new NotFoundProjectException(issueId));

        changedIssueRepository.delete(changedIssue);

        return "Delete OK";
    }



    //권한 확인
    public PowerType powerValidate(Long memberId ){

        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        QMemberTeam memberTeam=QMemberTeam.memberTeam;
////////////테스트//////////
        Product product=productRepository.findById(1L)
                .orElseThrow(NotFoundMemberByEmailException::new);
        Team team= Team.builder()
                .name("dd")
                .product(product)
                .build();

        teamRepository.save(team);
        Member member=memberRepository.findMemberById(memberId)
                .orElseThrow(NotFoundMemberByEmailException::new);
        MemberTeam memberTeam1=MemberTeam.builder()
                .member(member)
                .team(team)
                .powerType(PowerType.MASTER)
                .build();
        memberTeamRepository.save(memberTeam1);
////////////테스트//////////
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


        ProjectStatus projectStatus=query
                .select(project.projectStatus)
                .from(project)
                .where(project.id.eq(projectId))
                .fetchOne();

        if(projectStatus==ProjectStatus.PROGRESS_COMPLETE){
            throw new ExistProcessProjectExeption(projectId,projectStatus);
        }

        //접근 권한 있는 사용자인지 확인
        powerValidate(memberId);



        return AccessProperty.ACCESS_OK.toString();
    }



}
