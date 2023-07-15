package com.uplog.uplog.domain.changedIssue.dao;

import com.uplog.uplog.domain.changedIssue.model.ChangedIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangedIssueRepository extends JpaRepository<ChangedIssue, Long> {
}
