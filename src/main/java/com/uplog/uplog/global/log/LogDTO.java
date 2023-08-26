package com.uplog.uplog.global.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LogDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class LogRequestDTO{
        private String page;
        private boolean status; //status가 true이면 성공- 메세지는 무조건 success., false이면 실패.
        private String message;

    }
}
