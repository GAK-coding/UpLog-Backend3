package com.uplog.uplog.domain.tag.dto;

import com.uplog.uplog.domain.tag.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TagDTO {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CreateTagRequest{
        private String content;

        public Tag toEntity(){
            return Tag.builder()
                    .content(this.content)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagInfoDTO{
        private Long id;
        private String content;
    }
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
