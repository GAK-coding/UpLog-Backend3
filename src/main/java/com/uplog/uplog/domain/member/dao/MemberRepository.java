package com.uplog.uplog.domain.member.dao;

import com.uplog.uplog.domain.member.model.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findOneWithAuthoritiesByEmail(String email);
    Optional<Member> findMemberById(Long id);
    Optional<Member> findMemberByIdOrEmail(Long id, String email);

    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findMemberByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsById(Long id);



}