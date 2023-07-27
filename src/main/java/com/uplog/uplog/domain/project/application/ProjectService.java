package com.uplog.uplog.domain.project.application;

import com.uplog.uplog.domain.project.dto.ProjectDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import static com.uplog.uplog.domain.project.dto.ProjectDTO.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public CreateInitInfo createInit(CreateInitInfo createInitInfo, Long prodId){

        //변경이슈 먼저 하고 product랑 team완료되면 시작
        return createInitInfo;
    }
}
