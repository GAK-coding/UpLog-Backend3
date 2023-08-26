package com.uplog.uplog.global.log;

import com.uplog.uplog.global.log.LogDTO.LogRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @PostMapping(value = "/logs")
    public void createLog(@RequestBody LogRequestDTO logRequestDTO){
        logService.createLog(logRequestDTO);
    }
}
