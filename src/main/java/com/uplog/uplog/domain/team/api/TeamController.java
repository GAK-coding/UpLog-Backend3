package com.uplog.uplog.domain.team.api;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.team.application.TeamService;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.*;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerDTO;
import com.uplog.uplog.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    private final TeamService teamService;

    private final MemberRepository memberRepository;

    //================create=================
    @PostMapping(value = "/projects/{project-id}/teams")
    public ResponseEntity<CreateTeamResultDTO> createTeam(@PathVariable(name = "project-id")Long projectId, @RequestBody @Validated CreateTeamRequest createTeamRequest) throws Exception {
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        CreateTeamResultDTO createTeamResultDTO = teamService.createTeam(memberId, projectId, createTeamRequest);
        return new ResponseEntity<>(createTeamResultDTO, HttpStatus.CREATED);
    }
    //=============read==========================
    //팀아이디로 팀에 속한 멤버들 조회
    @GetMapping(value = "/teams/{team-id}/members")
    public ResponseEntity<List<MemberPowerDTO>> findMemberByTeamId(@PathVariable(name = "team-id")Long teamId){
        List<MemberPowerDTO> memberPowerDTOList = teamService.findMembersByTeamId(teamId);
        return ResponseEntity.ok(memberPowerDTOList);
    }

    //프로젝트아이디와 멤버 아이디로 멤버가 속한 팀 찾기
    @GetMapping(value = "/projects/{project-id}/member/teams")
    public ResponseEntity<TeamsBysMemberAndProject> findTeamsByMemberIdAndProjectId(@PathVariable(name = "project-id")Long projectId){
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        TeamsBysMemberAndProject teamsBysMemberAndProject = teamService.findTeamsByMemberIdAndProjectId(memberId, projectId);
        return ResponseEntity.ok(teamsBysMemberAndProject);
    }

    //팀아이디로 자식팀까지 조회
    @GetMapping(value = "/teams/{team-id}/child-team")
    public ResponseEntity<TeamIncludeChildInfoDTO> findTeamIncludeChild(@PathVariable(name = "team-id")Long teamId){
        TeamIncludeChildInfoDTO teamIncludeChildInfoDTO = teamService.findTeamIncludeChildByTeamId(teamId);
        return ResponseEntity.ok(teamIncludeChildInfoDTO);
    }

    //팀아이디로 멤버 + 자식팀(멤버 없음) 조회
    @GetMapping(value = "/teams/{team-id}/members/child-team")
    public ResponseEntity<TeamWithMemberAndChildTeamInfoDTO> findTeamWithMemberAndChildTeam(@PathVariable(name = "team-id")Long teamId) {
        TeamWithMemberAndChildTeamInfoDTO teamWithMemberAndChildTeamInfoDTO = teamService.findTeamWithMemberAndChildTeamByTeamId(teamId);
        return ResponseEntity.ok(teamWithMemberAndChildTeamInfoDTO);
    }

    //팀아이디로 하위팀 + 출력된 팀의 멤버들까지 조회
    @GetMapping(value = "/teams/{team-id}/child-team/members")
    public ResponseEntity<TeamIncludeChildWithMemberInfoDTO> findTeamIncludeChildWithTotalMember(@PathVariable(name = "team-id")Long teamId){
        TeamIncludeChildWithMemberInfoDTO teamIncludeChildWithMemberInfoDTO = teamService.findTeamIncludeChildWithTotalMemberByTeamId(teamId);
        return ResponseEntity.ok(teamIncludeChildWithMemberInfoDTO);
    }
    //================update========================
    @PatchMapping(value = "/teams/{team-id}")
    public ResponseEntity<AddMemberTeamResultDTO> addMemberToTeam(@PathVariable(name = "team-id")Long teamId,@RequestBody @Validated AddMemberToTeamRequest addMemberToTeamRequest) throws Exception {
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        AddMemberTeamResultDTO addMemberTeamResultDTO = teamService.addMemberToTeam(memberId, teamId, addMemberToTeamRequest);
        return ResponseEntity.ok(addMemberTeamResultDTO);
    }
    //===========delete==============================
    //팀 내에서 멤버 나가기
    @DeleteMapping(value = "/teams/{team-id}/member")
    public ResponseEntity<Long> deleteMemberToTeam(@PathVariable(name = "team-id")Long teamId){
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Long id = teamService.deleteMemberToTeam(memberId, teamId);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    //팀 삭제하기
    @DeleteMapping(value = "/teams/{team-id}/team")
    public ResponseEntity<Long> deleteTeam(@PathVariable(name = "team-id")Long teamId){
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Long id = teamService.deleteTeam(memberId, teamId);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
