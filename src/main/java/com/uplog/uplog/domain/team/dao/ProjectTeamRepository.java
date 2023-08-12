//package com.uplog.uplog.domain.team.dao;
//
//import com.uplog.uplog.domain.team.model.ProjectTeam;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface ProjectTeamRepository extends JpaRepository<ProjectTeam, Long> {
//    Optional<ProjectTeam> findById(Long projectId);
//    Optional<ProjectTeam> findByProjectIdAndName(Long id, String projectName);
//    boolean existsById(Long id);
//    List<ProjectTeam> findProjectTeamsByProjectId(Long projectId);
//    //int countProjectTeamsByProjectId()
//    List<ProjectTeam> findProjectTeamsByProjectIdAndParentTeamId(Long projectId, Long parentTeamId);
//}
