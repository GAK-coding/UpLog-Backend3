package com.uplog.uplog.domain.team.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.VerySimpleMemberInfoDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.application.ProductMemberService;
import com.uplog.uplog.domain.product.dao.ProductMemberRepository;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.ProductMember;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.task.dao.TaskRepository;
import com.uplog.uplog.domain.task.model.TaskStatus;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.*;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.SimpleMemberPowerInfoDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.TeamAndPowerTypeDTO;
import com.uplog.uplog.domain.team.exception.CanNotDeleteMemberToTeam;
import com.uplog.uplog.domain.team.exception.CanNotDeleteTeamException;
import com.uplog.uplog.domain.team.exception.NotFoundMemberInTeamException;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.*;
import com.uplog.uplog.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProjectRepository projectRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final ProductMemberRepository productMemberRepository;
    private final TaskRepository taskRepository;


    private final ProductMemberService productMemberService;
    private final MemberTeamService memberTeamService;
    private final MailService mailService;

    @Transactional
    public CreateTeamResultDTO createTeam(Long currentMemberId, Long projectId, CreateTeamRequest createTeamRequest) throws Exception {
        createTeamRequest.getMemberIdList().add(currentMemberId);
        Project project = projectRepository.findById(projectId).orElseThrow(NotFoundIdException::new);
        //멤버가 현재 프로젝트에 속한사람인지 확인
        Team rootTeam = teamRepository.findByProjectIdAndName(projectId, project.getVersion()).orElseThrow(
                ()->new NotFoundIdException("rootTeam의 객체를 찾을 수 없습니다."));
        if (!memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(currentMemberId, rootTeam.getId())) {
            throw new AuthorityException("팀에 속하지 않은 멤버로, 팀 생성 권한이 없습니다.");
        }
        for(Team t : project.getTeamList()){
            if(t.getName().equals(createTeamRequest.getName())){
                throw new DuplicatedNameException("프로젝트 내에서 팀 이름이 중복됩니다.");
            }
        }
//            Team parentTeam = teamRepository.findTeamById(createTeamRequest.getParentTeamId());
        //생성 외에 부모가 널로 들어왔다면, 프로젝트 생성시에 만들어진 젠체 그룹으로 부모넣어주기
        if (createTeamRequest.getParentTeamId() != null) {
            List<Long> duplicatedMemberIdList = new ArrayList<>();
            Team parentTeam = teamRepository.findById(createTeamRequest.getParentTeamId()).orElseThrow(
                    ()-> new NotFoundIdException("부모팀 객체를 찾을 수 없습니다."));
            //팀의 최대 depth는 2임. 부모가 2가 되면 안됨
            if(parentTeam.getDepth()==2){
                throw new DepthException("팀의 최대 depth를 초과했습니다.");
            }
            Team team = createTeamRequest.toEntity(project, parentTeam,parentTeam.getDepth()+1);
            teamRepository.save(team);

            //PowerType알아내기 --> 어떻게 알아내지  -> memberProduct에서 찾자. team을 이름으로 찾고 team이랑 멤버 아이디로 해도 되는데 그럼 중복된 이름이 걸릴 수 있음.
            for (Long memberId : createTeamRequest.getMemberIdList()) {
                if(memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(memberId, team.getId())){
                    duplicatedMemberIdList.add(memberId);
                }
                ProductMember memberProduct = productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, project.getProduct().getId()).orElseThrow(NotFoundIdException::new);
                CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
                        .teamId(team.getId())
                        .memberId(memberId)
                        .powerType(memberProduct.getPowerType() == PowerType.MASTER || memberProduct.getPowerType() == PowerType.LEADER ? PowerType.LEADER : memberProduct.getPowerType())
                        .link(createTeamRequest.getLink())
                        .build();
                memberTeamService.createMemberTeam(createMemberTeamRequest);
            }
            return CreateTeamResultDTO.builder()
                    .id(team.getId())
                    .DuplicatedMemberList(duplicatedMemberIdList)
                    .build();
        } else {
            //부모를 입력 안해줬을 경우, 루트 밑으로 들어가니까 뎁스는 당연히 1
            List<Long> duplicatedMemberIdList = new ArrayList<>();
            Team parentTeam = teamRepository.findByProjectIdAndName(projectId, project.getVersion()).orElseThrow(NotFoundIdException::new);
            Team team = createTeamRequest.toEntity(project, parentTeam,1);
            teamRepository.save(team);

            for (Long memberId : createTeamRequest.getMemberIdList()) {
                if(memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(memberId, team.getId())){
                    duplicatedMemberIdList.add(memberId);
                }
                ProductMember memberProduct = productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, project.getProduct().getId()).orElseThrow(NotFoundIdException::new);
                CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
                        .teamId(team.getId())
                        .memberId(memberId)
                        .powerType(memberProduct.getPowerType() == PowerType.MASTER || memberProduct.getPowerType() == PowerType.LEADER ? PowerType.LEADER : memberProduct.getPowerType())
                        .link(createTeamRequest.getLink())
                        .build();
                memberTeamService.createMemberTeam(createMemberTeamRequest);
            }
            return CreateTeamResultDTO.builder()
                    .id(team.getId())
                    .DuplicatedMemberList(duplicatedMemberIdList)
                    .build();

        }

    }

    //=======================Read===========================
    //팀에 속한 멤버 출력
    @Transactional(readOnly = true)
    public List<MemberPowerDTO> findMembersByTeamId(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
        List<MemberTeam> memberTeamList = memberTeamRepository.findMemberTeamsByTeamId(teamId);

        List<MemberPowerDTO> memberPowerDTOList = new ArrayList<>();
        for (MemberTeam mt : memberTeamList) {
            memberPowerDTOList.add(mt.toMemberPowerDTO());
        }
        return memberPowerDTOList;
    }

    //프로젝트와 멤버아이디로 멤버가 속한 팀을 보고싶을 때. -> 파워타입까지 확인해야하기 때문에 memberTeam을 queryDSL로 한 거임.
    @Transactional(readOnly = true)
    public TeamsBysMemberAndProject findTeamsByMemberIdAndProjectId(Long memberId, Long projectId){
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundIdException::new);
        Project project = projectRepository.findById(projectId).orElseThrow(NotFoundIdException::new);
        List<TeamAndPowerTypeDTO> teamAndPowerTypeDTOList = new ArrayList<>();

        List<MemberTeam> memberTeamList = memberTeamRepository.findMemberTeamsByMemberIdAndProjectId(memberId, projectId);
        for(MemberTeam mt : memberTeamList){
            teamAndPowerTypeDTOList.add(mt.toTeamAndPowerTypeDTO(mt.getTeam().toSimpleTeamInfoDTO()));
        }

        return TeamsBysMemberAndProject.builder()
                .memberName(member.getName())
                .memberNickname(member.getNickname())
                .projectName(project.getVersion())
                .teamAndPowerTypeDTOList(teamAndPowerTypeDTOList)
                .build();

    }

    //팀에 속한 자식 팀까지 보고싶을 때
    @Transactional(readOnly = true)
    public TeamIncludeChildInfoDTO findTeamIncludeChildByTeamId(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
        List<SimpleTeamIncludeChildInfoDTO> childTeamDTOList = new ArrayList<>();

        if(!team.getChildTeamList().isEmpty()){
            for(Team t : team.getChildTeamList()){
                List<SimpleTeamIncludeChildInfoDTO> childList = new ArrayList<>();
                if(!t.getChildTeamList().isEmpty()){
                    for(Team ct : t.getChildTeamList()){
                        //2번째 부터는 자식이 없음.
                        childList.add(ct.toSimpleTeamIncludeChildInfoDTO(null));
                    }
                }
                childTeamDTOList.add(t.toSimpleTeamIncludeChildInfoDTO(childList));
            }
        }

        return team.toTeamIncludeChildInfoDTO(childTeamDTOList);
    }
    //팀과 자식팀(멤버 없음)+ 현재 팀의 멤버 출력
    @Transactional(readOnly = true)
    public TeamWithMemberAndChildTeamInfoDTO findTeamWithMemberAndChildTeamByTeamId(Long teamId){
        Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
        List<VerySimpleMemberInfoDTO> verySimpleMemberInfoDTOList = new ArrayList<>();
        List<SimpleTeamInfoDTO> simpleTeamInfoDTOList = new ArrayList<>();

        for(MemberTeam mt : team.getMemberTeamList()){
            verySimpleMemberInfoDTOList.add(mt.getMember().toVerySimpleMemberInfoDTO());
        }
        if(!team.getChildTeamList().isEmpty()){
            for(Team t : team.getChildTeamList()){
                simpleTeamInfoDTOList.add(t.toSimpleTeamInfoDTO());
            }
        }
        return team.toTeamWithMemberAndChildTeamInfoDTO(verySimpleMemberInfoDTOList, simpleTeamInfoDTOList);
    }

    //팀과 자식 팀 + 멤버까지 조회
    //TODO 성능 고려 해보기 -> 함수로 빼도 결국엔 다중 for문을 돌게 되어있음.
    @Transactional(readOnly = true)
    public TeamIncludeChildWithMemberInfoDTO findTeamIncludeChildWithTotalMemberByTeamId(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
        List<VerySimpleMemberInfoDTO> verySimpleMemberInfoDTOList = new ArrayList<>();

        //속한 멤버들 출력
        for (MemberTeam mt : team.getMemberTeamList()) {
            verySimpleMemberInfoDTOList.add(mt.getMember().toVerySimpleMemberInfoDTO());
        }
        List<SimpleTeamIncludeChildWithMemberInfoDTO> fChildList = new ArrayList<>();
        if (!team.getChildTeamList().isEmpty()) {
            List<VerySimpleMemberInfoDTO> fVerySimpleMemberDTOlist = new ArrayList<>();
            for (Team t : team.getChildTeamList()) {
                //member 추가하기
                for (MemberTeam mt : t.getMemberTeamList()) {
                    fVerySimpleMemberDTOlist.add(mt.getMember().toVerySimpleMemberInfoDTO());
                }
                //해당 팀의 두번째 뎁스 자식 그룹이 담김(존재한다면)
                List<SimpleTeamIncludeChildWithMemberInfoDTO> sChildList = new ArrayList<>();
                if (!t.getChildTeamList().isEmpty()) {
                    List<VerySimpleMemberInfoDTO> sVerySimpleMemberInfoDTO = new ArrayList<>();
                    for (Team ct : t.getChildTeamList()) {
                        for (MemberTeam mt : ct.getMemberTeamList()) {
                            sVerySimpleMemberInfoDTO.add(mt.getMember().toVerySimpleMemberInfoDTO());
                        }
                        //2번째 부터는 자식이 없음.
                        sChildList.add(ct.toSimpleTeamIncludeChildWithMemberInfoDTO(sVerySimpleMemberInfoDTO, null));
                    }
                }
                fChildList.add(t.toSimpleTeamIncludeChildWithMemberInfoDTO(fVerySimpleMemberDTOlist, sChildList));
            }
        }
        return team.toTeamIncludeChildWithMemberInfoDTO(verySimpleMemberInfoDTOList,fChildList);

    }

    //=========================update==============================================
    //새로운 멤버가 초대되었을 때
    @Transactional
    public AddMemberTeamResultDTO addMemberToTeam(Long memberId, Long teamId, AddMemberToTeamRequest addMemberToTeamRequest) throws Exception {
        List<Long> duplicatedMemberIdList = new ArrayList<>();

        //팀의 존재 여부 확인
        Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
        //현재 멤버가 프로젝트에 속한 사람인지 봐야함 아니라면, 초대 권한이 없음.
        if(!memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(memberId, teamId)){
            throw new AuthorityException("팀에 멤버를 초대할 수 있는 권한이 없습니다.");
        }

        Team rootTeam = teamRepository.findTeamByProjectIdAndDepth(team.getProject().getId(), 0).get(0);

        for(Long nMemberId : addMemberToTeamRequest.getAddMemberIdList()){
            MemberTeam memberTeam = memberTeamRepository.findMemberTeamByMemberIdAndTeamId(memberId,rootTeam.getId()).orElseThrow(NotFoundIdException::new);
            if(memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(nMemberId, teamId)){
                duplicatedMemberIdList.add(nMemberId);
                continue;
            }
            CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
                    .memberId(nMemberId)
                    .teamId(teamId)
                    .powerType(memberTeam.getPowerType())
                    .link(addMemberToTeamRequest.getLink())
                    .build();
            memberTeamService.createMemberTeam(createMemberTeamRequest);
        }

        List<MemberPowerDTO> memberPowerDTOList = findMembersByTeamId(teamId);

        return AddMemberTeamResultDTO.builder()
                .id(teamId)
                .MemberPowerDTO(memberPowerDTOList)
                .DuplicatedMemberList(duplicatedMemberIdList)
                .build();

    }


