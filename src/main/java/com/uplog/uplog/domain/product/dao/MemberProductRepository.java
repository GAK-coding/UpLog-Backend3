package com.uplog.uplog.domain.product.dao;


import com.uplog.uplog.domain.product.model.MemberProduct;
import com.uplog.uplog.domain.team.model.PowerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberProductRepository extends JpaRepository<MemberProduct, Long> {
    Optional<MemberProduct> findById(Long id);
    List<MemberProduct> findMemberProductsByMemberId(Long memberId);
    Long countMemberProductsByMemberId(Long memberId);
    Optional<MemberProduct> findMemberProductByMemberIdAndProductId(Long memberId, Long ProductId);
    List<MemberProduct> findMemberProductsByProductIdAndPowerType(Long productId, PowerType powerType);
    //List<MemberProduct> findMemberProductsByMemberIdAndOrderByIndex(Long memberId);
    boolean existsMemberProductsByMemberEmailAndProductId(String memberEmail, Long productId);

}
