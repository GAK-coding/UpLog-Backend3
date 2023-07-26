package com.uplog.uplog.domain.post.dto;

import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.post.model.PostType;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.tag.model.PostTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePostRequest{
        private Long menuId;
        private String postType;
//        private List<PostTag> postTagList;
        private String title;
        private String content;
        private Long porductId;
        private Long projectId;
        private LocalDateTime createTime;

        public Post toEntity(Member member, Menu menu, Product product, Project project){
            return Post.builder()
                    .author(member)
                    .menu(menu)
                    .postType(PostType.valueOf(postType))
                    .title(title)
                    .content(content)
                    .productName(product.getName())
                    .version(project.getVersion())
                    .createTime(createTime)
                    .build();
        }

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostInfoDTO{
        private Long id;
        private String title;
        private MemberDTO.PowerMemberInfoDTO authorInfoDTO;
        private Long menuId;
        private String menuName;
        private String productName;
        private String projectName;
        private String version;
        private PostType postType;
        private String content;
        private LocalDateTime createTime;

    }
}