//    @Transactional
//    public String changeMemberToOtherTeam(UpdateMemberToOtherTeamRequest updateMemberToOtherTeamRequest){
//
//        return "Success";
//    }

//===================delete=======================================
    //멤버가 방출될 때.
    //이건 delete mapping 해야하기 때문에 productMember에서 해야함.


    //TODO 그룹 내에선 자발적으로 나가는것만 가능.태스크를 다 완료 하고 나가야함. memberTeam으로 바꿔야함.
    //그룹 내에서 멤버 나가기
    @Transactional
    public Long deleteMemberToTeam(Long memberId, Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
        List<Team> childTeamList = team.getChildTeamList();

        //자기 자신만 나갈 수 있기 때문에 팀에 속하지 않을 경우 예외
        if(!memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(memberId, teamId)){
            throw new NotFoundMemberInTeamException();
        }
        else {
            MemberTeam memberTeam = memberTeamRepository.findMemberTeamByMemberIdAndTeamId(memberId, teamId).get();


            int taskCount = taskRepository.findTasksByTargetMemberIdAndTeamId(memberId, teamId).size();
            int completeTaskCount = taskRepository.countTasksByTargetMemberIdAndTeamIdAndTaskStatus(memberId, teamId, TaskStatus.PROGRESS_COMPLETE);

            //task를 다 완료하지 않으면 나갈 수 없음.
            if (taskCount != completeTaskCount) {
                throw new CanNotDeleteMemberToTeam("task를 다 수행해야 나갈 수 있습니다.");
            }
            //자식팀 확인
            for (Team t : childTeamList) {
                if (memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(memberId, t.getId())) {
                    int childTaskCount = taskRepository.findTasksByTargetMemberIdAndTeamId(memberId, t.getId()).size();
                    int childCompleteTaskCount = taskRepository.countTasksByTargetMemberIdAndTeamIdAndTaskStatus(memberId, t.getId(), TaskStatus.PROGRESS_COMPLETE);

                    if (childTaskCount != childCompleteTaskCount) {
                        throw new CanNotDeleteMemberToTeam("하위 팀의 task를 다 수행해야 나갈 수 있습니다.");
                    }
                }

            }

            memberTeam.updateDelStatus(true);
            for (Team t : childTeamList) {
                if (memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(memberId, t.getId())) {
                    MemberTeam mt = memberTeamRepository.findMemberTeamByMemberIdAndTeamId(memberId, teamId).get();
                    mt.updateDelStatus(true);
                }
            }
        }

        return teamId;
    }

    //그룹 삭제
    //태스크가 없거나 하위 그룹이 없을 경우,
    @Transactional
    public Long deleteTeam(Long memberId, Long teamId){
        Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
        Member member = memberRepository.findMemberById(memberId).orElseThrow(NotFoundIdException::new);

        if(!team.getTaskList().isEmpty()){
            throw new CanNotDeleteTeamException("task가 존재하기 때문에 팀을 삭제할 수 없습니다.");
        }
        else if(!team.getChildTeamList().isEmpty()){
            throw new CanNotDeleteTeamException("하위 팀이 존재하기 때문에 팀을 삭제할 수 없습니다.");
        }
        else if(team.getTaskList().isEmpty() && team.getChildTeamList().isEmpty()){
            List<MemberTeam> memberTeamList = memberTeamRepository.findMemberTeamsByTeamId(teamId);

            for(MemberTeam mt : memberTeamList){
                memberTeamRepository.delete(mt);
            }
            teamRepository.delete(team);
        }



        return team.getId();
    }

}
