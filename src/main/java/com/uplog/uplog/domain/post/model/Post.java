package com.uplog.uplog.domain.post.model;

import com.uplog.uplog.domain.comment.model.Comment;
import com.uplog.uplog.domain.like.dao.PostLikeRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.tag.dto.TagDTO;
import com.uplog.uplog.domain.tag.dto.TagDTO.TagInfoDTO;
import com.uplog.uplog.domain.tag.model.PostTag;
import com.uplog.uplog.domain.tag.model.Tag;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.uplog.uplog.domain.post.dto.PostDTO.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    //TODO 고도화할때 만약 해당 포스트를 지워서 포스트태그가 없어졌을때 해당 태그가 하나도 없다면 태그도 지우게 해야함(현재는 포스트태그만 삭제돼)
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTagList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();


    private String title;
    //content에 image랑 codeblock이 한번에 포함된것
    private String content;
    private String productName;
    private String version;

    @CreationTimestamp
    private LocalDateTime createTime;


//    public PostInfoDTO toPostInfoDTO(){
//        return PostInfoDTO.builder()
//                .id(this.getId())
//                .title(this.getTitle())
//                .authorInfoDTO(this.getAuthor().powerMemberInfoDTO())
//                .menuId(this.getMenu().getId())
//                .menuName(this.getMenu().getMenuName())
//                .productName(this.getProductName())
//                .projectName(this.getVersion())
//                .postType(this.getPostType())
//                .content(this.getContent())
//                .createTime(this.getCreateTime())
//                .build();
//    }

//    public PostInfoDTO1 toPostInfoDTO1(){
//        return PostInfoDTO1.builder()
//                .id(this.getId())
//                .title(this.getTitle())
//                .authorInfoDTO(this.getAuthor().powerMemberInfoDTO())
//                //.menuId(this.getMenu().getId())
//                //.menuName(this.getMenu().getMenuName())
//                //.productName(this.getProductName())
//                //.projectName(this.getVersion())
//                .postType(this.getPostType())
//                .content(this.getContent())
//                .createTime(this.getCreateTime())
//                .build();
//    }

    public SimplePostInfoDTO toSimplePostInfoDTO(){
        return SimplePostInfoDTO.builder()
                .id(this.id)
                .postTitle(this.title)
                .build();

    }
    public void addPostTag(PostTag postTag) {
        postTagList.add(postTag);
    }

    public void updatePostTagList(List<PostTag> postTags){this.postTagList=postTags;}

    public void updatePostTitle(String updateTitle){this.title=updateTitle;}
    public void updatePostContent(String updatecontent){this.content=updatecontent;}
    public void updatePostMenu(Menu menu){this.menu=menu;}
    public void updatePostType(PostType updatepostType){this.postType=updatepostType;}
    public  void updatePostProductName(String updateProductName){this.productName=updateProductName;}
    public void updatePostVersion(String updateVersion){this.version=updateVersion;}
//    public void addTag(Tag tag) {
//        if (postTagList == null) {
//            postTagList = new ArrayList<>();
//        }
//        PostTag postTag = new PostTag(this, tag);
//        postTagList.add(postTag);
//    }
//    public void addPostTag(PostTag postTag) {
//        if (postTag != null) {
//            this.postTagList.add(postTag);
//            postTag.getPost().getPostTagList().add(postTag);
//        }
//    }

    public PostInfoDTO toPostInfoDTO(List<TagInfoDTO> postTagList){
        return  PostInfoDTO.builder()
                .id(this.id)
                .title(this.title)
                .authorInfoDTO(this.author.powerMemberInfoDTO())
                .menuId(this.menu.getId())
                .menuName(this.menu.getMenuName())
                .productName(this.productName)
                .projectName(this.version)
                .postType(this.postType)
                .content(this.content)
                .postTags(postTagList)
                .createTime(this.getCreateTime())
                .build();
    }

}
