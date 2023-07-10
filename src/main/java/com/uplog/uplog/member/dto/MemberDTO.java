package com.uplog.uplog.member.dto;

import com.uplog.uplog.member.model.LoginType;
import com.uplog.uplog.member.model.Member;
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

        public Member toMemberEntity(){
            return Member.builder()
                    .email(email)
                    .name(name)
                    .nickname(nickname)
                    .password(password)
                    .position(position)
                    .loginType(loginType)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoDTO{
        private Long id;
        private String email;
        private String name;
        private String nickname;
        private String password;
        private Position position;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleMemberInfoDTO{
        private Long id;
        private String name;
        private String nickname;
        private String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest{
        private Long id;
        private String password;
        private String newPassword;
    }



}
