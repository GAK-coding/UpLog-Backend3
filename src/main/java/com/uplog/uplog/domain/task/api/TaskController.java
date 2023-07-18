package com.uplog.uplog.domain.task.api;

import com.uplog.uplog.domain.task.application.TaskService;
import com.uplog.uplog.domain.task.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

//    @PostMapping
//    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
//        TaskDTO createdTask = taskService.createTask(taskDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
//    }
//
//    @GetMapping("/{taskId}")
//    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long taskId) {
//        TaskDTO taskDTO = taskService.getTaskById(taskId);
//        return ResponseEntity.ok(taskDTO);
//    }
//
//    @PutMapping("/{taskId}")
//    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long taskId, @RequestBody TaskDTO taskDTO) {
//        TaskDTO updatedTask = taskService.updateTask(taskId, taskDTO);
//        return ResponseEntity.ok(updatedTask);
//    }
//
//    @DeleteMapping("/{taskId}")
//    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
//        taskService.deleteTask(taskId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping
//    public ResponseEntity<List<TaskDTO>> getAllTasks() {
//        List<TaskDTO> tasks = taskService.getAllTasks();
//        return ResponseEntity.ok(tasks);
//    }
}

