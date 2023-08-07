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

    private final CommentService commentService;

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
    @PostMapping(value = "/comments/{post-id}/{member-id}")
    public ResponseEntity<SimpleCommentInfo> createComment(@RequestBody @Validated CreateCommentRequest commentData,
                                                        @PathVariable("post-id")Long postId, @PathVariable("member-id")Long memberId){
        SimpleCommentInfo simpleData = commentService.createComment(commentData,postId,memberId);
        return new ResponseEntity<>(simpleData, HttpStatus.CREATED);
    }

     /*
        READ
     */
    @GetMapping(value="/comments/{post-id}/post")
    public ResponseEntity<List<SimpleCommentInfo>> findCommentsByPostId(@PathVariable("post-id")Long postId){

        List<SimpleCommentInfo> readList= commentService.findCommentByPostId(postId);
        return new ResponseEntity<>(readList,HttpStatus.OK);
    }

    @GetMapping(value="/comments/{comment-id}/comment")
    public ResponseEntity<List<SimpleCommentInfo>> findCommentById(@PathVariable("comment-id")Long commentId){

        List<SimpleCommentInfo> readSingleList= commentService.findCommentById(commentId);
        return new ResponseEntity<>(readSingleList,HttpStatus.OK);
    }

     /*
        UPDATE
     */

    @PatchMapping(value="/comments/{comment-id}/{member-id}/content")
    public ResponseEntity<SimpleCommentInfo> updateCommentContent(@RequestBody @Validated UpdateCommentContent updateCommentContent,
                                                                  @PathVariable("comment-id")Long commentId,
                                                                  @PathVariable("member-id")Long memberId){
        SimpleCommentInfo simpleCommentInfo = commentService.updateCommentContent(updateCommentContent,commentId,memberId);
        return new ResponseEntity<>(simpleCommentInfo,HttpStatus.OK);
    }

     /*
        DELETE
     */

    @DeleteMapping(value="/comments/{comment-id}/{member-id}")
    public ResponseEntity<String> deleteComment(@PathVariable("comment-id")Long commentId,
                                                @PathVariable("member-id")Long memberId){
        String message= commentService.deleteComment(commentId,memberId);
        return ResponseEntity.ok(message);
    }
}
