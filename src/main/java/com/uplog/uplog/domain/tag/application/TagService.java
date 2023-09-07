package com.uplog.uplog.domain.tag.application;

import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.tag.dao.PostTagRepository;
import com.uplog.uplog.domain.tag.dao.TagRepository;
import com.uplog.uplog.domain.tag.dto.TagDTO;
import com.uplog.uplog.domain.tag.dto.TagDTO.PostTagInfoDTO;
import com.uplog.uplog.domain.tag.model.PostTag;
import com.uplog.uplog.domain.tag.model.Tag;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.uplog.uplog.domain.tag.dto.TagDTO.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public Long createTag(Long postId, CreateTagRequest createTagRequest) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundIdException("해당 포스트는 존재하지 않습니다."));
        Tag tag = createTagRequest.toEntity();
        tagRepository.save(tag);

        PostTag postTag = PostTag.builder()
                .post(post)
                .tag(tag)
                .build();
        postTagRepository.save(postTag);
        tag.getPostTagList().add(postTag);
        post.getPostTagList().add(postTag);

        return tag.getId();
    }
}
