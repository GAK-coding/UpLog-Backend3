package com.uplog.uplog.domain.member.dto;

import com.uplog.uplog.domain.member.model.LoginType;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest{
        private String email;
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailRequest{
        private String email;
        private int type;
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
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PowerMemberInfoDTO{
        private Long id;
        private String name;
        private String nickname;
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
       // private Long id;
        private String password;
        private String newPassword;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeNameRequest{
        //private Long id;
        private String newName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangeNicknameRequest{
        //private long id;
        private String newNickname;
    }

    //안쓰일것같지만 혹시 몰라서 만들어놓음.
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangePositionRequest{
       // private long id;
        private Position newPosition;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteMemberRequest{
        private String password;
    }



}
