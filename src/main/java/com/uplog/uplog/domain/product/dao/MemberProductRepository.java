package com.uplog.uplog.domain.product.dao;


import com.uplog.uplog.domain.product.model.MemberProduct;
import com.uplog.uplog.domain.team.model.PowerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberProductRepository extends JpaRepository<MemberProduct, Long> {
    Optional<MemberProduct> findById(Long id);
    List<MemberProduct> findMemberProductsByMemberId(Long memberId);
    Long countMemberProductsByMemberId(Long memberId);
    Optional<MemberProduct> findMemberProductsByMemberIdAndProductId(Long memberId, Long ProductId);
    List<MemberProduct> findMemberProductsByProductIdAndPowerType(Long productId, PowerType powerType);
    boolean existsMemberProductsByMemberEmailAndProductId(String memberEmail, Long productId);

}
