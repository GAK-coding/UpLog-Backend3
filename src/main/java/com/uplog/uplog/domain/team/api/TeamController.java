package com.uplog.uplog.domain.team.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    //team 생성시 기업만 만들 수 있으므로 pathvariable에 기업의 아이디가 들어감.
}
