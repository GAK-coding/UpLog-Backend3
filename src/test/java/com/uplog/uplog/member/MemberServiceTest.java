package com.uplog.uplog.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplog.uplog.domain.member.api.AuthController;
import com.uplog.uplog.domain.member.api.MemberController;
import com.uplog.uplog.domain.member.application.MemberService;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.*;
import com.uplog.uplog.domain.member.exception.DuplicatedMemberException;
import com.uplog.uplog.domain.member.exception.NotMatchPasswordException;
import com.uplog.uplog.domain.member.model.LoginType;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.global.error.ErrorResponse;
import org.assertj.core.api.Assertions;
import org.hibernate.sql.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.querydsl.core.alias.MethodType.get;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.web.servlet.function.ServerResponse.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.ServerResponse.temporaryRedirect;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@MockBean(JpaMetamodelMappingContext.class)
@RunWith(SpringRunner.class)
public class MemberServiceTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mvc;

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

    public CreateMemberRequest createMemberRequest() {
        return CreateMemberRequest.builder()
                .email("jjung@naver.com")
                .name("김짱")
                .nickname("짱")
                .password("12345678")
                .position(Position.INDIVIDUAL)
                .build();
    }

    @Test
    @DisplayName("member save 성공 테스트")
    public void successRegisterTest() {
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();

        //when
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //then
        Assertions.assertThat(memberRepository.existsById(memberInfoDTO.getId())).isEqualTo(true);
    }

    @Test
    @DisplayName("member save 실패 테스트 : 이미 존재하는 멤버")
    public void failRegisterTest() {
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();

        memberService.createMember(createMemberRequest);

        //when & then
        org.junit.jupiter.api.Assertions.assertThrows(DuplicatedMemberException.class, () -> {
            MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);
        });
    }


    @Test
    @DisplayName("Login 성공 테스트")
    public void loginSuccessTest() throws Exception {
        //given
        setup();
        String userEmail = "yun@naver.com";
        String password = "12345678";
        LoginRequest loginRequest = LoginRequest.builder()
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
                    .andExpect(status().is4xxClientError())
                    .andReturn();


    }


    @Test
    @DisplayName("member 수정 성공 테스트 : 멤버 이름 변경")
    public void successChangeNameTest() {
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //when
        UpdateMemberRequest updateNameRequest = UpdateMemberRequest.builder()
                .newName("김감자")
                .build();
        memberService.updateMember(memberInfoDTO.getId(), updateNameRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getName()).isEqualTo("김감자");
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 멤버 닉네임 변경")
    public void successChangeNicknameTest() {
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //when
        UpdateMemberRequest updateNicknameRequest = UpdateMemberRequest.builder()
                .newNickname("옹심이")
                .build();
        memberService.updateMember(memberInfoDTO.getId(), updateNicknameRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getNickname()).isEqualTo("옹심이");
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 비밀번호 변경")
    public void successChangePwdTest() {
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //when
        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .password("12345678")
                .newPassword("13579973")
                .build();
        memberService.updateMemberPassword(memberInfoDTO.getId(), updatePasswordRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(passwordEncoder.matches("13579973",member.getPassword())).isEqualTo(true);
    }

    @Test
    @DisplayName("member 수정 실패 테스트 : 비밀번호 변경 실패")
    public void failChangePwdTest() {
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //when
        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .password("12345677")
                .newPassword("13579973")
                .build();

        //then
        org.junit.jupiter.api.Assertions.assertThrows(NotMatchPasswordException.class, () -> {
            memberService.updateMemberPassword(memberInfoDTO.getId(), updatePasswordRequest);
        });
    }

    @Test
    @DisplayName("member 수정 성공 테스트 : 포지션 변경")
    public void successChangePositionTest() {
        //given
        CreateMemberRequest createMemberRequest = createMemberRequest();
        MemberInfoDTO memberInfoDTO = memberService.createMember(createMemberRequest);

        //when
        UpdatePositionRequest updatePositionRequest = UpdatePositionRequest.builder()
                .newPosition(Position.COMPANY)
                .build();
        memberService.updateMemberPostion(memberInfoDTO.getId(), updatePositionRequest);


        //then
        Member member = memberRepository.findMemberById(memberInfoDTO.getId()).get();
        Assertions.assertThat(member.getPosition()).isEqualTo(Position.COMPANY);
    }

}
