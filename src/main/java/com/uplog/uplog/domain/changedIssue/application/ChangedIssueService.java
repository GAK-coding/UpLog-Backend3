package com.uplog.uplog.domain.changedIssue.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.changedIssue.dao.ChangedIssueRepository;
import com.uplog.uplog.domain.changedIssue.exception.NotFoundIssueByProjectException;
import com.uplog.uplog.domain.changedIssue.exception.NotFoundIssueException;
import com.uplog.uplog.domain.changedIssue.exception.NotFoundPowerByMemberException;
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
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.method.AuthorizedMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final MemberTeamRepository memberTeamRepository;
    private final ProductRepository productRepository;
    private final TeamRepository teamRepository;
    private final AuthorizedMethod authorizedMethod;


    @Transactional
    public IssueInfoDTO createIssue(CreateChangedIssueRequest CreateChangedIssueRequest,
                                    Long projectId, Long memberId){


        Project project=projectRepository.findById(projectId)
                .orElseThrow(()->new NotFoundProjectException(projectId));
        Member member=memberRepository.findMemberById(memberId)
                .orElseThrow(NotFoundMemberByEmailException::new);

        ChangedIssue changedIssue= CreateChangedIssueRequest.toEntity(member,project);
        changedIssueRepository.save(changedIssue);

        IssueInfoDTO IssueData=changedIssue.toIssueInfoDTO();

        return IssueData;


    }

    @Transactional(readOnly = true)
    public IssueInfoDTO findByIssueId(Long issueId){

        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        QChangedIssue changedIssue=QChangedIssue.changedIssue;

        ChangedIssue changedIssue1=changedIssueRepository.findById(issueId)
                .orElseThrow(()->new NotFoundIssueException(issueId));

        IssueInfoDTO IssueInfoDTO =changedIssue1.toIssueInfoDTO();

        return IssueInfoDTO;
    }

    @Transactional(readOnly = true)
    public List<IssueInfoByProjectDTO> findIssueByProjectId(Long projectId){

        List<ChangedIssue> issueList=changedIssueRepository.findByProjectId(projectId);

        if (issueList.isEmpty())
            throw new NotFoundIssueByProjectException(projectId);
        else if(issueList==null)
            throw new NotFoundIssueByProjectException(projectId);
        List<IssueInfoByProjectDTO> issueInfoByProjectDTOList=new ArrayList<>();;
        for(ChangedIssue temp:issueList){

            issueInfoByProjectDTOList.add(temp.toIssueInfoByProjectDTO());

        }

        return issueInfoByProjectDTOList;


    }

    //업데이트 관련 된 정보만 받아서 값이 있는 컬럼만 업데이트 시킴.
    //작성자만 가능
    //현재 진행중인 프로젝트만 가능
    @Transactional
    public SimpleIssueInfoDTO updateChangedIssue(UpdateChangedIssueRequest UpdateChangedIssueRequest, Long issueId,Long memberId){


        ChangedIssue changedIssue=changedIssueRepository.findById(issueId)
                .orElseThrow(()->new NotFoundProjectException(issueId));
        Long currentProjectId=changedIssue.getProject().getId();

        //현재 진행중인 프로젝트가 맞는지 확인
        authorizedMethod.checkProjectProgress(currentProjectId);
        //마스터,리더가 맞는지 확인(밑에서 확인하지만 2중확인임)
        authorizedMethod.powerValidateByMemberId(memberId);

        //작성자와 일치하는지 확인->마스터 리더 같이 확인하는셈(작성자가 이미 리더 또는 마스터일테니까)
        if(changedIssue.getAuthor().getId().equals(memberId)){
            changedIssue.updateChangedIssue(UpdateChangedIssueRequest);
            changedIssue.onUpdate();
            return changedIssue.toSimpleIssueInfoDTO();
        }
        else{
            throw new AuthorityException("작성자와 일치하지 않아 수정 권한이 없습니다.");
        }

    }

    //이력 작성자만 가능
    //헌재 진행중인 프로젝트만 삭제 가능
    @Transactional
    public String deleteChangedIssue(Long issueId,Long memberId){
        ChangedIssue changedIssue=changedIssueRepository.findById(issueId)
                .orElseThrow(()->new NotFoundProjectException(issueId));
        Long currentProjectId=changedIssue.getProject().getId();

        //현재 진행중인 프로젝트가 맞는지 확인
        authorizedMethod.checkProjectProgress(currentProjectId);
        //마스터,리더가 맞는지 확인(밑에서 확인하지만 2중확인임)
        authorizedMethod.powerValidateByMemberId(memberId);

        //작성자와 일치하는지 확인->마스터 리더 같이 확인하는셈(작성자가 이미 리더 또는 마스터일테니까)
        if(changedIssue.getAuthor().getId().equals(memberId)){
            changedIssueRepository.delete(changedIssue);
            return "Delete OK";
        }
        else{
            throw new AuthorityException("작성자와 일치하지 않아 수정 권한이 없습니다.");
        }

    }



//    ////////////테스트////////// -> 추후 지울 것.
//    Product product=productRepository.findById(1L)
//            .orElseThrow(NotFoundMemberByEmailException::new);
//    Team team= Team.builder()
//            .name("dd")
//            .product(product)
//            .build();
//
//        teamRepository.save(team);
//    Member member=memberRepository.findMemberById(memberId)
//            .orElseThrow(NotFoundMemberByEmailException::new);
//    MemberTeam memberTeam1=MemberTeam.builder()
//            .member(member)
//            .team(team)
//            .powerType(PowerType.MASTER)
//            .build();
//        memberTeamRepository.save(memberTeam1);
//////////////테스트////////// -> 추후 지울 것.



}