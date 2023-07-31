package com.uplog.uplog.domain.project.api;

import com.uplog.uplog.domain.comment.api.CommentController;
import com.uplog.uplog.domain.project.application.ProjectService;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.uplog.uplog.domain.project.dto.ProjectDTO.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProjectController {

    private final ProjectService projectService;

    // summary -> api 내용(기능) description -> 세부 설명 tag -> 그룹 (도메인 별 컨트롤러 이름)
    @Operation(summary = "project", description = "project", tags = { "project Controller" })
    // response 코드 별로 응답 시 내용(설명) 작성
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = CommentController.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })

    @PostMapping(value="/projects/{product-id}")
    public ResponseEntity<CreateInitInfo> CreateInitProject(@RequestBody CreateInitInfo createInitInfo, @PathVariable("product-id")Long productId) {

        //진행 중인 프로젝트가 있을 시 접근 제한
        projectService.checkProcessProject(productId);

        projectService.createInit(createInitInfo,productId);


        return new ResponseEntity<>(createInitInfo, HttpStatus.CREATED);
    }

    @PatchMapping(value="/projects/{project-id}/{member-id}")
    public ResponseEntity<UpdateProjectInfo> updateProjectInfo(@RequestBody UpdateProjectStatus updateProjectStatus,
                                               @PathVariable("project-id")Long projectId,
                                               @PathVariable("member-id")Long memberId){

        //권한 확인
        projectService.powerValidate(memberId);
        UpdateProjectInfo updateProjectInfo=projectService.updateProject(updateProjectStatus,projectId);

        return new ResponseEntity<>(updateProjectInfo,HttpStatus.OK);

    }
}