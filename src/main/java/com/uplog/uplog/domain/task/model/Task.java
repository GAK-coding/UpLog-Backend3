package com.uplog.uplog.domain.task.model;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member targetMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private TaskStatus taskStatus;
    private String taskDetail;
    private LocalDate startTime;
    private LocalDate endTime;


    public TaskInfoDTO toTaskInfoDTO(){
        return TaskInfoDTO.builder()
                .id(this.getId())
                .targetmember(this.getTargetMember())
                .menu(this.getMenu())
                .taskStatus(this.getTaskStatus())
                .startTime(this.getStartTime())
                .endTime(this.getEndTime())
                .build();
    }

    public UpdateTaskDTO toUpdateTaskDTO(UpdateTaskRequest updateTaskRequest){
        return UpdateTaskDTO.builder()
                .id(this.getId())
                .targetmember(this.getTargetMember())
                .menu(this.getMenu())
                .taskStatus(this.getTaskStatus())
                .startTime(this.getStartTime())
                .endTime(this.getEndTime())
                .build();
    }
    public updateTaskStatusDTO toUpdateTaskStatusDTO (UpdateTaskStatusRequest updateTaskStatusRequest){
        return updateTaskStatusDTO.builder()
                .id(this.getId())
                .taskStatus(this.getTaskStatus())
                .build();
    }




}
