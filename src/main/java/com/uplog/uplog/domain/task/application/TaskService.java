package com.uplog.uplog.domain.task.application;

import com.uplog.uplog.domain.task.dao.TaskRepository;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.task.exception.handler.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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

//    변경사항 있는것만 확인하고 걔네만 업데이트 하는걸->task에서 처리함
    @Transactional
    public Task updateTask(Long id,UpdateTaskData updateTaskData) {
        Task task = taskRepository.findById(updateTaskData.getId()).orElseThrow(NotFoundTaskByIdException::new);
        if(!updateTaskData.getId().equals(id)){
            throw new NotMatchTaskToUpdateException();
        }
        else{
            task.UpdateTask(updateTaskData);
        }
        return taskRepository.save(task);
    }

    //task상태 변경(이건 아무곳에서나 변경 가능해서 로직 따로 뺐음)
    @Transactional
    public Task updateTaskStatus(Long id,UpdateTaskStatusData updateTaskStatusData){
        Task task=taskRepository.findById(updateTaskStatusData.getId()).orElseThrow(NotFoundTaskByIdException::new);
        if(!updateTaskStatusData.getId().equals(id)){
            throw new NotMatchTaskToUpdateException();
        }
        else{
            task.updateTaskStatus(updateTaskStatusData.getTaskStatus());
        }
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        taskRepository.delete(task);
    }
}
