package com.uplog.uplog.domain.changedIssue.api;

import com.sun.mail.iap.Response;
import com.uplog.uplog.domain.changedIssue.application.ChangedIssueService;
import com.uplog.uplog.domain.changedIssue.dto.ChangedIssueDTO;
import com.uplog.uplog.domain.comment.api.CommentController;
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

import static com.uplog.uplog.domain.changedIssue.dto.ChangedIssueDTO.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChangedIssueController {

    private final ChangedIssueService changedIssueService;

    // summary -> api 내용(기능) description -> 세부 설명 tag -> 그룹 (도메인 별 컨트롤러 이름)
    @Operation(summary = "ChangedIssue", description = "ChangedIssue", tags = { "ChangedIssue Controller" })
    // response 코드 별로 응답 시 내용(설명) 작성
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = CommentController.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })

    @GetMapping(value="/changedIssues/{member-id}/validate")
    public String checkMemberPower(@PathVariable("member-id")Long memberId){

        return changedIssueService.checkMemberPower(memberId);
    }

    @GetMapping(value="/changedIssues/{member-id}/{project-id}/validate")
    public String checkProjectProcess(@PathVariable("member-id")Long memberId,
                                   @PathVariable("prorject-id")Long projectId){

        return changedIssueService.checkProjectProgress(memberId,projectId);
    }



    @PostMapping(value="/changedIssues/{project-id}/{member-id}/{product-id}")
    public ResponseEntity<createInitChangedIssueInfo> createInitIssue(@RequestBody @Validated createInitChangedIssueInfo createInitChangedIssueInfo,
                                                                     @PathVariable("project-id")Long projId,
                                                                     @PathVariable("member-id")Long memberId,
                                                                      @PathVariable("product-id")Long productId) {

        createInitChangedIssueInfo createInitChangedIssueInfo1=changedIssueService.createInitIssue(createInitChangedIssueInfo,projId,memberId,productId);

        return new ResponseEntity<>(createInitChangedIssueInfo1, HttpStatus.CREATED);
    }

    @GetMapping(value="/changedIssues/{issue-id}")
    public ResponseEntity<issueInfo> readIssueInfo(@PathVariable("issue-id")Long issueId){

        issueInfo issueInfo=changedIssueService.readIssueInfo(issueId);

        return new ResponseEntity<>(issueInfo,HttpStatus.OK);
    }

    @PatchMapping(value="/changedIssues/{issue-id}/{member-id}/updateissue")
    public ResponseEntity<updateChangedIssue> updateChangedIssue(@RequestBody @Validated updateChangedIssue updateChangedIssue,
                                                                 @PathVariable("issue-id")Long issueId){

        updateChangedIssue updateChangedIssue1=changedIssueService.updateChangedIssue(updateChangedIssue,issueId);

        return new ResponseEntity<>(updateChangedIssue1,HttpStatus.OK);

    }


}
