package com.uplog.uplog.domain.task.model;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    private String taskName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member targetMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    private String taskDetail;

    private LocalDate startTime;
    private LocalDate endTime;

    private Long taskIndex; //drag and drop



//
//    public TaskDTO taskDTO?(){
//        return
//    }

//    public TaskInfoDTO toTaskInfoDTO1(){
//        return TaskInfoDTO.builder()
//                .id(this.getId())
//                .taskName(this.getTaskName())
//                .targetMemberInfoDTO(this.getTargetMember().powerMemberInfoDTO())
//                .taskStatus(this.getTaskStatus())
//                .startTime(this.getStartTime())
//                .endTime(this.getEndTime())
//                .build();
//    }

    public TaskInfoDTO toTaskInfoDTO(){
        return TaskInfoDTO.builder()
                .id(this.getId())
                .taskName(this.getTaskName())
                .targetMemberInfoDTO(this.getTargetMember().powerMemberInfoDTO())
                .taskDetail(this.taskDetail)
                .menuId(this.getMenu().getId())
                .menuName(this.getMenu().getMenuName())
                .teamId(this.getTeam().getId())
                .teamName(this.getTeam().getName())
                .taskStatus(this.getTaskStatus())
                .startTime(this.getStartTime())
                .endTime(this.getEndTime())
                .taskIndex(this.taskIndex)
                .build();
    }


//    //수정될거(메뉴,멤버,팀,이름,상세설명,시작날짜,끝날짜)
//    public void UpdateTask(UpdateTaskData updateTaskData){
//        this.menu=(updateTaskData.getMenu()!=null)? updateTaskData.getMenu():this.menu;
//        this.targetMember=(updateTaskData.getTargetmember()!=null)? updateTaskData.getTargetmember():this.targetMember;
//        this.projectTeam=(updateTaskData.getProjectTeam()!=null)? updateTaskData.getProjectTeam():this.projectTeam;
//        this.taskName=(updateTaskData.getTaskName()!=null)? updateTaskData.getTaskName():this.taskName;
//        this.taskDetail=(updateTaskData.getTaskDetail()!=null)? updateTaskData.getTaskDetail():this.taskDetail;
//        this.startTime=(updateTaskData.getStartTime()!=null)? updateTaskData.getStartTime():this.startTime;
//        this.endTime=(updateTaskData.getEndTime()!=null)? updateTaskData.getEndTime():this.endTime;
//        this.taskStatus=(updateTaskData.getTaskStatus()!=null)? updateTaskData.getTaskStatus():this.taskStatus;
//
//
//    }

    public void updateTaskName(String updateName){this.taskName=updateName;}
    public void updateTaskDate(LocalDate updateStartTime, LocalDate updateEndTime){this.startTime=updateStartTime; this.endTime=updateEndTime;}
    public void updateTaskContent(String updateContent){this.taskDetail=updateContent;}

    public void updateTaskmember(Member targetMember){this.targetMember=targetMember;}
    public void updateTaskTeam(Team team){this.team=team;}
    public void updateTaskMenu(Menu menu){this.menu=menu;}

    public void updateTaskStatus(TaskStatus taskStatus){
        this.taskStatus=taskStatus;
    }

    public void updateTaskIndex(Long updateTaskIndex){this.taskIndex=updateTaskIndex;}


//    public updateTaskStatusDTO toUpdateTaskStatusDTO (UpdateTaskStatusRequest updateTaskStatusRequest){
//        return updateTaskStatusDTO.builder()
//                .id(this.getId())
//                .taskStatus(this.getTaskStatus())
//                .build();
//    }

}
