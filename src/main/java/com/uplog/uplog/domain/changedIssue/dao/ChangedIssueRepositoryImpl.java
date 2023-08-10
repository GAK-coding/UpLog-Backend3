package com.uplog.uplog.domain.changedIssue.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.project.model.QProject;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.QMemberTeam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.uplog.uplog.domain.project.model.QProject.project;
import static com.uplog.uplog.domain.team.model.QMemberTeam.memberTeam;

public class ChangedIssueRepositoryImpl implements ChangedIssueRepositoryCustom{

    private final JPAQueryFactory query;
    public ChangedIssueRepositoryImpl(EntityManager entityManager){
        this.query=new JPAQueryFactory(entityManager);
    }




    ///////////////////////////Query///////////////////////////
    @Override
    public PowerType findMemberPowerTypeByMemberId(Long memberId){
        return query
                .select(memberTeam.powerType)
                .from(memberTeam)
                .where(memberTeam.member.id.eq(memberId))
                .fetchOne();
    }
    @Override
    public ProjectStatus findProjectStatusByProjectId(Long projectId){
        return query
                .select(project.projectStatus)
                .from(project)
                .where(project.id.eq(projectId))
                .fetchOne();
    }



}
