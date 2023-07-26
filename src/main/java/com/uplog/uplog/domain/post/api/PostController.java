package com.uplog.uplog.domain.post.api;

import com.uplog.uplog.domain.post.application.PostService;
import com.uplog.uplog.domain.post.dto.PostDTO.*;
import com.uplog.uplog.domain.post.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    @PostMapping(value="/posts/{member_id}")
    public ResponseEntity<PostInfoDTO> createPost(@PathVariable(name = "member_id") Long id, @RequestBody CreatePostRequest createPostRequest) {
        Post createdPost = postService.createPost(id,createPostRequest);
        PostInfoDTO postInfoDTO = createdPost.toPostInfoDTO();
        return ResponseEntity.ok(postInfoDTO);
    }

    @DeleteMapping("posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }


}
