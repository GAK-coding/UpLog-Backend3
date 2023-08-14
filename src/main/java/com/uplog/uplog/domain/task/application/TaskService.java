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
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.exception.NotFountTeamByProjectException;
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
import java.util.*;
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
    private final MemberTeamRepository memberTeamRepository;


    //========================================create========================================
    //task 생성

    @Transactional
    public TaskInfoDTO createTask(Long id,CreateTaskRequest createTaskRequest) {
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
        authorizedMethod.PostTaskValidateByMemberId(AuthorMember,rootTeam);
        authorizedMethod.PostTaskValidateByMemberId(targetMember,rootTeam);

        if (!projectTeam.getProject().getId().equals(menu.getProject().getId())) {
            throw new AuthorityException("해당 프로젝트 팀은 현재 프로젝트에 존재하지 않는 프로젝트팀 입니다.");
        }

        if(targetMember.getPosition()== Position.INDIVIDUAL){
            Long createIndex=findMaxIndexByTaskStatus(TaskStatus.PROGRESS_BEFORE);
            Task task = createTaskRequest.toEntity(targetMember,menu,projectTeam,createIndex);
            taskRepository.save(task);
            return task.toTaskInfoDTO();
        }
        else{
            //기업인경우
            throw new AuthorityException("테스크 생성 권한이 없습니다.");
        }
    }

    //========================================read========================================
    //task하나만읽기
    @Transactional(readOnly = true)
    public TaskInfoDTO findTaskById(Long id) {
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        return task.toTaskInfoDTO();
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
    public TaskInfoDTO updateTaskName(Long id,UpdateTaskNameRequest updateTaskNameRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskName(updateTaskNameRequest.getUpdatetaskName());

        return task.toTaskInfoDTO();
    }

    @Transactional
    public TaskInfoDTO updateTaskDate(Long id,UpdateTaskDateRequest updateTaskDateRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskDate(updateTaskDateRequest.getUpdateStartTime(),updateTaskDateRequest.getUpdateEndTime());

        return task.toTaskInfoDTO();
    }

    @Transactional
    public TaskInfoDTO updateTaskContent(Long id,UpdateTaskContentRequest updateTaskContentRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskContent(updateTaskContentRequest.getUpdateContent());
        System.out.println(task.getTaskName());
        System.out.println(task+"dakfjals;kdfj");
        return task.toTaskInfoDTO();
    }

    //멤버, 메뉴, 그룹 업데이트 짜야함
    @Transactional
    public TaskInfoDTO updateTaskMenu(Long id,UpdateTaskMenuRequest updateTaskMenuRequest){
        Menu menu = menuRepository.findById(updateTaskMenuRequest.getUpdateMenuId())
                .orElseThrow(() -> new NotFoundIdException("해당 메뉴는 존재하지 않습니다."));
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskMenu(menu);

        return task.toTaskInfoDTO();
    }

    @Transactional
    public TaskInfoDTO updateTaskMember(Long id,UpdateTaskMemberRequest updateTaskMemberRequest){
        Member member = memberRepository.findById(updateTaskMemberRequest.getUpdateTargetMemberId())
                .orElseThrow(() -> new NotFoundIdException("해당 멤버는 존재하지 않습니다."));
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        authorizedMethod.PostTaskValidateByMemberId(member,task.getTeam());
        task.updateTaskmember(member);

        return task.toTaskInfoDTO();
    }

    @Transactional
    public TaskInfoDTO updateTaskProjectTeam(Long id,UpdateTaskTeamRequest updateTaskTeamRequest) {
        Team projectTeam = teamRepository.findById(updateTaskTeamRequest.getUpdateTeamId())
                .orElseThrow(() -> new NotFoundIdException("해당 프로젝트팀은 존재하지 않습니다."));
        Task task = taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        //바꾸려는 팀이 프로젝트에 존재하지 않을때
        if (!projectTeam.getProject().getId().equals(task.getMenu().getProject().getId())) {
            throw new NotFountTeamByProjectException();
        }

        task.updateTaskTeam(projectTeam);

        return task.toTaskInfoDTO();
    }



    //task상태 변경(이건 아무곳에서나 변경 가능해서 로직 따로 뺐음)
    @Transactional
    public TaskInfoDTO updateTaskStatus(Long id,UpdateTaskStatusRequest UpdateTaskStatusRequest){
        Task task=taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        task.updateTaskStatus(UpdateTaskStatusRequest.getTaskStatus());

        return task.toTaskInfoDTO();
    }


    @Transactional
    public TaskInfoDTO updateTask(Long id, UpdateTaskRequest updateTaskRequest) {
        Task task = taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);

        // 업데이트 요청에 따라 필요한 정보만 업데이트
        boolean updated = false;

        if (updateTaskRequest.getUpdateTaskName() != null) {
            task.updateTaskName(updateTaskRequest.getUpdateTaskName());
            updated = true;
        }

        if (updateTaskRequest.getUpdateStartTime() != null || updateTaskRequest.getUpdateEndTime() != null) {
            task.updateTaskDate(updateTaskRequest.getUpdateStartTime(), updateTaskRequest.getUpdateEndTime());
            updated = true;
        }

        if (updateTaskRequest.getUpdateTaskDetail() != null) {
            task.updateTaskContent(updateTaskRequest.getUpdateTaskDetail());
            updated = true;
        }

        if (updateTaskRequest.getUpdateMenuId() != null) {
            Menu menu = menuRepository.findById(updateTaskRequest.getUpdateMenuId())
                    .orElseThrow(() -> new NotFoundIdException("해당 메뉴는 존재하지 않습니다."));
            task.updateTaskMenu(menu);
            updated = true;
        }

        if (updateTaskRequest.getUpdateTargetMemberId() != null) {
            Member member = memberRepository.findById(updateTaskRequest.getUpdateTargetMemberId())
                    .orElseThrow(() -> new NotFoundIdException("해당 멤버는 존재하지 않습니다."));
            authorizedMethod.PostTaskValidateByMemberId(member, task.getTeam());
            task.updateTaskmember(member);
            updated = true;
        }

        if (updateTaskRequest.getUpdateTeamId() != null) {
            Team projectTeam = teamRepository.findById(updateTaskRequest.getUpdateTeamId())
                    .orElseThrow(() -> new NotFoundIdException("해당 프로젝트팀은 존재하지 않습니다."));
            // 바꾸려는 팀이 프로젝트에 존재하지 않을 때
            if (!projectTeam.getProject().getId().equals(task.getMenu().getProject().getId())) {
                throw new NotFountTeamByProjectException();
            }
            task.updateTaskTeam(projectTeam);
            updated = true;
        }

        if (updateTaskRequest.getUpdateTaskStatus() != null) {
            task.updateTaskStatus(updateTaskRequest.getUpdateTaskStatus());
            updated = true;
        }

        if (updated) {
            taskRepository.save(task);
        }
        return task.toTaskInfoDTO();
    }

    //========================================delete========================================
    @Transactional
    public String deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        taskRepository.delete(task);
        return "delete";
    }

    @Transactional
    public Long findMaxIndexByTaskStatus(TaskStatus taskStatus) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QTask qTask = QTask.task;

        Long maxIndex = query
                .select(qTask.taskIndex.max().coalesce(0L).add(1L))
                .from(qTask)
                .where(qTask.taskStatus.eq(taskStatus))
                .fetchOne();

        return (maxIndex != null) ? maxIndex : 1L; // maxIndex가 null일 경우 1을 반환
    }

    @Transactional
    public void updateTaskOrder(List<Long> taskIds, TaskStatus status) {
        // 해당 상태에서의 최대 인덱스 조회
        Long maxIndex = findMaxIndexByTaskStatus(status);

        // 중복을 방지하기 위해 이미 존재하는 인덱스 값을 저장하는 Set
        Set<Long> usedIndexes = new HashSet<>();

        // 선택된 테스크들의 인덱스를 조정
        for (Long taskId : taskIds) {
            Task task = taskRepository.findById(taskId).orElseThrow(NotFoundTaskByIdException::new);

            // 이미 사용 중인 인덱스인 경우, 현재 최대 인덱스에 1을 더한 값을 사용
            if (usedIndexes.contains(task.getTaskIndex())) {
                task.updateTaskIndex(maxIndex + 1);
                maxIndex++;
            } else {
                // 중복이 아니라면 현재 인덱스로 설정하고, 사용 중인 인덱스 목록에 추가
                task.updateTaskIndex(task.getTaskIndex());
                usedIndexes.add(task.getTaskIndex());
            }
        }

        // 변경된 순서의 Task들을 저장
        taskRepository.saveAll(taskRepository.findAllById(taskIds));
    }

}
