package com.uplog.uplog.domain.member.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
//import com.uplog.uplog.domain.member.dao.RedisDao;
//import com.uplog.uplog.domain.member.dao.RefreshTokenRepository;
import com.uplog.uplog.domain.member.dao.RedisDao;
import com.uplog.uplog.domain.member.dao.RefreshTokenRepository;
import com.uplog.uplog.domain.member.dto.TokenDTO;
import com.uplog.uplog.domain.member.dto.TokenRequestDTO;
import com.uplog.uplog.domain.member.exception.DuplicatedMemberException;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.exception.NotMatchPasswordException;
import com.uplog.uplog.domain.member.model.Authority;
import com.uplog.uplog.domain.member.model.Member;
//import com.uplog.uplog.domain.member.model.RefreshToken;
import com.uplog.uplog.global.exception.ExpireRefreshTokenException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.domain.member.dto.MemberDTO.*;
import com.uplog.uplog.global.jwt.TokenProvider;
import com.uplog.uplog.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Duration;
import java.util.*;

/*
닉네임, 이름 중복 가능. 고유값은 email 하나뿐.
탈퇴시에 해당 멤버 정보는 싹 날리고 리스트나 기록에서는 닉네임대신에 알수없음(이름)이런식으로!!
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    @PersistenceContext
    private EntityManager entityManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisDao redisDao;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private long seconds=10000;
    private long AccessTokenValidityInMilliseconds = Duration.ofMinutes(30).toMillis();//만료시간 30분
    private long RefreshTokenValidityInMilliseconds=Duration.ofDays(14).toMillis(); //만료시간 2주
    //================================Member Create=====================================
    /*
    멤버 생성시 고려해야할 부분.
    email만 유일한 값.
    닉네임, 이름, 중복가능.
    이메일 보내기. 인증번호는 영어, 숫자, 특수문자를 조합한 6자리 인증번호.
    */


    //security 로직 추가
    @Transactional
    public MemberInfoDTO createMember(CreateMemberRequest createMemberRequest){
        //이메일이 존재하는 멤버인지 확인. 존재하면 이미 존재한다고 예외처리
        if(!memberRepository.existsByEmail(createMemberRequest.getEmail())){
            if (memberRepository.findOneWithAuthoritiesByEmail(createMemberRequest.getEmail()).orElse(null) != null) {
                throw new DuplicatedMemberException("이미 존재하는 회원입니다.");
            }

            Authority authority=(getOrCreateAuthority("ROLE_USER"));
            Member member = createMemberRequest.toMemberEntity(authority,passwordEncoder);
            memberRepository.save(member);




            return member.toMemberInfoDTO();
        }
        else{//이미 존재하는 회원
            throw new DuplicatedMemberException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 토큰 재발급
     */
    @Transactional
    public TokenDTO refresh(TokenRequestDTO tokenRequestDto) {
        // 1. Refresh Token 검증 (validateToken() : 토큰 검증)
        if(!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new ExpireRefreshTokenException();
        }

        // 2. Access Token에서 ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());


        // 3. 저장소에서 ID를 기반으로 Refresh Token값 가져옴
        String rtkInRedis = redisDao.getValues(authentication.getName());
        if(rtkInRedis==null){
            throw new RuntimeException("로그아웃 된 사용자입니다.");
        }
        // 4. Refresh Token 일치 여부
        if (!rtkInRedis.equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDTO tokenDto = tokenProvider.createToken(authentication);


        // 6. 저장소 정보 업데이트
        redisDao.deleteValues(authentication.getName());

        redisDao.setValues(authentication.getName(),tokenDto.getRefreshToken(),Duration.ofSeconds(seconds));

        //토큰 발급
        return tokenDto;
    }
    private Authority getOrCreateAuthority(String authorityName) {
        Authority existingAuthority = entityManager.find(Authority.class, authorityName);
        if (existingAuthority != null) {
            return existingAuthority;
        } else {
            Authority newAuthority = new Authority(authorityName);
            entityManager.persist(newAuthority);
            return newAuthority;
        }
    }
    //로그인
    @Transactional(readOnly = true)
    public MemberInfoDTO login(LoginRequest loginRequest){
        Member member = memberRepository.findMemberByEmail(loginRequest.getEmail()).orElseThrow(NotFoundMemberByEmailException::new);
        return member.toMemberInfoDTO();
    }

    @Transactional(readOnly = true)
    public Optional<Member> getUserWithAuthorities(String email){
        return memberRepository.findOneWithAuthoritiesByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMyUserWithAuthorities(){
        return SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail);
    }

    //인증 이메일 전송


    //===========================Member Read===============================================
    //멤버 아이디로 조회
    @Transactional(readOnly = true)
    public MemberInfoDTO findMemberById(Long id){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        return member.toMemberInfoDTO();
    }

    //멤버 이메일로 조회
    @Transactional(readOnly = true)
    public MemberInfoDTO findMemberByEmail(String email){
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(NotFoundIdException::new);
        return member.toMemberInfoDTO();
    }

    //멤버 전체 조회
    @Transactional(readOnly = true)
    public FindMembersDTO findAllMembers(){
        List<Member> members = memberRepository.findAll();
        List<MemberInfoDTO> memberInfoDTOList = new ArrayList<>();
        for(Member m : members){
            MemberInfoDTO memberInfoDTO = m.toMemberInfoDTO();
            memberInfoDTOList.add(memberInfoDTO);
        }

        return FindMembersDTO.builder()
                .memberCount(members.size())
                .memberInfoDTOList(memberInfoDTOList)
                .build();
    }



    //==========================Member Update====================================================
    //TODO postman으로 변경사항 잘 반영되어 들어오는지 확인할것!!!!!!! + transaction공부
    //이름 변경
    @Transactional
    public SimpleMemberInfoDTO updateMemberName(Long id,UpdateNameRequest updateNameRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        member.updateName(updateNameRequest.getNewName());
        return member.simpleMemberInfoDTO();
    }
    //닉네임 변경
    @Transactional
    public SimpleMemberInfoDTO updateMemberNickname(Long id,UpdateNicknameRequest updateNicknameRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        member.updateNickname(updateNicknameRequest.getNewNickname());
        return member.simpleMemberInfoDTO();
    }
    //비밀번호 변경
    @Transactional
    public SimpleMemberInfoDTO updateMemberPassword(Long id,UpdatePasswordRequest updatePasswordRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        //기존 비밀번호를 모르면 비밀번호 변경 불가
        if(member.getPassword().equals(updatePasswordRequest.getPassword())) {
            member.updatePassword(updatePasswordRequest.getNewPassword());
            return member.simpleMemberInfoDTO();
        }
        else{
            throw new NotMatchPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }
    //position 변경(있을지 모르겠지만 혹시 모르니까)
    @Transactional
    public SimpleMemberInfoDTO updateMemberPostion(Long id, UpdatePositionRequest updatePositionRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        member.updatePosition(updatePositionRequest.getNewPosition());
        return member.simpleMemberInfoDTO();
    }


    //=========================Member Delete=================================================
    @Transactional
    public String deleteMember(Long id, DeleteMemberRequest deleteMemberRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        //TODO SpringSecurity 하고 나서 getCurrentMember 하고나서 로직 수정 필요함.
        //TODO 닉네임(이름)되어있는 것을 -> (알수없음)(이름)으로 바꾸는 과정 있어야함. -> 나중에 프론트와 상의가 필요할 수도 있음.
        if(member.getPassword().equals(deleteMemberRequest.getPassword())){
            memberRepository.delete(member);
            return "DELETE";
        }
        else{
            throw new NotMatchPasswordException("비밀번호가 일치하지 않습니다.");
        }


    }


}
