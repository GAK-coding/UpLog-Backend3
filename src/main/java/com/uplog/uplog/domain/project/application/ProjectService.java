package com.uplog.uplog.domain.project.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.project.exception.ExistProcessProjectExeption;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.project.model.QProject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.List;

import static com.uplog.uplog.domain.project.dto.ProjectDTO.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ProjectRepository projectRepository;


    @Transactional
    public CreateInitInfo createInit(CreateInitInfo createInitInfo, Long prodId){

        //새로 생성 되는 프로젝트는 진행 중 고정 값.
        Project project=createInitInfo.toEntity(ProjectStatus.PROGRESS_IN);

        projectRepository.save(project);

        CreateInitInfo createInitInfo1=project.toCreateInitChangedIssueInfo();

        return createInitInfo1;
    }

    //진행 중 project가 있으면 접근 제한
    public void checkProcessProject(Long productId){

        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        QProject project=QProject.project;
        List<Project> projectList=query
                .selectFrom(project)
                .where(project.product.id.eq(productId))
                .fetch();


        for(Project project1: projectList){


            if(project1.getProjectStatus()== ProjectStatus.PROGRESS_IN){

                throw new ExistProcessProjectExeption(project1.getId());
            }
        }

    }

}