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

    //create
    @PostMapping(value="/posts/{member_id}")
    public ResponseEntity<PostInfoDTO> createPost(@PathVariable(name = "member_id") Long id, @RequestBody CreatePostRequest createPostRequest) {
        Post createdPost = postService.createPost(id,createPostRequest);
        PostInfoDTO postInfoDTO = createdPost.toPostInfoDTO();
        return ResponseEntity.ok(postInfoDTO);
    }


    //delete
    @DeleteMapping("posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    //update
    @PatchMapping("/posts/{post_id}/title")
    public ResponseEntity<PostInfoDTO> updatePostTitle(@PathVariable Long id,@RequestBody UpdatePostTitleRequest updatePostTitleRequest){
        Post updatedPost=postService.updatePostTitle(id,updatePostTitleRequest);
        PostInfoDTO postInfoDTO=updatedPost.toPostInfoDTO();
        return ResponseEntity.ok(postInfoDTO);
    }

    @PatchMapping("/posts/{post_id}/content")
    public ResponseEntity<PostInfoDTO> updatePostContent(@PathVariable Long id,@RequestBody UpdatePostContentRequest updatePostContentRequest){
        Post updatedPost=postService.updatePostContent(id,updatePostContentRequest);
        PostInfoDTO postInfoDTO=updatedPost.toPostInfoDTO();
        return ResponseEntity.ok(postInfoDTO);
    }

    @PatchMapping("/posts/{post_id}/type")
    public ResponseEntity<PostInfoDTO> updatePostType(@PathVariable Long id,@RequestBody UpdatePostTypeRequest updatePostTypeRequest){
        Post updatedPost=postService.updatePostType(id,updatePostTypeRequest);
        PostInfoDTO postInfoDTO=updatedPost.toPostInfoDTO();
        return ResponseEntity.ok(postInfoDTO);
    }
    @PatchMapping("/posts/{post_id}/menu")
    public ResponseEntity<PostInfoDTO> updatePostMenu(@PathVariable Long id,@RequestBody UpdatePostMenuRequest updatePostMenuRequest){
        Post updatedPost=postService.updatePostMenu(id,updatePostMenuRequest);
        PostInfoDTO postInfoDTO=updatedPost.toPostInfoDTO();
        return ResponseEntity.ok(postInfoDTO);
    }







}
