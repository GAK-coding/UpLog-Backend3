package com.uplog.uplog.domain.task.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.task.dao.TaskRepository;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.task.exception.handler.*;
import com.uplog.uplog.domain.team.dao.ProjectTeamRepository;
import com.uplog.uplog.domain.team.model.ProjectTeam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final ProjectTeamRepository teamRepository;
    private final MenuRepository menuRepository;


    //task 생성

    @Transactional
    public Task createTask(Long id,CreateTaskRequest createTaskRequest) {
        Member targetMember = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Menu menu = menuRepository.findById(createTaskRequest.getMenuId())
                .orElseThrow(() -> new RuntimeException("Menu not found"));

        ProjectTeam projectTeam = teamRepository.findById(createTaskRequest.getProjectTeamId())
                .orElseThrow(() -> new RuntimeException("ProjectTeam not found"));

        Task task = createTaskRequest.toEntity(targetMember,menu,projectTeam);

//        Task task = taskSaveRequest.toEntity();

        taskRepository.save(task);
        return task;
    }

    //task읽기
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        return task;
    }

//    변경사항 있는것만 확인하고 걔네만 업데이트 하는걸->task에서 처리함
//    @Transactional
//    public Task updateTask(Long id,UpdateTaskData updateTaskData) {
//        Task task = taskRepository.findById(updateTaskData.getId()).orElseThrow(NotFoundTaskByIdException::new);
//        if(!updateTaskData.getId().equals(id)){
//            throw new NotMatchTaskToUpdateException();
//        }
//        else{
//            task.UpdateTask(updateTaskData);
//            taskRepository.save(task);
//        }
//        return task;
//    }


    @Transactional
    public Task updateTaskName(Long id,UpdateTaskNameRequest updateTaskNameRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskName(updateTaskNameRequest.getUpdatetaskName());
        taskRepository.save(task);

        return task;
    }

    @Transactional
    public Task updateTaskDate(Long id,UpdateTaskDateRequest updateTaskDateRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskDate(updateTaskDateRequest.getUpdateStartTime(),updateTaskDateRequest.getUpdateEndTime());
        taskRepository.save(task);

        return task;
    }

    @Transactional
    public Task updateTaskContent(Long id,UpdateTaskContentRequest updateTaskContentRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskContent(updateTaskContentRequest.getUpdateContent());
        taskRepository.save(task);

        return task;
    }

    //멤버, 메뉴, 그룹 업데이트 짜야함
    @Transactional
    public Task updateTaskMenu(Long id,UpdateTaskMenuRequest updateTaskMenuRequest){
        Menu menu = menuRepository.findById(updateTaskMenuRequest.getUpdateMenuId())
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskMenu(menu);
        taskRepository.save(task);

        return task;
    }

    @Transactional
    public Task updateTaskMember(Long id,UpdateTaskMemberRequest updateTaskMemberRequest){
        Member member = memberRepository.findById(updateTaskMemberRequest.getUpdateTargetMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskmember(member);
        taskRepository.save(task);

        return task;
    }

    @Transactional
    public Task updateTaskProjectTeam(Long id,UpdateTaskTeamRequest updateTaskTeamRequest) {
        ProjectTeam projectTeam = teamRepository.findById(updateTaskTeamRequest.getUpdateTeamId())
                .orElseThrow(() -> new RuntimeException("ProjectTeam not found"));
        Task task = taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskTeam(projectTeam);
        taskRepository.save(task);

        return task;
    }



    //task상태 변경(이건 아무곳에서나 변경 가능해서 로직 따로 뺐음)
    @Transactional
    public Task updateTaskStatus(Long id,UpdateTaskStatusRequest UpdateTaskStatusRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskStatus(UpdateTaskStatusRequest.getTaskStatus());
        taskRepository.save(task);

        return task;
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        taskRepository.delete(task);
    }
}
