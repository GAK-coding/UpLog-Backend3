package com.uplog.uplog.domain.changedIssue.model;

import com.uplog.uplog.domain.changedIssue.dto.ChangedIssueDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    private LocalDateTime createTime;

    @CreationTimestamp
    private LocalDateTime modifiedTime;

    @Builder
    public ChangedIssue(Member author, Project project, IssueStatus issueStatus, String title, String content, LocalDateTime createdTime,LocalDateTime modifiedTime){
        this.author=author;
        this.project=project;
        this.issueStatus=issueStatus;
        this.title=title;
        this.content=content;

    }


    public ChangedIssueDTO.SimpleIssueInfoDTO toSimpleIssueInfoDTO(){
        return ChangedIssueDTO.SimpleIssueInfoDTO.builder()
                .title(this.title)
                .content(this.content)
                .issueStatus(this.issueStatus)
                .createdTime(this.getCreatedTime())
                .modifiedTime(this.getModifiedTime())
                .build();
    }

    public ChangedIssueDTO.IssueInfoDTO toIssueInfoDTO(){
        return ChangedIssueDTO.IssueInfoDTO.builder()
                .id(this.id)
                .projectId(this.project.getId())
                .title(this.title)
                .content(this.content)
                .issueStatus(this.issueStatus)
                .createdTime(this.getCreatedTime())
                .modifiedTime(this.getModifiedTime())
                .build();

    }

    public ChangedIssueDTO.IssueInfoByProjectDTO toIssueInfoByProjectDTO(){
        return ChangedIssueDTO.IssueInfoByProjectDTO.builder()
                .id(this.id)
                .title(this.title)
                .issueStatus(this.issueStatus)
                .content(this.content)
                .createdTime(this.getCreatedTime())
                .modifiedTime(this.getModifiedTime())
                .build();
    }

    public void updateChangedIssue(ChangedIssueDTO.UpdateChangedIssueRequest UpdateChangedIssueRequest){

        this.title=(UpdateChangedIssueRequest.getTitle()!=null)? UpdateChangedIssueRequest.getTitle():this.title;
        this.content=(UpdateChangedIssueRequest.getContent()!=null)? UpdateChangedIssueRequest.getContent():this.content;
        this.issueStatus=(UpdateChangedIssueRequest.getIssueStatus()!=null)? UpdateChangedIssueRequest.getIssueStatus():this.issueStatus;
    }
    public void onUpdate() {
        modifiedTime = LocalDateTime.now();
    }



}