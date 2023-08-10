package com.uplog.uplog.domain.project.api;

import com.uplog.uplog.domain.comment.api.CommentController;
import com.uplog.uplog.domain.project.application.ProjectService;
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

    //todo 처음에 default로 일단 해당 제품의 멤버(그룹으로 치면 전체)를 TeamList로 넣어줘야 하나?
    @PostMapping(value="/projects/{product-id}")
    public ResponseEntity<CreateInitInfo> CreateInitProject(@RequestBody CreateInitInfo createInitInfo, @PathVariable("product-id")Long productId) {

        //진행 중인 프로젝트가 있을 시 접근 제한
        projectService.checkProcessProject(productId);

        projectService.createInit(createInitInfo,productId);


        return new ResponseEntity<>(createInitInfo, HttpStatus.CREATED);
    }

    //Todo read 어떤 걸 보여줄 지 더 고민 -> projectTeamList, productId, menuList, version
    //Todo 새로고침 시 전체를 넘겨주는 상황.

    //전체 조회
    @GetMapping(value="/projects/{project-id}/{member-id}")
    public ResponseEntity<requestProjectAllInfo> readProjectAllInfo(@PathVariable("project-id")Long projectId,
                                                                    @PathVariable("member-id")Long memberId){

        requestProjectAllInfo requestProjectAllInfo =projectService.readProject(projectId,memberId);
        return new ResponseEntity<>(requestProjectAllInfo,HttpStatus.OK);
    }

    //버전 클릭 시 프론트에게 클라이언트인지 아닌 지를 보내줌

    @GetMapping(value="/projects/{project-id}/{member-id}/power")
    public ResponseEntity<requestProjectInfo> readProjectInfo(@PathVariable("project-id")Long projectId,
                                                              @PathVariable("member-id")Long memberId){

        requestProjectInfo requestProjectInfo=projectService.readProjectSimple(projectId,memberId);

        return new ResponseEntity<>(requestProjectInfo,HttpStatus.OK);
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

    @DeleteMapping(value="/projects/{project-id}/{member-id}")
    public String deleteProject(@PathVariable("project-id")Long projectId,
                                @PathVariable("member-id")Long memberId){

        //권한 확인
        projectService.powerValidate(memberId);

        return projectService.deleteProject(projectId);
    }
}