


package com.uplog.uplog.domain.member.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.member.dao.MemberRepository;
//import com.uplog.uplog.domain.member.dao.RedisDao;
//import com.uplog.uplog.domain.member.dao.RefreshTokenRepository;
import com.uplog.uplog.domain.member.dao.RedisDao;
import com.uplog.uplog.domain.member.dao.RefreshTokenRepository;
import com.uplog.uplog.domain.member.dto.TokenRequestDTO;
import com.uplog.uplog.domain.member.exception.DuplicatedMemberException;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.exception.NotMatchPasswordException;
import com.uplog.uplog.domain.member.model.Authority;
import com.uplog.uplog.domain.member.model.Member;
//import com.uplog.uplog.domain.member.model.RefreshToken;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.QMemberTeam;
import com.uplog.uplog.global.exception.ExpireAccessTokenException;
import com.uplog.uplog.global.exception.ExpireRefreshTokenException;
import com.uplog.uplog.global.exception.InConsistencyRefreshTokenException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.domain.member.dto.MemberDTO.*;
import com.uplog.uplog.global.jwt.CustomHttpStatus;
import com.uplog.uplog.global.jwt.TokenProvider;
import com.uplog.uplog.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private final ProductRepository productRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
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
    public HttpServletResponse refresh(HttpServletRequest request, HttpServletResponse response) {

        TokenRequestDTO tokenRequestDTO=getTokenFromCookie(request);

        String Access=tokenRequestDTO.getAccessToken();
        String Refresh=tokenRequestDTO.getRefreshToken();
        System.out.println("Access : "+Access+"  Refresh: "+Refresh);
        // 1. Refresh Token 검증 (validateToken() : 토큰 검증)
        if(!tokenProvider.validateToken(Refresh,request,"REFRESH")) {

            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            SecurityContextHolder.clearContext();
            System.out.println("REFRESH HERE1 ");
            redisTemplate.opsForValue().set(Access, "logout");
            return response;
            //throw new ExpireRefreshTokenException();

            //TODO 여기 테스트
            //request.setAttribute("exception", CustomHttpStatus.REFRESH_EXPIRED.value());
        }

        // 2. Access Token에서 ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(Refresh);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 3. 저장소에서 ID를 기반으로 Refresh Token값 가져옴
        String rtkInRedis = redisDao.getValues("RT:"+authentication.getName());
        if(rtkInRedis==null){

            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            SecurityContextHolder.clearContext();
            redisTemplate.opsForValue().set(Access, "logout");
            System.out.println("REFRESH HERE2 ");
            request.setAttribute("exception", CustomHttpStatus.REFRESH_EXPIRED.getStatus());
            return response;
            //throw new ExpireRefreshTokenException();
        }
        // 4. Refresh Token 일치 여부
        if (!rtkInRedis.equals(Refresh)) {
            throw new InConsistencyRefreshTokenException();
        }
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        SecurityContextHolder.clearContext();
        //Long expiration = tokenProvider.getExpiration(tokenRequestDto.getAccessToken());
        redisTemplate.opsForValue().set(Access, "logout");
        // 5. 새로운 토큰 생성
        response = tokenProvider.createToken(response,authentication);

        // 6. 저장소 정보 업데이트
        redisDao.deleteValues("RT:"+authentication.getName());

        redisDao.setValues("RT:"+authentication.getName(),Refresh,Duration.ofSeconds(RefreshTokenValidityInMilliseconds));
        //토큰 발급
        return response;
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

    private TokenRequestDTO getTokenFromCookie(HttpServletRequest request){

        String Access="";
        String Refresh="";
        if(request.getCookies()==null)
            throw new ExpireRefreshTokenException();
        else {
            Access = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("Access"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
            Refresh = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("Refresh"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
            if(Access==null){
                throw new ExpireAccessTokenException();
            }
            else if(Refresh==null){
                throw new ExpireRefreshTokenException();
            }
            Access = Access.substring(6);
            Refresh = Refresh.substring(6);
        }
        TokenRequestDTO tokenRequestDTO=new TokenRequestDTO();
        tokenRequestDTO.addTokenToMemberInfoDTO(Access,Refresh);

        return tokenRequestDTO;

    }
    //로그인
    @Transactional(readOnly = true)
    public MemberInfoDTO login(LoginRequest loginRequest, HttpServletResponse response){
        Member member = memberRepository.findMemberByEmail(loginRequest.getEmail()).orElseThrow(NotFoundMemberByEmailException::new);

        if(!this.passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())){
            throw new NotMatchPasswordException("비밀번호가 틀립니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());

        Authentication authentication=authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        response =tokenProvider.createToken(response,authentication);

        return member.toMemberInfoDTO();
    }
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response){

        TokenRequestDTO tokenRequestDTO=getTokenFromCookie(request);
        String Access=tokenRequestDTO.getAccessToken();
        String Refresh=tokenRequestDTO.getRefreshToken();
        // 로그아웃 하고 싶은 토큰이 유효한 지 먼저 검증하기
        if (!tokenProvider.validateToken(Access,request,"ACCESS")){
            throw new ExpireAccessTokenException();
        }

        // Access Token에서 User email을 가져온다
        Authentication authentication = tokenProvider.getAuthentication(Access);

        // Redis에서 해당 User email로 저장된 Refresh Token 이 있는지 여부를 확인 후에 있을 경우 삭제를 한다.
        if (redisTemplate.opsForValue().get("RT:"+authentication.getName())!=null){
            // Refresh Token을 삭제
            redisTemplate.delete("RT:"+authentication.getName());
        }
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        SecurityContextHolder.clearContext();


        // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
        Long expiration = tokenProvider.getExpiration(Access);
        redisTemplate.opsForValue().set(Access,"logout",expiration, TimeUnit.MILLISECONDS);

    }


    @Transactional(readOnly = true)
    public Optional<Member> getUserWithAuthorities(String email){
        return memberRepository.findOneWithAuthoritiesByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMyUserWithAuthorities(){
        Long memberId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        System.out.println("d: "+memberId);
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
    //멤버 변경 한번에 합침
    //이름 변경
    @Transactional
    public VerySimpleMemberInfoDTO updateMember(Long id,UpdateMemberRequest updateMemberRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        if(updateMemberRequest.getNewName()!=null) {
            member.updateName(updateMemberRequest.getNewName());

            //기업인 사용자가 이름을 바꾸면 모든 제품 내에 회사이름을 바꿔줘야함.
            if(member.getPosition()== Position.COMPANY){
                List<Product> productList = productRepository.findProductsByCompanyId(member.getId());
                for(Product p : productList){
                    p.updateCompany(updateMemberRequest.getNewName());
                }
            }
        }
        if(updateMemberRequest.getNewNickname()!=null) {
            member.updateNickname(updateMemberRequest.getNewNickname());
        }
        if(updateMemberRequest.getImage()!=null) {
            member.updateImage(updateMemberRequest.getImage());
        }
        return member.toVerySimpleMemberInfoDTO();
    }
    //비밀번호 변경
    @Transactional
    public SimpleMemberInfoDTO updateMemberPassword(Long id,UpdatePasswordRequest updatePasswordRequest){
        Member member = memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);


        //현재 유저 비밀번호를 가져오기 위해 UserDetails 객체를 현재 유저 이메일로 가져온다.
        UserDetails userDetails=customUserDetailsService.loadUserByUsername(member.getEmail());

        //단순히 equals를 사용하면 암호화 시 매번 암호화 된 문자열이 바뀌기 때문에 아래 메소드를 사용함.
        //userDetails로 가져온 password가 암호화 되어 db에 저장되어 있는 비밀번호, 그리고 전달 받은 평문 암호(새로운 비밀번호)와 동일한 지 검증.
        if (!this.passwordEncoder.matches(updatePasswordRequest.getPassword(), userDetails.getPassword())) {
            throw new NotMatchPasswordException("비밀번호가 일치하지 않습니다.");
        }
        else{
            //일치하면 전달받은 새로운 비밀번호를 암호화 하여 다시 db에 저장.
            member.updatePassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
            return member.simpleMemberInfoDTO();
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
        //MemberBase memberBase=memberRepository.findMemberById(id).orElseThrow(NotFoundIdException::new);
        //TODO SpringSecurity 하고 나서 getCurrentMember 하고나서 로직 수정 필요함.
        //TODO 닉네임(이름)되어있는 것을 -> (알수없음)(이름)으로 바꾸는 과정 있어야함. -> 나중에 프론트와 상의가 필요할 수도 있음.
        //TODO memberTeam 객체 지우고 Team 객체의 memberTeamList 가져와서 해당 되는 memberTeam객체 빼준 뒤에 업데이트 해줘야할듯
        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        QMemberTeam memberTeam=QMemberTeam.memberTeam;


        //QTeam team=QTeam.team;
        if(this.passwordEncoder.matches(deleteMemberRequest.getPassword(), member.getPassword())){

            MemberTeam memberTeam1=query
                    .selectFrom(memberTeam)
                    .where(memberTeam.member.id.eq(id))
                    .fetchOne();

//            Team team1=query
//                    .select(team)
//                    .from(team)
//                    .where(team.id.eq(memberTeam1.getTeam().getId()))
//                    .fetchOne();

            query.delete(memberTeam)
                    .where(memberTeam.member.id.eq(id))
                    .execute();

            // 회원(Member)를 삭제
            memberRepository.delete(member);

            //회원 탈퇴 후에도 토큰이 유효하기 때문에 jwt정보 제거 하고 로그아웃 처리해야 됨
            //TODO 회원 탈퇴 작업할 때 다시 수정
//            SecurityContextHolder.clearContext();
//            TokenRequestDTO tokenRequestDTO=new TokenRequestDTO();
//            tokenRequestDTO.addTokenToMemberInfoDTO(deleteMemberRequest.getAccessToken(),deleteMemberRequest.getRefreshToken());
//            logout(tokenRequestDTO);

            return "DELETE";
        }
        else{
            throw new NotMatchPasswordException("비밀번호가 일치하지 않습니다.");
        }



    }


}
