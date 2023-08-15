package com.uplog.uplog.domain.team.dao;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberTeamRepository extends JpaRepository<MemberTeam, Long>, MemberTeamCustomRepository{
    List<MemberTeam> findMemberTeamsByTeamId(Long teamId);
    Optional<MemberTeam> findMemberTeamByMemberIdAndTeamId(Long memberId, Long teamId);
    Optional<MemberTeam> findMemberTeamById(Long id);
    boolean existsMemberTeamByMemberEmailAndTeamId(String memberEmail, Long teamId);
    boolean existsMemberTeamByMemberIdAndTeamId(Long memberId, Long teamId);

    List<MemberTeam> findMemberTeamsByTeamIdAndPowerType(Long teamId, PowerType powerType);
}
