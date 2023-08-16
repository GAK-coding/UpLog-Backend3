package com.uplog.uplog.domain.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TagDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostTagInfoDTO{
        private Long id;
        private Long postId;
        private Long tagId;
        private String content;
    }
}
