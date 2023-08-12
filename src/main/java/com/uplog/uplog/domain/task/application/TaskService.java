package com.uplog.uplog.domain.task.application;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.member.model.QMember;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.task.dao.TaskRepository;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.task.model.QTask;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.task.exception.*;
import com.uplog.uplog.domain.task.model.TaskStatus;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.method.AuthorizedMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    @PersistenceContext
    private EntityManager entityManager;

    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final MenuRepository menuRepository;
    private final AuthorizedMethod authorizedMethod;


    //========================================create========================================
    //task 생성

    @Transactional
    public Task createTask(Long id,CreateTaskRequest createTaskRequest) {
        Member AuthorMember = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundIdException("해당 멤버는 존재하지 않습니다."));

        Member targetMember=memberRepository.findById(createTaskRequest.getTargetMemberId())
                .orElseThrow(() -> new NotFoundIdException("해당 멤버는 존재하지 않습니다."));

        Menu menu = menuRepository.findById(createTaskRequest.getMenuId())
                .orElseThrow(() -> new NotFoundIdException("해당 메뉴는 존재하지 않습니다."));

        Team projectTeam = teamRepository.findById(createTaskRequest.getTeamId())
                .orElseThrow(() -> new NotFoundIdException("해당 프로젝트팀은 존재하지 않습니다."));
        Team rootTeam = teamRepository.findByProjectIdAndName(menu.getProject().getId(), menu.getProject().getVersion()).orElseThrow(NotFoundIdException::new);

        //테스크 생성자, 타겟멤버 둘다 권한 확인
        authorizedMethod.CreatePostTaskValidateByMemberId(AuthorMember,rootTeam);

        if (!projectTeam.getProject().getId().equals(menu.getProject().getId())) {
            throw new AuthorityException("해당 프로젝트 팀은 현재 프로젝트에 존재하지 않는 프로젝트팀 입니다.");
        }

        if(targetMember.getPosition()== Position.INDIVIDUAL){
            Task task = createTaskRequest.toEntity(targetMember,menu,projectTeam);
            taskRepository.save(task);
            return task;
        }
        else{
            //기업인경우
            throw new AuthorityException("테스크 생성 권한이 없습니다.");
        }
    }

    //========================================read========================================
    //task하나만읽기
    @Transactional(readOnly = true)
    public Task findTaskById(Long id) {
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        return task;
    }

    //해당 프로젝트의 전체 테스크 조회
    @Transactional(readOnly = true)
    public List<TaskInfoDTO> findAllTask(Long projectId) {
        // 프로젝트 아이디를 가지고 있는 메뉴 리스트 조회
        List<Menu> menuList = menuRepository.findByProjectId(projectId);

        List<TaskInfoDTO> taskList = new ArrayList<>();

        // 각 메뉴에 해당하는 테스크들을 찾아서 taskList에 추가
        for (Menu menu : menuList) {
            List<Task> tasks = taskRepository.findByMenuId(menu.getId());
            for (Task task : tasks) {
                TaskInfoDTO taskInfoDTO = TaskInfoDTO.builder()
                        .id(task.getId())
                        .taskName(task.getTaskName())
                        .targetMemberInfoDTO(new MemberDTO.PowerMemberInfoDTO(
                                task.getTargetMember().getId(),
                                task.getTargetMember().getName(),
                                task.getTargetMember().getNickname(),
                                task.getTargetMember().getPosition()
                        ))
                        .menuId(menu.getId())
                        .menuName(menu.getMenuName())
                        .teamId(task.getTeam().getId())
                        .teamName(task.getTeam().getName())
                        .taskStatus(task.getTaskStatus())
                        .taskDetail(task.getTaskDetail())
                        .startTime(task.getStartTime())
                        .endTime(task.getEndTime())
                        .build();

                taskList.add(taskInfoDTO);
            }
        }

        return taskList;
    }

    public Map<TaskStatus, List<TaskInfoDTO>> findAllTasksByStatus(Long projectId) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QTask task = QTask.task;

        List<Task> tasks = query
                .selectFrom(task)
                .where(task.menu.project.id.eq(projectId))
                .fetch();

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

    public List<TaskInfoDTO> findTaskByStatus(Long projectId, TaskStatus taskStatus) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QTask task = QTask.task;

        List<Task> tasks = query
                .selectFrom(task)
                .where(task.menu.project.id.eq(projectId).and(task.taskStatus.eq(taskStatus)))
                .fetch();

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
//    @Transactional(readOnly = true)
//    public List<TaskInfoDTO> findByMenuId(Long menuId){
//        List<Task> taskList=taskRepository.findByMenuId(menuId);
//        List<TaskInfoDTO> taskInfoDTOS=new ArrayList<>();
//        for(Task task:taskList){
//            TaskInfoDTO taskInfoDTO=task.toTaskInfoDTO();
//            taskInfoDTOS.add(taskInfoDTO);
//        }
//        return taskInfoDTOS;
//    }
    public List<Task> findByMenuId(Long menuId) {
        List<Task> taskList = taskRepository.findByMenuId(menuId);
        return taskList;
    }

    @Transactional(readOnly = true)
    public Page<Task> findPageByMenuId(Long menuId, Pageable pageable) {
        return taskRepository.findByMenuId(menuId, pageable);
    }

//    public Slice<Task> findSliceByMenuId(Long menuId, Pageable pageable) {
//        return taskRepository.findSliceByMenuId(menuId, pageable);
//    }




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
                .orElseThrow(() -> new NotFoundIdException("해당 메뉴는 존재하지 않습니다."));
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskMenu(menu);

        return task;
    }

    @Transactional
    public Task updateTaskMember(Long id,UpdateTaskMemberRequest updateTaskMemberRequest){
        Member member = memberRepository.findById(updateTaskMemberRequest.getUpdateTargetMemberId())
                .orElseThrow(() -> new NotFoundIdException("해당 멤버는 존재하지 않습니다."));
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskmember(member);

        return task;
    }

    @Transactional
    public Task updateTaskProjectTeam(Long id,UpdateTaskTeamRequest updateTaskTeamRequest) {
        Team projectTeam = teamRepository.findById(updateTaskTeamRequest.getUpdateTeamId())
                .orElseThrow(() -> new NotFoundIdException("해당 프로젝트팀은 존재하지 않습니다."));
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
