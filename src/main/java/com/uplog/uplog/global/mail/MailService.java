package com.uplog.uplog.global.mail;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.global.error.ErrorResponse;
import com.uplog.uplog.global.mail.MailDTO.EmailRequest;
import com.uplog.uplog.global.method.GlobalMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.validation.constraints.Email;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    @Autowired
    private final JavaMailSender javaMailSender; //Bean으로 등록해둔 MailConfig를 emailsender라는 이름으로 autowired
    //private final GlobalMethod globalMethod;
    private final MemberRepository memberRepository;

    private String authNum;//인증 번호
    private String title="";
    private String subtitle="";

    //메일 내용 작성
    public MimeMessage createMessage(EmailRequest emailRequest) throws MessagingException, UnsupportedEncodingException {
        //authNum = globalMethod.makeRandNum(emailRequest.getType());
        log.info("보내는 대상" + emailRequest.getEmail());
        log.info("authNum : " + authNum);
        MimeMessage message = javaMailSender.createMimeMessage();
        String guide ="";
//        String title = "";
//        String subtitle = "";

        if(emailRequest.getType() == 0){
            title = "UpLog 회원가입 이메일 인증";
            subtitle = "회원가입 인증 코드입니다.";
            guide = "CODE";
        }
        else if(emailRequest.getType() == 1){
            title = "UpLog 비밀번호 변경 이메일 인증";
            subtitle = "비밀번호 변경 인증 코드입니다.";
            guide = "CODE";
        }
        else if(emailRequest.getType() == 2){
            title = "UpLog TeamSpace 초대 이메일";
            subtitle = "TeamSpace 링크입니다.";
            authNum = emailRequest.getLink();
            guide = "Link";
        }
        message.addRecipients(RecipientType.TO, emailRequest.getEmail()); //보내는 대상
        message.setSubject(title); //제목

        String msg = "";
        msg += "<div style='margin:100px;'>";
        //msg += "<h1>안녕하세요</h1>";
        msg += "<h2>안녕하세요, release를 누구나 간편하게!<h3>";
        msg += "<h1>UpLog입니다.";
        msg += "<br>";
        msg += "<p>만족스러운 서비스를 제공하도록 노력하겠습니다. 감사합니다!";
        msg += "<div align='center' style='border:1px solid black; font-family:verdana; font-size:80%';>";
        msg += "<h3 style='color:blue'>";
        msg += subtitle + "</h3>";
        msg += "<div>";
        msg += guide + " : <strong>";
        msg += authNum + "</strong><div><br/>"; //메일에 인증번호 넣기
        msg += "</div>";
        message.setText(msg, "utf-8","html"); //내용, charset 타입, subtype
        message.setFrom(new InternetAddress("gak_uplog@naver.com", "UpLog")); //보내는 사람

        return message;
    }

    //메일 발송
    //MimeMessage 객체 안에 전송할 메일의 내용을 담는다.
    //bean으로 등록해둔 javaMail 객체를 사용해서 이메일을 보낸다.
    @Transactional
    public ErrorResponse sendSimpleMessage(EmailRequest emailRequest) throws Exception{
        GlobalMethod globalMethod = new GlobalMethod();
        authNum = globalMethod.makeRandNum(emailRequest.getType());

        MimeMessage message = createMessage(emailRequest); //메일 생성
        try {//예외 처리
            javaMailSender.send(message); //메일 발송
        }catch(MailException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Fail to send mail", e);
        }

        //비밀번호 변경이라면, 비번 재설정해줘야함.
        if(emailRequest.getType()==1){
            Member member = memberRepository.findMemberByEmail(emailRequest.getEmail()).orElseThrow(NotFoundMemberByEmailException::new);
            member.changePassword(authNum);
        }
        return ErrorResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message(subtitle + authNum)
                .build();
    }




}
