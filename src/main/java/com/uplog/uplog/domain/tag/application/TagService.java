package com.uplog.uplog.domain.tag.application;

import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.tag.dao.PostTagRepository;
import com.uplog.uplog.domain.tag.dao.TagRepository;
import com.uplog.uplog.domain.tag.dto.TagDTO;
import com.uplog.uplog.domain.tag.model.PostTag;
import com.uplog.uplog.domain.tag.model.Tag;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    public PostTag createPostTag(Long postId,Long tagId){
        Post post=postRepository.findById(postId).orElseThrow(() -> new NotFoundIdException("해당 포스트는 존재하지 않습니다."));
        Tag tag=tagRepository.findById(tagId).orElseThrow(() -> new NotFoundIdException("해당 태그은 존재하지 않습니다."));
        PostTag postTag= PostTag.builder()
                .post(post)
                .tag(tag)
                .build();
        postTagRepository.save(postTag);
        //post.addPostTag(postTag);
        return postTag;
    }
}
