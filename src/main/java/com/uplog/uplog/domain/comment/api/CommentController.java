package com.uplog.uplog.domain.comment.api;

import com.uplog.uplog.domain.comment.application.CommentService;
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

    private final CommentService commentApplication;

    // summary -> api 내용(기능) description -> 세부 설명 tag -> 그룹 (도메인 별 컨트롤러 이름)
    @Operation(summary = "Comment", description = "comments", tags = { "Comment Controller" })
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
    @PostMapping(value = "/posts/{post-id}/comments/{member-id}")
    public ResponseEntity<CommentInfo> createComment(@RequestBody @Validated CommentInfo commentData,
                                                     @PathVariable("post-id")Long postId, @PathVariable("member-id")Long memberId){
        CommentInfo commentInfo = commentApplication.createComment(commentData,postId,memberId);
        return new ResponseEntity<>(commentInfo, HttpStatus.CREATED);
    }

     /*
        READ
     */
    @GetMapping(value="/comments/{post-id}/comments")
    public ResponseEntity<List<ReadCommentInfo>> readComment(@PathVariable("post-id")Long postId){

        List<ReadCommentInfo> readList=commentApplication.readPostComment(postId);
        return new ResponseEntity<>(readList,HttpStatus.OK);
    }

    @GetMapping(value="/comments/{comment-id}/single-comment")
    public ResponseEntity<List<ReadCommentInfo>> readSingleComment(@PathVariable("comment-id")Long commentId){

        List<ReadCommentInfo> readSingleList=commentApplication.readPostSingleComment(commentId);
        return new ResponseEntity<>(readSingleList,HttpStatus.OK);
    }

     /*
        UPDATE
     */

    @PatchMapping(value="/comments/{comment-id}/{member-id}/content")
    public ResponseEntity<ReadCommentInfo> updateComment(@RequestBody @Validated UpdateCommentContent updateCommentContent,
                                                         @PathVariable("comment-id")Long commentId,
                                                         @PathVariable("member-id")Long memberId){
        ReadCommentInfo readCommentInfo=commentApplication.updateCommentContent(updateCommentContent,commentId,memberId);
        return new ResponseEntity<>(readCommentInfo,HttpStatus.OK);
    }

     /*
        DELETE
     */

    @DeleteMapping(value="/comments/{comment-id}/{member-id}")
    public ResponseEntity<String> deleteComment(@PathVariable("comment-id")Long commentId,
                                                @PathVariable("member-id")Long memberId){
        String message= commentApplication.deleteComment(commentId,memberId);
        return ResponseEntity.ok(message);
    }
}
