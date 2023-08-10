package com.uplog.uplog.domain.task.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.task.dao.TaskRepository;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.task.exception.*;
import com.uplog.uplog.domain.task.model.TaskStatus;
import com.uplog.uplog.domain.team.dao.ProjectTeamRepository;
import com.uplog.uplog.domain.team.model.ProjectTeam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final ProjectTeamRepository teamRepository;
    private final MenuRepository menuRepository;


    //========================================create========================================
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
//        Task task = createTaskRequest.toEntity(targetMember);


        //Task task = taskSaveRequest.toEntity();

        taskRepository.save(task);
        return task;
    }

    //========================================read========================================
    //task하나만읽기
    @Transactional(readOnly = true)
    public Task findTaskById(Long id) {
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        return task;
    }

    public Map<TaskStatus, List<TaskInfoDTO>> findAllTasksByStatus() {
        // 모든 테스크를 조회
        List<Task> tasks = taskRepository.findAll();

        // 상태별로 테스크를 그룹화하여 Map에 저장
        Map<TaskStatus, List<Task>> taskStatusMap = tasks.stream()
                .collect(Collectors.groupingBy(Task::getTaskStatus));

        // 상태별로 TaskInfoDTO로 변환하여 리스트에 저장
        Map<TaskStatus, List<TaskInfoDTO>> taskInfoDTOMap = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            List<Task> taskList = taskStatusMap.getOrDefault(status, new ArrayList<>());
            List<TaskInfoDTO> taskInfoDTOList = taskList.stream()
                    .map(Task::toTaskInfoDTO)
                    .collect(Collectors.toList());
            taskInfoDTOMap.put(status, taskInfoDTOList);
        }

        return taskInfoDTOMap;
    }

    public List<TaskInfoDTO> findTaskByStatus(TaskStatus taskStatus) {
        List<Task> tasks = taskRepository.findByTaskStatus(taskStatus);
        return tasks.stream()
                .map(Task::toTaskInfoDTO)
                .collect(Collectors.toList());
    }

//    public List<TaskInfoDTO> getAllTaskInfoDTOs(TaskStatus status) {
//        List<Task> tasks = taskRepository.findByTaskStatus(status);
//        List<Task> sortedTasks = sortTasksByCustomOrder(tasks); // 순서를 정렬하는 메서드 호출
//        return sortedTasks.stream()
//                .map(Task::toTaskInfoDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<TaskInfoDTO> getAllTaskInfoDTOss(TaskStatus status) {
//        List<Task> tasks = taskRepository.findByTaskStatus(status);
//        List<Task> taskList=sortTasksByCustomOrder(tasks);
//        List<TaskInfoDTO> taskInfoDTOSss=new ArrayList<>();
//        for(Task task:taskList){
//            TaskInfoDTO taskInfoDTO=task.toTaskInfoDTO();
//            taskInfoDTOSss.add(taskInfoDTO);
//        }
//        return taskInfoDTOSss;
//    }

//    private List<Task> sortTasksByCustomOrder(List<Task> tasks) {
//        if (tasks.size() >= 1) {
//            Task temp = tasks.get(1);
//            tasks.set(1, tasks.get(2));
//            tasks.set(2, temp);
//        }
//        return tasks;
//    }


    //메뉴별로 읽기
    @Transactional(readOnly = true)
    public List<TaskInfoDTO> findByMenuId(Long menuId){
        List<Task> taskList=taskRepository.findByMenuId(menuId);
        List<TaskInfoDTO> taskInfoDTOS=new ArrayList<>();
        for(Task task:taskList){
            TaskInfoDTO taskInfoDTO=task.toTaskInfoDTO();
            taskInfoDTOS.add(taskInfoDTO);
        }
        return taskInfoDTOS;
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

    //========================================update========================================
    @Transactional
    public Task updateTaskName(Long id,UpdateTaskNameRequest updateTaskNameRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskName(updateTaskNameRequest.getUpdatetaskName());

        return task;
    }

    @Transactional
    public Task updateTaskDate(Long id,UpdateTaskDateRequest updateTaskDateRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskDate(updateTaskDateRequest.getUpdateStartTime(),updateTaskDateRequest.getUpdateEndTime());

        return task;
    }

    @Transactional
    public Task updateTaskContent(Long id,UpdateTaskContentRequest updateTaskContentRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskContent(updateTaskContentRequest.getUpdateContent());

        return task;
    }

    //멤버, 메뉴, 그룹 업데이트 짜야함
    @Transactional
    public Task updateTaskMenu(Long id,UpdateTaskMenuRequest updateTaskMenuRequest){
        Menu menu = menuRepository.findById(updateTaskMenuRequest.getUpdateMenuId())
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskMenu(menu);

        return task;
    }

    @Transactional
    public Task updateTaskMember(Long id,UpdateTaskMemberRequest updateTaskMemberRequest){
        Member member = memberRepository.findById(updateTaskMemberRequest.getUpdateTargetMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskmember(member);

        return task;
    }

    @Transactional
    public Task updateTaskProjectTeam(Long id,UpdateTaskTeamRequest updateTaskTeamRequest) {
        ProjectTeam projectTeam = teamRepository.findById(updateTaskTeamRequest.getUpdateTeamId())
                .orElseThrow(() -> new RuntimeException("ProjectTeam not found"));
        Task task = taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskTeam(projectTeam);

        return task;
    }



    //task상태 변경(이건 아무곳에서나 변경 가능해서 로직 따로 뺐음)
    @Transactional
    public Task updateTaskStatus(Long id,UpdateTaskStatusRequest UpdateTaskStatusRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskStatus(UpdateTaskStatusRequest.getTaskStatus());

        return task;
    }


    //========================================delete========================================
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        taskRepository.delete(task);
    }
}
