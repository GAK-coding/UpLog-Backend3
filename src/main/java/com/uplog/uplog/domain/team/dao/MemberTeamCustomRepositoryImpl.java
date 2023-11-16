package com.uplog.uplog.domain.team.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.member.model.QMember;
import com.uplog.uplog.domain.project.model.QProject;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.QMemberTeam;
import com.uplog.uplog.domain.team.model.QTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RequiredArgsConstructor
public class MemberTeamCustomRepositoryImpl implements MemberTeamCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private static final QMemberTeam memberTeam = QMemberTeam.memberTeam;
    private static final QProject project = QProject.project;
    private static final QTeam team = QTeam.team;
    private static final QMember member = QMember.member;


    //멤버 아이디와 프로젝트 아이디로 팀 찾기
    @Override
    public List<MemberTeam> findMemberTeamsByMemberIdAndProjectId(Long memberId, Long projectId) {
        List<MemberTeam> meberTeamList = jpaQueryFactory
                .selectFrom(memberTeam)
                .innerJoin(memberTeam.team, team)
                .where(
                        team.project.id.eq(projectId),
                        memberTeam.member.id.eq(memberId)
                )
                .fetchJoin()
                .fetch();
        return meberTeamList;
    }
}
