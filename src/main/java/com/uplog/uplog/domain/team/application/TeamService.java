package com.uplog.uplog.domain.team.application;

import com.uplog.uplog.domain.product.application.ProductMemberService;
import com.uplog.uplog.domain.product.dao.ProductMemberRepository;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.ProductMember;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.TeamDTO.CreateTeamRequest;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final ProductRepository productRepository;
    private final ProjectRepository projectRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final ProductMemberRepository productMemberRepository;


    private final ProductMemberService productMemberService;
    private final MemberTeamService memberTeamService;
    private final MailService mailService;

    @Transactional
    public Long createTeam(Long currentMemberId, CreateTeamRequest createTeamRequest) throws Exception {

        Project project = projectRepository.findById(createTeamRequest.getProjectId()).orElseThrow(NotFoundIdException::new);
        //멤버가 현재 프로젝트에 속한사람인지 확인
        Team rootTeam = teamRepository.findByProjectIdAndName(project.getId(), project.getVersion()).orElseThrow(NotFoundIdException::new);
        if(!memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(currentMemberId, rootTeam.getId())){
            throw new AuthorityException("팀에 속하지 않은 멤버로, 팀 생성 권한이 없습니다.");
        }

//            Team parentTeam = teamRepository.findTeamById(createTeamRequest.getParentTeamId());
            //생성 외에 부모가 널로 들어왔다면, 프로젝트 생성시에 만들어진 젠체 그룹으로 부모넣어주기
            if(createTeamRequest.getParentTeamId() != null) {
                Team parentTeam = teamRepository.findById(createTeamRequest.getParentTeamId()).orElseThrow(NotFoundIdException::new);
                Team team = createTeamRequest.toEntity(project, parentTeam);
                teamRepository.save(team);

                //PowerType알아내기 --> 어떻게 알아내지  -> memberProduct에서 찾자. team을 이름으로 찾고 team이랑 멤버 아이디로 해도 되는데 그럼 중복된 이름이 걸릴 수 있음.
                for (Long memberId : createTeamRequest.getMemberIdList()) {
                    ProductMember memberProduct = productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, createTeamRequest.getProjectId()).orElseThrow(NotFoundIdException::new);
                    CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
                            .teamId(team.getId())
                            .memberId(memberId)
                            .powerType(memberProduct.getPowerType()==PowerType.MASTER || memberProduct.getPowerType()==PowerType.LEADER ? PowerType.LEADER : memberProduct.getPowerType())
                            .link(createTeamRequest.getLink())
                            .build();
                    memberTeamService.createMemberTeam(createMemberTeamRequest);
                }
                return team.getId();
            }
            else{
                Team parentTeam = teamRepository.findByProjectIdAndName(createTeamRequest.getProjectId(), project.getVersion()).orElseThrow(NotFoundIdException::new);
                Team team = createTeamRequest.toEntity(project, parentTeam);
                teamRepository.save(team);

                for(Long memberId : createTeamRequest.getMemberIdList()) {
                    ProductMember memberProduct = productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, createTeamRequest.getProjectId()).orElseThrow(NotFoundIdException::new);
                    CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
                            .teamId(team.getId())
                            .memberId(memberId)
                            .powerType(memberProduct.getPowerType()==PowerType.MASTER || memberProduct.getPowerType()==PowerType.LEADER ? PowerType.LEADER : memberProduct.getPowerType())
                            .link(createTeamRequest.getLink())
                            .build();
                    memberTeamService.createMemberTeam(createMemberTeamRequest);
            }
                return team.getId();

        }

    }


    //TODO DELETE는 마스터만 가능하게? 기업도 가능하게 해야해? 우선 둘다 가능하게 함. -> 이건 나중에 고려
    //근데 이게 프로덕트가 삭제되면 자동으로 삭제되는건가? -> 삭제 없음
    @Transactional
    public String deleteTeam(Long id){
        Team team = teamRepository.findById(id).orElseThrow(NotFoundIdException::new);
        teamRepository.delete(team);
        return "DELETE";
    }

}
