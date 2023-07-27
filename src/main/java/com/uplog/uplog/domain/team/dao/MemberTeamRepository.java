package com.uplog.uplog.domain.team.dao;

import com.uplog.uplog.domain.team.model.MemberTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberTeamRepository extends JpaRepository<MemberTeam, Long> {
    List<MemberTeam> findMemberTeamsByTeamId(Long id);
}
