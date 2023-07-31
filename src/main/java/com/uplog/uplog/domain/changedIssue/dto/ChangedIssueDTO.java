package com.uplog.uplog.domain.changedIssue.dto;

import com.uplog.uplog.domain.changedIssue.model.ChangedIssue;
import com.uplog.uplog.domain.changedIssue.model.IssueStatus;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.project.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ChangedIssueDTO {


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createInitChangedIssueInfo{

        private String title;
        private String content;
        private Long id;
        private IssueStatus issueStatus;
        private LocalDateTime createdTime;
        private LocalDateTime modifiedTime;

        public ChangedIssue toEntity(Member author, Project project){
            return ChangedIssue.builder()
                    .project(project)
                    .author(author)
                    .title(title)
                    .content(content)
                    .issueStatus(issueStatus)
                    .build();

        }

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class updateChangedIssue{
        private String title;
        private String content;
        private IssueStatus issueStatus;

    }
}
