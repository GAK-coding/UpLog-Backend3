package com.uplog.uplog.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDTO {
    private String accessToken;
    private String refreshToken;

    public void addTokenToMemberInfoDTO(String accessToken,String refreshToken){
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;

    }
}
