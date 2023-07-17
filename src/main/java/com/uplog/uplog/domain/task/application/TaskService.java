package com.uplog.uplog.domain.task.application;

import com.uplog.uplog.domain.task.dao.TaskRepository;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.task.exception.handler.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    //task 생성

    @Transactional
    public Task createTask(TaskSaveRequest taskSaveRequest) {
        Task task = taskSaveRequest.toEntity();
        return taskRepository.save(task);
    }

    //task읽기
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
    }

    //변경사항 있는것만 확인하고 걔네만 업데이트 하는걸로 바꿔야함
    @Transactional
    public Task updateTask(UpdateTaskRequest updateTaskRequest) {
        Task task = taskRepository.findById(updateTaskRequest.getId()).orElseThrow(NotFoundTaskByIdException::new);
        task.toUpdateTaskDTO(updateTaskRequest);
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTaskStatus(UpdateTaskStatusRequest updateTaskStatusRequest){
        Task task=taskRepository.findById(updateTaskStatusRequest.getId()).orElseThrow(NotFoundTaskByIdException::new);
        task.toUpdateTaskStatusDTO(updateTaskStatusRequest);
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        taskRepository.delete(task);
    }
}
