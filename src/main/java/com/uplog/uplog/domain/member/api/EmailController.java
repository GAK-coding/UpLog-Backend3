package com.uplog.uplog.domain.member.api;

import com.uplog.uplog.domain.member.application.MailService;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.EmailRequest;
import com.uplog.uplog.global.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class EmailController {
    private final MailService mailService;
    @PostMapping("/member/email-request")
    public ResponseEntity<ErrorResponse> sendEmail(@RequestBody @Validated EmailRequest emailRequest) throws Exception {
        ErrorResponse errorResponse = mailService.sendSimpleMessage(emailRequest);
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }
}
