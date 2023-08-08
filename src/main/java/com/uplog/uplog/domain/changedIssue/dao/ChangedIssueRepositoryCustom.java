package com.uplog.uplog.domain.changedIssue.dao;

import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.team.model.PowerType;

public interface ChangedIssueRepositoryCustom {

    PowerType findMemberPowerTypeByMemberId(Long memberId);
    ProjectStatus findProjectStatusByProjectId(Long projectId);
}
