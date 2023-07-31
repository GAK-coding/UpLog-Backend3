package com.uplog.uplog.domain.changedIssue.model;

import com.uplog.uplog.domain.changedIssue.dto.ChangedIssueDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangedIssue extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "changedIssue_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private IssueStatus issueStatus;

    private String title;
    private String content;

    @Builder
    public ChangedIssue(Member author, Project project, IssueStatus issueStatus, String title, String content, LocalDateTime createdTime,LocalDateTime modifiedTime){
        this.author=author;
        this.project=project;
        this.issueStatus=issueStatus;
        this.title=title;
        this.content=content;

    }

    public ChangedIssueDTO.createInitChangedIssueInfo toCreateInitChangedIssueInfo(){
        return ChangedIssueDTO.createInitChangedIssueInfo.builder()
                .id(this.id)
                .createdTime(this.getCreatedTime())
                .modifiedTime(this.getModifiedTime())
                .build();
    }

    public ChangedIssueDTO.updateChangedIssue toUpdateChangedIssueInfo(){
        return ChangedIssueDTO.updateChangedIssue.builder()
                .title(this.title)
                .content(this.content)
                .issueStatus(this.issueStatus)
                .build();
    }

    public void updateChangedIssue(ChangedIssueDTO.updateChangedIssue updateChangedIssue){

        this.title=(updateChangedIssue.getTitle()!=null)?updateChangedIssue.getTitle():this.title;
        this.content=(updateChangedIssue.getContent()!=null)?updateChangedIssue.getContent():this.content;
        this.issueStatus=(updateChangedIssue.getIssueStatus()!=null)?updateChangedIssue.getIssueStatus():this.issueStatus;
    }


}
