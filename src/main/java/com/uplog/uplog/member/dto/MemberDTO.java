package com.uplog.uplog.member.dto;

import com.uplog.uplog.member.model.LoginType;
import com.uplog.uplog.member.model.Member;
import com.uplog.uplog.member.model.MemberStatus;
import com.uplog.uplog.member.model.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveMemberRequest{
        private String email;
        private String name;
        private String nickname;
        private String password;
        private Position position;
        private LoginType loginType;
        private MemberStatus memberStatus;

        public Member toMemberEntity(){
            return Member.builder()
                    .email(email)
                    .name(name)
                    .nickname(nickname)
                    .password(password)
                    .position(position)
                    .loginType(loginType)
                    .memberStatus(MemberStatus.MEMBER_ACTIVE)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoDTO{
        private Long Id;
        private String email;
        private String name;
        private String nickname;
        private String password;
        private Position position;
    }
}
