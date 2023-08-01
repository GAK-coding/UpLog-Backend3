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
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class CreatePostRequest{
//        private Long menuId;
//        private String postType;
////        private List<PostTag> postTagList;
//        private String title;
//        private String content;
//        private Long porductId;
//        private Long projectId;
//
//
//// TODO POSTTYPE 관련해서 좀 더 수정해야함
//
//        public Post toEntity(Member member, Menu menu, Product product, Project project){
//            Post.PostBuilder postBuilder = Post.builder()
//                    .author(member)
//                    .menu(menu)
//                    .title(this.title)
//                    .content(content)
//                    .productName(product.getName())
//                    .version(project.getVersion());
//
//            if (postType != null) {
//                postBuilder.postType(PostType.valueOf(postType));
//            }
//
//            return postBuilder.build();
//        }
//
//    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePostRequest{
        //private Long menuId;
        private String postType;
        //        private List<PostTag> postTagList;
        private String title;
        private String content;
        //private Long porductId;
        //private Long projectId;


// TODO POSTTYPE 관련해서 좀 더 수정해야함

        public Post toEntity(Member member, PostType postType){
            return Post.builder()
                    .author(member)
                    //.menu(menu)
                    .title(this.title)
                    .content(content)
                    .postType(postType)
                    //.productName(product.getName())
                    //.version(project.getVersion());
                    .build();
        }

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostInfoDTO1{
        private Long id;
        private String title;
        private MemberDTO.PowerMemberInfoDTO authorInfoDTO;
        //private Long menuId;
        //private String menuName;
        //private String productName;
        //private String projectName;
        //private String version;
        private PostType postType;
        private String content;
        private LocalDateTime createTime;

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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePostTitleRequest{
        private Long id;
        private String updateTitle;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePostContentRequest{
        private Long id;
        private String updateContent;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePostTypeRequest{
        private Long id;
        private String updatePostType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePostMenuRequest{
        private Long id;
        private Long updateMenuId;
    }

    //updateTag

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePostProductRequest{
        private Long id;
        private String updateProductName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePostVersionRequest{
        private Long id;
        private String updateVersion;
    }
}
