package com.uplog.uplog.domain.team.api;

import com.uplog.uplog.domain.team.application.TeamService;
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
    //team 생성시 기업만 만들 수 있으므로 pathvariable에 기업의 아이디가 들어감.

    @PostMapping(value = "/teams/{team-id}")
    public ResponseEntity<String> deleteTeam(@PathVariable(name = "team-id")Long id){
        String s = teamService.deleteTeam(id);
        return new ResponseEntity<>(s, HttpStatus.OK);
    }
}
