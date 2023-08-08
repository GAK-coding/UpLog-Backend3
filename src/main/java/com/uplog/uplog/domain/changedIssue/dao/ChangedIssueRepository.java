package com.uplog.uplog.domain.changedIssue.dao;

import com.uplog.uplog.domain.changedIssue.model.ChangedIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChangedIssueRepository extends JpaRepository<ChangedIssue, Long>,ChangedIssueRepositoryCustom {
    Optional<ChangedIssue> findById(Long id);

    List<ChangedIssue> findByProjectId(Long projectId);

    List<ChangedIssue> findByAuthorId(Long authorId);
}
