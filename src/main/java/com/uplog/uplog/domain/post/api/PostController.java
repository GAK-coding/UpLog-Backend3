package com.uplog.uplog.domain.post.api;

import com.uplog.uplog.domain.post.application.PostService;
import com.uplog.uplog.domain.post.dto.PostDTO.*;
import com.uplog.uplog.domain.post.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;

    /*
    create
     */
    @PostMapping(value="/posts/{member_id}")
    public ResponseEntity<PostInfoDTO> createPost(@PathVariable(name = "member_id") Long id, @RequestBody CreatePostRequest createPostRequest) {
        PostInfoDTO postInfoDTO = postService.createPost(id,createPostRequest);
        return ResponseEntity.ok(postInfoDTO);
    }


    /*
    delete
     */
    @DeleteMapping("posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    /*
    update
     */
    @PatchMapping("/posts/{post_id}/title")
    public ResponseEntity<PostInfoDTO> updatePostTitle(@PathVariable Long id,@RequestBody UpdatePostTitleRequest updatePostTitleRequest,Long currentUserId){
        PostInfoDTO postInfoDTO=postService.updatePostTitle(id,updatePostTitleRequest,currentUserId);
        return ResponseEntity.ok(postInfoDTO);
    }

    @PatchMapping("/posts/{post_id}/content")
    public ResponseEntity<PostInfoDTO> updatePostContent(@PathVariable Long id,@RequestBody UpdatePostContentRequest updatePostContentRequest,Long currentUserId){
        PostInfoDTO postInfoDTO=postService.updatePostContent(id,updatePostContentRequest,currentUserId);
        return ResponseEntity.ok(postInfoDTO);
    }

    @PatchMapping("/posts/{post_id}/type")
    public ResponseEntity<PostInfoDTO> updatePostType(@PathVariable Long id,@RequestBody UpdatePostTypeRequest updatePostTypeRequest,Long currentUserId){
        PostInfoDTO postInfoDTO=postService.updatePostType(id,updatePostTypeRequest,currentUserId);
        return ResponseEntity.ok(postInfoDTO);
    }
    @PatchMapping("/posts/{post_id}/menu")
    public ResponseEntity<PostInfoDTO> updatePostMenu(@PathVariable Long id,@RequestBody UpdatePostMenuRequest updatePostMenuRequest,Long currentUserId){
        PostInfoDTO postInfoDTO=postService.updatePostMenu(id,updatePostMenuRequest,currentUserId);
        return ResponseEntity.ok(postInfoDTO);
    }

    /*
    GET
     */
    @GetMapping("/posts/menus/{menu-id}")
    public ResponseEntity<List<PostInfoDTO>> getPostByMenu(@PathVariable("menu-id") Long menuId){
        List<PostInfoDTO> postInfoDTOs=postService.findPostInfoByMenuId(menuId);
        return new ResponseEntity<>(postInfoDTOs, HttpStatus.OK);
    }







}
