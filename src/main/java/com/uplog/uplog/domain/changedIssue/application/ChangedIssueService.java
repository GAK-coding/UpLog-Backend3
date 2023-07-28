package com.uplog.uplog.domain.changedIssue.application;

import com.uplog.uplog.domain.changedIssue.dao.ChangedIssueRepository;
import com.uplog.uplog.domain.changedIssue.model.ChangedIssue;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.exception.NotFoundProjectException;
import com.uplog.uplog.domain.project.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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


    @Transactional
    public createInitChangedIssueInfo createInitIssue(createInitChangedIssueInfo createInitChangedIssueInfo,
                                                      Long projId, Long memberId){

        Project project=projectRepository.findById(projId)
                .orElseThrow(()->new NotFoundProjectException(projId));
        Member member=memberRepository.findMemberById(memberId)
                .orElseThrow(NotFoundMemberByEmailException::new);
        //수정, 삭제 권한 -> memberId로 memberTeam 쿼리

        ChangedIssue changedIssue=createInitChangedIssueInfo.of(member,project);
        changedIssueRepository.save(changedIssue);

        createInitChangedIssueInfo IssueData=changedIssue.of();

        return IssueData;


    }
}
