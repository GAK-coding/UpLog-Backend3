package com.uplog.uplog.domain.task.dto;

import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.task.model.TaskStatus;
import com.uplog.uplog.domain.menu.model.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskDTO {
    private Long id;
    private MemberDTO targetMember;
    private TaskStatus taskStatus;
    private String taskDetail;
    private LocalDate startTime;
    private LocalDate endTime;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskSaveRequest {
        private Member targetMember;
        private Menu menu;
        private TaskStatus taskStatus;
        private LocalDate startTime;
        private LocalDate endTime;

        public Task toEntity() {
            return Task.builder()
                    .targetMember(targetMember)
                    .menu(menu)
                    .taskStatus(taskStatus)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskInfoDTO{
        private Long id;
        private Member targetmember;
        private Menu menu;
        private TaskStatus taskStatus;
        private String taskDetail;
        private LocalDate startTime;
        private LocalDate endTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskRequest{
        private Long id;
        private Member targetmember;
        private Menu menu;
        private TaskStatus taskStatus;
        private String taskDetail;
        private LocalDate startTime;
        private LocalDate endTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskStatusRequest{
        private Long id;
        private TaskStatus taskStatus;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskDTO{
        private Long id;
        private Member targetmember;
        private Menu menu;
        private TaskStatus taskStatus;
        private String taskDetail;
        private LocalDate startTime;
        private LocalDate endTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class updateTaskStatusDTO{
        private Long id;
        private TaskStatus taskStatus;
    }


}
