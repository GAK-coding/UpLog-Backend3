package com.uplog.uplog.domain.post.api;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.post.application.PostService;
import com.uplog.uplog.domain.post.dto.PostDTO.*;
import com.uplog.uplog.global.util.SecurityUtil;
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
    private final MemberRepository memberRepository;

    /*
    create
     */
    @PostMapping(value="/posts")
    public ResponseEntity<Long> createPost(@RequestBody CreatePostRequest createPostRequest) {
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Long postInfoDTO = postService.createPost(memberId,createPostRequest);
        return ResponseEntity.ok(postInfoDTO);
    }


    /*
    delete
     */
    @DeleteMapping("posts/{post-id}")
    public String deletePost(@PathVariable(name="post-id") Long id){
        Long currentUserId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        return postService.deletePost(id,currentUserId);
    }

    /*
    update
     */
    @PatchMapping("/posts/{post-id}/title")
    public ResponseEntity<Long> updatePostTitle(@PathVariable(name="post-id") Long id, @RequestBody UpdatePostTitleRequest updatePostTitleRequest){
        Long currentUserId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Long postInfoDTO=postService.updatePostTitle(id,updatePostTitleRequest,currentUserId);
        return ResponseEntity.ok(postInfoDTO);
    }

    @PatchMapping("/posts/{post-id}/content")
    public ResponseEntity<Long> updatePostContent(@PathVariable(name="post-id") Long id, @RequestBody UpdatePostContentRequest updatePostContentRequest){
        Long currentUserId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Long postInfoDTO=postService.updatePostContent(id,updatePostContentRequest,currentUserId);
        return ResponseEntity.ok(postInfoDTO);
    }

    @PatchMapping("/posts/{post-id}/type")
    public ResponseEntity<Long> updatePostType(@PathVariable(name="post-id") Long id, @RequestBody UpdatePostTypeRequest updatePostTypeRequest){
        Long currentUserId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Long postInfoDTO=postService.updatePostType(id,updatePostTypeRequest,currentUserId);
        return ResponseEntity.ok(postInfoDTO);
    }
    @PatchMapping("/posts/{post-id}/menu")
    public ResponseEntity<Long> updatePostMenu(@PathVariable(name="post-id") Long id, @RequestBody UpdatePostMenuRequest updatePostMenuRequest){
        Long currentUserId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Long postInfoDTO=postService.updatePostMenu(id,updatePostMenuRequest,currentUserId);
        return ResponseEntity.ok(postInfoDTO);
    }

    @PatchMapping("/posts/{post-id}")
    public ResponseEntity<Long> updatePost(
            @PathVariable("post-id") Long id,
            @RequestBody UpdatePostRequest updatePostRequest) {
        Long currentUserId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Long updatedPost = postService.updatePostInfo(id, updatePostRequest, currentUserId);
        return ResponseEntity.ok(updatedPost);
    }

    /*
    GET
     */
    @GetMapping("/posts/menus/{menu-id}")
    public ResponseEntity<List<PostDetailInfoDTO>> getPostByMenu(@PathVariable(name="menu-id") Long menuId){
        List<PostDetailInfoDTO> postInfoDTOs=postService.findPostInfoByMenuId(menuId);
        return new ResponseEntity<>(postInfoDTOs, HttpStatus.OK);
    }

    @GetMapping("/posts/{post-id}")
    public ResponseEntity<PostDetailInfoDTO> getPostById(@PathVariable(name="post-id") Long postId){
        PostDetailInfoDTO postInfoDTO=postService.findById(postId);
        return ResponseEntity.ok(postInfoDTO);
    }







}
