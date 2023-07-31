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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public CreateInitInfo CreateInitProject(@RequestBody CreateInitInfo createInitInfo, @PathVariable("product-id")Long prodId) {

        //변경이슈 먼저 하고 product랑 team완료되면 시작
        projectService.createInit(createInitInfo,prodId);


        return createInitInfo;
    }
}