package com.uplog.uplog.domain.member.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository){
        this.memberRepository=memberRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String name){
        System.out.println("32");
        return memberRepository.findOneWithAuthoritiesByEmail(name)
                .map(member->createUser(name,member))
                .orElseThrow(()-> new UsernameNotFoundException(name + " -> 데이터베이스에서 찾을 수 없습니다."));

    }
    private org.springframework.security.core.userdetails.User createUser(String name, Member member){
        System.out.println("38");
        //if(member.getActivated()==false){
        //    throw new RuntimeException(name + " -> 활성화되어 있지 않습니다.");
        //}
        System.out.println("33");
        List<GrantedAuthority>grantedAuthorities=member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        System.out.println("34");
        return new org.springframework.security.core.userdetails.User(member.getEmail(),
            member.getPassword(),
            grantedAuthorities);
        }
}
