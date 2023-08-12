package com.uplog.uplog.domain.like.api;

import com.uplog.uplog.domain.comment.dto.CommentDTO;
import com.uplog.uplog.domain.comment.dto.CommentDTO.VerySimpleCommentInfoDTO;
import com.uplog.uplog.domain.like.application.LikeService;
import com.uplog.uplog.domain.like.dto.LikeDTO;
import com.uplog.uplog.domain.like.dto.LikeDTO.LikeInfoDTO;
import com.uplog.uplog.domain.member.dao.MemberRepository;
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
//@RequestMapping(value = "/members/{member-id}")
public class CommentLikeController {
    private final LikeService likeService;

    private final MemberRepository memberRepository;

    @PostMapping(value = "/comments/{comment-id}/likes")
    public ResponseEntity<LikeInfoDTO> createCommentLike(@PathVariable(name = "comment-id")Long commentId){
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        LikeInfoDTO likeInfoDTO = likeService.createCommentLike(memberId, commentId);
        return ResponseEntity.ok(likeInfoDTO);
    }

    @GetMapping(value = "/comments/{comment-id}/likes")
    public ResponseEntity<Integer> countCommentLike(@PathVariable(name = "comment-id")Long commentId){
        int cnt = likeService.countCommentLike(commentId);
        return ResponseEntity.ok(cnt);
    }

    @GetMapping(value = "/comments/likes")
    public ResponseEntity<List<VerySimpleCommentInfoDTO>> findCommentLikesByMemberId(){
        Long memberId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        List<VerySimpleCommentInfoDTO> verySimpleCommentInfoDTOList = likeService.findCommentLikeByMemberId(memberId);
        return ResponseEntity.ok(verySimpleCommentInfoDTOList);
    }

}
