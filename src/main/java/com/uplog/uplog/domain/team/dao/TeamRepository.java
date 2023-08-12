package com.uplog.uplog.domain.team.dao;

import com.uplog.uplog.domain.team.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    //Optional<Team> findById(Long id);
    Team findTeamById(Long id);
    Optional<Team> findByProjectIdAndName(Long projectId, String name);
}
