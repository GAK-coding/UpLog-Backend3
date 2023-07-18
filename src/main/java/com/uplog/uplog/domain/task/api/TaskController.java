package com.uplog.uplog.domain.task.api;

import com.uplog.uplog.domain.member.api.TestController;
import com.uplog.uplog.domain.task.application.TaskService;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.task.model.Task;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;
//    @Operation(summary = "task 생성", description = "task 생성", tags = { "Task Controller" })
//    // response 코드 별로 응답 시 내용(설명) 작성
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(schema = @Schema(implementation = TestController.class))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
//            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
//    })
    @PostMapping(value="/task")
    public ResponseEntity<TaskInfoDTO> createTask(@RequestBody TaskSaveRequest taskSaveRequest) {
        Task createdTask = taskService.createTask(taskSaveRequest);
        TaskInfoDTO taskInfoDTO = createdTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //조회
    @GetMapping("/task/{id}")
    public ResponseEntity<TaskInfoDTO> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        TaskInfoDTO taskInfoDTO = task.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //수정
    @PutMapping("/task/{id}")
    public ResponseEntity<TaskInfoDTO> updateTask(@PathVariable Long id, @RequestBody UpdateTaskData updateTaskData) {
        Task updatedTask = taskService.updateTask(id,updateTaskData);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //상태 수정
    @PatchMapping("/task/taskStatus/{id}")
    public ResponseEntity<TaskInfoDTO> updateTaskStatus(@PathVariable Long id, @RequestBody UpdateTaskStatusData updateTaskStatusData) {
        Task updatedTask = taskService.updateTaskStatus(id,updateTaskStatusData);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //삭제
    @DeleteMapping("/task/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }



}
