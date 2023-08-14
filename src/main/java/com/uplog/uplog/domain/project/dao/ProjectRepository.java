package com.uplog.uplog.domain.project.dao;

import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findById(Long id);
    List<Project> findProjectsByProductId(Long productId);
    Optional<Project> findProjectByProductIdAndProjectStatus(Long productId, ProjectStatus projectStatus);
    boolean existsById(Long id);

}
