package com.uplog.uplog.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDTO {

    private String accessToken;
    public void addTokenToMemberInfoDTO(String accessToken){
        this.accessToken=accessToken;

    }
}
