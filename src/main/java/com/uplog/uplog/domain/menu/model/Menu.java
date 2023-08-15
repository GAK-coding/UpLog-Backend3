package com.uplog.uplog.domain.menu.model;

import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.uplog.uplog.domain.menu.dto.MenuDTO.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noticePost_id")
    private Post noticePost;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "menu",cascade = CascadeType.REMOVE)
    private List<Task> taskList = new ArrayList<>();

    private String menuName;

    public MenuInfoDTO toMenuInfoDTO() {
        MenuInfoDTO.MenuInfoDTOBuilder builder = MenuInfoDTO.builder()
                .id(this.getId())
                .menuName(this.getMenuName())
                .projectId(this.getProject().getId())
                .version(this.getProject().getVersion());

        if (this.getNoticePost() != null) {
            builder.noticePostId(this.getNoticePost().getId());
            // .noticePost(this.getNoticePost().toPostInfoDTO())
        }

        return builder.build();
    }

    public SimpleMenuInfoDTO toSimpleMenuInfoDTO(){
        return SimpleMenuInfoDTO.builder()
                .id(this.id)
                .menuName(this.menuName)
                .build();

    }
    public void updateMenuName(String updateMenuName){this.menuName=updateMenuName;}
    public void updateNoticePost(Post updateNoticePost){this.noticePost=updateNoticePost;}



}
