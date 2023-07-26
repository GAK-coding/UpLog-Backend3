package com.uplog.uplog.domain.comment.api;

import com.uplog.uplog.domain.comment.application.CommentApplication;
import com.uplog.uplog.domain.comment.dto.CommentDTO;
import com.uplog.uplog.domain.member.api.TestController;
import com.uplog.uplog.domain.member.dto.MemberDTO;
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

import java.util.List;

import static com.uplog.uplog.domain.comment.dto.CommentDTO.*;


@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentController {

    private final CommentApplication commentApplication;

    // summary -> api 내용(기능) description -> 세부 설명 tag -> 그룹 (도메인 별 컨트롤러 이름)
    @Operation(summary = "Comment", description = "comment", tags = { "Comment Controller" })
    // response 코드 별로 응답 시 내용(설명) 작성
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = CommentController.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })

     /*
        CREATE
     */
    @PostMapping(value = "/comment/create/{post-id}/{member-id}")
    public ResponseEntity<CommentInfo> createComment(@RequestBody @Validated CommentInfo commentData,
                                                     @PathVariable("post-id")Long postId, @PathVariable("member-id")Long memberId){
        CommentInfo commentInfo = commentApplication.createComment(commentData,postId,memberId);
        return new ResponseEntity<>(commentInfo, HttpStatus.CREATED);
    }

     /*
        READ
     */
    @GetMapping(value="/comment/read/{post-id}")
    public ResponseEntity<List<ReadCommentInfo>> ReadComment(@PathVariable("post-id")Long postId){

        List<ReadCommentInfo> readList=commentApplication.ReadPostComment(postId);
        return new ResponseEntity<>(readList,HttpStatus.OK);
    }
}
