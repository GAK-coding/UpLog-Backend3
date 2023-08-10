package com.uplog.uplog.domain.like.api;

import com.uplog.uplog.domain.like.application.LikeService;
import com.uplog.uplog.domain.like.dto.LikeDTO;
import com.uplog.uplog.domain.like.dto.LikeDTO.LikeInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
//@RequestMapping(value="/members/{member-id}")
public class PostLikeController {
    private final LikeService likeSerivce;

    @PostMapping(value = "/members/{member-id}/posts/{post-id}/likes")
    public ResponseEntity<LikeInfoDTO> createPostLike(@PathVariable(name = "member-id")Long memberId, @PathVariable(name = "post-id")Long postId){
        LikeInfoDTO likeInfoDTO = likeSerivce.createPostLike(memberId, postId);
        return ResponseEntity.ok(likeInfoDTO);
    }

    @GetMapping(value = "/posts/{post-id}/likes")
    public ResponseEntity<Integer> countPostLike(@PathVariable(name = "post-id")Long postId){
        int cnt = likeSerivce.countPostLike(postId);
        return ResponseEntity.ok(cnt);
    }

}
