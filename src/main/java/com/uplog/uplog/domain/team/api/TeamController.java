package com.uplog.uplog.domain.team.api;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.team.application.TeamService;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.CreateTeamRequest;
import com.uplog.uplog.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    private final TeamService teamService;

    private final MemberRepository memberRepository;

    @PostMapping(value = "/teams")
    public ResponseEntity<Long> createTeam(CreateTeamRequest createTeamRequest) throws Exception {
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Long teamId = teamService.createTeam(memberId, createTeamRequest);
        return new ResponseEntity<>(teamId, HttpStatus.CREATED);
    }

    @PostMapping(value = "/teams/{team-id}")
    public ResponseEntity<String> deleteTeam(@PathVariable(name = "team-id")Long id){
        String s = teamService.deleteTeam(id);
        return new ResponseEntity<>(s, HttpStatus.OK);
    }
}
