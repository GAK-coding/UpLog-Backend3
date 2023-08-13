package com.uplog.uplog.domain.task.dto;

import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.task.model.TaskStatus;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.team.model.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTaskRequest{
        private String taskName;
        private Long menuId;
        private Long teamId;
        private String taskDetail;
        private Long targetMemberId;
        private LocalDate startTime;
        private LocalDate endTime;

        public Task toEntity(Member targetMember,Menu menu, Team team) {
            return Task.builder()
                    .targetMember(targetMember)
                    .menu(menu)
                    .taskStatus(TaskStatus.PROGRESS_BEFORE)
                    .team(team)
                    .taskName(taskName)
                    .taskDetail(taskDetail)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();
        }
    }
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class CreateTaskRequest{
//        private String taskName;
//        private Long menuId;
//        private Long projectTeamId;
//        private String taskDetail;
//        private LocalDateTime startTime;
//        private LocalDateTime endTime;
//
//        public Task toEntity(Member targetMember,Menu menu, ProjectTeam projectTeam) {
//            return Task.builder()
//                    .targetMember(targetMember)
//                    .menu(menu)
//                    .taskStatus(TaskStatus.PROGRESS_BEFORE)
//                    .projectTeam(projectTeam)
//                    .taskName(taskName)
//                    .taskDetail(taskDetail)
//                    .startTime(startTime)
//                    .endTime(endTime)
//                    .build();
//        }
//    }



    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskInfoDTO{
        private Long id;
        private String taskName;
        private MemberDTO.PowerMemberInfoDTO targetMemberInfoDTO;
        //private Long targetMemberId;
        //private String targetmemberName;
        private Long menuId;
        private String menuName;
        private Long teamId;
        private String teamName;
        private Long parentTeamId;
        private TaskStatus taskStatus;
        private String taskDetail;
        private LocalDate startTime;
        private LocalDate endTime;
    }



//    @Getter
////    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class UpdateTaskData{
//        private Long id;
//        private String taskName;
//        private Member targetmember;
//        private Menu menu;
//        private ProjectTeam projectTeam;
//        private TaskStatus taskStatus;
//        private String taskDetail;
//        private LocalDateTime startTime;
//        private LocalDateTime endTime;
//    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskNameRequest{
        //private Long id;
        private String updatetaskName;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskDateRequest{
        //private Long id;
        private LocalDate updateStartTime;
        private LocalDate updateEndTime;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskMenuRequest{
        //private Long id;
        private Long updateMenuId;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskTeamRequest{
        //private Long id;
        private Long updateTeamId;

    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskMemberRequest{
        //private Long id;
        private Long updateTargetMemberId;

    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskContentRequest{
        //private Long id;
        private String updateContent;

    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskStatusRequest{
        //private Long id;
        private TaskStatus taskStatus;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PowerMemberInfoDTO {
        private Long id;
        private String name;
        private String nickname;
        private Position position;
    }

//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class TaskListDTO{
//        private List<TaskInfoDTO> beforeTask =new ArrayList<>();
//        private List<TaskInfoDTO> inTask =new ArrayList<>();
//        private List<TaskInfoDTO> completeTask =new ArrayList<>();
//    }
 //    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class UpdateTaskDTO{
//        private Long id;
//        private String taskName;
//        private Member targetmember;
//        private ProjectTeam projectTeam;
//        private Menu menu;
//        private TaskStatus taskStatus;
//        private String taskDetail;
//        private LocalDateTime startTime;
//        private LocalDateTime endTime;
//    }



}
