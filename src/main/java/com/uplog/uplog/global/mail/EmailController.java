package com.uplog.uplog.global.mail;

import com.uplog.uplog.global.error.ErrorResponse;
import com.uplog.uplog.global.mail.MailDTO.EmailRequest;
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
    @PostMapping("/members/email-request")
    public ResponseEntity<ErrorResponse> sendEmail(@RequestBody @Validated EmailRequest emailRequest) throws Exception {
        ErrorResponse errorResponse = mailService.sendSimpleMessage(emailRequest);
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }
}
