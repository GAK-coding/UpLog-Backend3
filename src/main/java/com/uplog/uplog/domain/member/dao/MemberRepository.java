package com.uplog.uplog.domain.member.dao;

import com.uplog.uplog.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberById(Long id);
    Optional<Member> findMemberByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsById(Long id);



}
