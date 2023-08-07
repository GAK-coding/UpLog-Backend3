package com.uplog.uplog.domain.like.api;

import com.uplog.uplog.domain.like.application.LikeService;
import com.uplog.uplog.domain.like.dto.LikeDTO;
import com.uplog.uplog.domain.like.dto.LikeDTO.LikeInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/members/{member-id}")
public class CommentLikeController {
    private final LikeService likeService;

    @PostMapping(value = "/comments/{comment-id}")
    public ResponseEntity<LikeInfoDTO> createCommentLike(@PathVariable(name = "member-id")Long memberId, @PathVariable(name = "comment-id")Long commentId){
        LikeInfoDTO likeInfoDTO = likeService.createCommentLike(memberId, commentId);
        return ResponseEntity.ok(likeInfoDTO);
    }


}
