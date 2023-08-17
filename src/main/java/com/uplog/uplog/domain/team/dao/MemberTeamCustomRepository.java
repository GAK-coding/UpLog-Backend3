package com.uplog.uplog.domain.team.dao;

import com.uplog.uplog.domain.team.model.MemberTeam;

import java.util.List;

public interface MemberTeamCustomRepository {
    List<MemberTeam> findMemberTeamsByMemberIdAndProjectId(Long memberId, Long projectId);
}
