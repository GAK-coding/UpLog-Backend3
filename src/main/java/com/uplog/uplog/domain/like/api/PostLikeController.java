package com.uplog.uplog.domain.like.api;

import com.uplog.uplog.domain.comment.dto.CommentDTO;
import com.uplog.uplog.domain.comment.dto.CommentDTO.VerySimpleCommentInfoDTO;
import com.uplog.uplog.domain.like.application.LikeService;
import com.uplog.uplog.domain.like.dto.LikeDTO;
import com.uplog.uplog.domain.like.dto.LikeDTO.LikeInfoDTO;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.post.dto.PostDTO;
import com.uplog.uplog.domain.post.dto.PostDTO.SimplePostInfoDTO;
import com.uplog.uplog.global.util.SecurityUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
//@RequestMapping(value="/members/{member-id}")
public class PostLikeController {
    private final LikeService likeSerivce;

    private final MemberRepository memberRepository;

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

    @GetMapping(value = "/posts/likes")
    public ResponseEntity<List<SimplePostInfoDTO>> findPostLikesByMembeId(){
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        List<SimplePostInfoDTO> simplePostInfoDTOList = likeSerivce.findPostLikeByMemberId(memberId);
        return ResponseEntity.ok(simplePostInfoDTOList);
    }

}
