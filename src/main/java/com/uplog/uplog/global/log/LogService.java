package com.uplog.uplog.global.log;

import com.uplog.uplog.global.log.LogDTO.LogRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogService {

    @Transactional
    public void createLog(LogRequestDTO logRequestDTO){
        //성공일 경우, log.info로 분리
        if(logRequestDTO.isStatus()){
            log.info(logRequestDTO.getPage() + " " + logRequestDTO.getMessage());
        }
        else{
            log.warn(logRequestDTO.getPage() + " " + logRequestDTO.getMessage());
        }
    }
}
