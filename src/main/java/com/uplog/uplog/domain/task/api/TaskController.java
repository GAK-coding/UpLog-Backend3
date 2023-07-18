package com.uplog.uplog.domain.task.api;

import com.uplog.uplog.domain.task.application.TaskService;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.task.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;

    //task 생성
    @PostMapping(value="/task")
    public ResponseEntity<TaskInfoDTO> createTask(@RequestBody TaskSaveRequest taskSaveRequest) {
        Task createdTask = taskService.createTask(taskSaveRequest);
        TaskInfoDTO taskInfoDTO = createdTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //테스크 조회
    @GetMapping("/{id}")
    public ResponseEntity<TaskInfoDTO> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        TaskInfoDTO taskInfoDTO = task.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //테스크 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<TaskInfoDTO> updateTask(@PathVariable Long id, @RequestBody UpdateTaskData updateTaskData) {
        Task updatedTask = taskService.updateTask(id,updateTaskData);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //테스크 상태 업데이트
    @PatchMapping("taskStatus/{id}")
    public ResponseEntity<TaskInfoDTO> updateTaskStatus(@PathVariable Long id, @RequestBody UpdateTaskStatusData updateTaskStatusData) {
        Task updatedTask = taskService.updateTaskStatus(id,updateTaskStatusData);
        TaskInfoDTO taskInfoDTO = updatedTask.toTaskInfoDTO();
        return ResponseEntity.ok(taskInfoDTO);
    }

    //테스크 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }



}
