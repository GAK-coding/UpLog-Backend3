package com.uplog.uplog.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplog.uplog.domain.member.application.MemberService;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.model.LoginType;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@MockBean(JpaMetamodelMappingContext.class)
@RunWith(SpringRunner.class)
public class MemberControllerTest {
    @Autowired
    private  MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        Member member = Member.builder()
                .email("yun@naver.com")
                .name("윤")
                .nickname("쁑")
                .loginType(LoginType.UPLOG)
                .password(passwordEncoder.encode("12345678"))
                .position(Position.INDIVIDUAL)
                .build();
        memberRepository.save(member);

    }

    @AfterEach
    public void afterEach(){
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("login controller success")
    public void login() throws Exception{
        setup();
        String userEmail = "yun@naver.com";
        String password = "12345678";
        MemberDTO.LoginRequest loginRequest = MemberDTO.LoginRequest.builder()
                .email(userEmail)
                .password(password)
                .build();
        //memberService.login(loginRequest);

//        //when
//        try {
//            mvc.perform(post("/members/login"))
//                    .andDo(print())
//                    .andExpect(status().isOk())
//                    .andExpect(content().json(""));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        // Create and save a member

        // when & then
        mvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

    }
}
