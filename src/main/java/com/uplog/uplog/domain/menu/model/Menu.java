package com.uplog.uplog.domain.menu.model;

import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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

    @OneToMany(mappedBy = "menu")
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "menu")
    private List<Task> taskList = new ArrayList<>();

    private String menuName;



}
