package com.uplog.uplog.domain.task.api;

import com.uplog.uplog.domain.member.api.TestController;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.task.application.TaskService;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.task.model.TaskStatus;
import com.uplog.uplog.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;
    private final MemberRepository memberRepository;

    @PostMapping(value="/tasks")
    public ResponseEntity<TaskInfoDTO> createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Task createdTask = taskService.createTask(memberId,createTaskRequest);
        TaskInfoDTO taskInfoDTO = createdTask.toTaskInfoDTO();
        return new ResponseEntity<>(taskInfoDTO, HttpStatus.CREATED);
    }

    //개별조회
    @GetMapping("/tasks/{task-id}")
    public ResponseEntity<TaskInfoDTO> findTaskById(@PathVariable(name="task-id") Long id) {
        Task task = taskService.findTaskById(id);
        TaskInfoDTO taskInfoDTO = task.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //전체조회
    @GetMapping("/tasks/all/{project-id}")
    public ResponseEntity<List<TaskInfoDTO>> findAllTaskByProjectId(@PathVariable(name="project-id")Long projectId) {
        List<TaskInfoDTO> taskInfoDTOs = taskService.findAllTask(projectId);
        return new ResponseEntity<>(taskInfoDTOs, HttpStatus.OK);
    }

    //전체조회
    @GetMapping("/tasks/allByStatus/{project-id}")
    public ResponseEntity<Map<TaskStatus, List<TaskInfoDTO>>> findAllTasksByStatus(@PathVariable(name="project-id")Long projectId) {
        Map<TaskStatus, List<TaskInfoDTO>> taskInfoDTOMap = taskService.findAllTasksByStatus(projectId);
        return ResponseEntity.ok(taskInfoDTOMap);
    }



    //status별로 조회
    @GetMapping("/tasks/{taskStatus}/status")
    public ResponseEntity<List<TaskInfoDTO>> findTaskByStatus(@PathVariable(name="taskStatus")TaskStatus taskStatus,Long projectId) {
        List<TaskInfoDTO> taskInfoDTOs = taskService.findTaskByStatus(projectId,taskStatus);
        return new ResponseEntity<>(taskInfoDTOs, HttpStatus.OK);
    }



//    @GetMapping("/tasks/{menu-id}")
//    public ResponseEntity<List<TaskInfoDTO>> findTaskByMenuId(@PathVariable(name="menu-Id") Long id) {
//        List<TaskInfoDTO> taskInfoDTOS = taskService.findByMenuId(id);
//        return new ResponseEntity<>(taskInfoDTOS, HttpStatus.OK);
//    }

//    //수정
//    @PutMapping("/task/{id}")
//    public ResponseEntity<TaskInfoDTO> updateTask(@PathVariable Long id, @RequestBody UpdateTaskData updateTaskData) {
//        Task updatedTask = taskService.updateTask(id,updateTaskData);
//        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
//        return ResponseEntity.ok(taskInfoDTO);
//    }



    //수정부분에서 path에 task_id가 있어서 dto에 id빼도 되는데 혹시 몰라서 일단 넣어둠
    //이름 수정
    @PatchMapping("/tasks/{task-id}/title")
    public ResponseEntity<TaskInfoDTO> updateTaskName(@PathVariable(name="task-id") Long id, @RequestBody UpdateTaskNameRequest updateTaskNameRequest) {
        Task updatedTask = taskService.updateTaskName(id,updateTaskNameRequest);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //날짜 수정(시작날짜,종료날짜)
    @PatchMapping("/tasks/{task_id}/date")
    public ResponseEntity<TaskInfoDTO> updateTaskDate(@PathVariable(name="task-id") Long id, @RequestBody UpdateTaskDateRequest updateTaskDateRequest) {
        Task updatedTask = taskService.updateTaskDate(id,updateTaskDateRequest);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //상세 내용 수정
    @PatchMapping("/tasks/{task-id}/content")
    public ResponseEntity<TaskInfoDTO> updateTaskContent(@PathVariable(name="task-id") Long id, @RequestBody UpdateTaskContentRequest updateTaskContentRequest) {
        Task updatedTask = taskService.updateTaskContent(id,updateTaskContentRequest);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //target멤버 수정
    @PatchMapping("/tasks/{task-id}/target-Member")
    public ResponseEntity<TaskInfoDTO> updateTaskStatus(@PathVariable(name="task-id") Long id, @RequestBody UpdateTaskMemberRequest updateTaskMemberRequest) {
        Task updatedTask = taskService.updateTaskMember(id,updateTaskMemberRequest);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //Menu수정
    @PatchMapping("/tasks/{task-id}/menu")
    public ResponseEntity<TaskInfoDTO> updateTaskMenu(@PathVariable(name="task-id") Long id, @RequestBody UpdateTaskMenuRequest updateTaskMenuRequest) {
        Task updatedTask = taskService.updateTaskMenu(id,updateTaskMenuRequest);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }


    //테스크 팀 수정
    @PatchMapping("/tasks/{task-id}/taskTeam")
    public ResponseEntity<TaskInfoDTO> updateTaskTeam(@PathVariable(name="task-id") Long id, @RequestBody UpdateTaskTeamRequest updateTaskTeamRequest) {
        Task updatedTask = taskService.updateTaskProjectTeam(id,updateTaskTeamRequest);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }


    //상태 수정
    @PatchMapping("/tasks/{task-id}/status")
    public ResponseEntity<TaskInfoDTO> updateTaskStatus(@PathVariable(name="task-id") Long id, @RequestBody UpdateTaskStatusRequest UpdateTaskStatusRequest) {
        Task updatedTask = taskService.updateTaskStatus(id,UpdateTaskStatusRequest);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //삭제
    @DeleteMapping("/tasks/{task-id}")
    public ResponseEntity<Void> deleteTask(@PathVariable(name="task-id") Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }



}
