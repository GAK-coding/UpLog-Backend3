package com.uplog.uplog.domain.chatting.model;

import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChattingRoom extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chattingRoom_id")
    private Long id;

    @OneToMany(mappedBy = "chattingRoom")
    private List<MemberChattingRoom> memberChattingRoomList;


}
