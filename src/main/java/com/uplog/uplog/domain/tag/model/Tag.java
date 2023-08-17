package com.uplog.uplog.domain.tag.model;

import com.uplog.uplog.domain.tag.dto.TagDTO;
import com.uplog.uplog.domain.tag.dto.TagDTO.TagInfoDTO;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tag extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @OneToMany(mappedBy = "tag")
    private List<PostTag> postTagList = new ArrayList<>();

    private String content;

    @Builder
    public Tag(Long id, String content){
        this.id = id;
        this.content = content;
    }

    public TagInfoDTO toTagInfoDTO(){
        return TagInfoDTO.builder()
                .id(this.id)
                .content(this.content)
                .build();
        }
    }


