package com.uplog.uplog.domain.product.dao;


import com.uplog.uplog.domain.product.model.ProductMember;
import com.uplog.uplog.domain.team.model.PowerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMemberRepository extends JpaRepository<ProductMember, Long> {
    Optional<ProductMember> findById(Long id);
    List<ProductMember> findProductMembersByMemberId(Long memberId);
    Long countProductMembersByMemberId(Long memberId);
    Optional<ProductMember> findProductMemberByMemberIdAndProductId(Long memberId, Long ProductId);
    List<ProductMember> findProductMembersByProductIdAndPowerType(Long productId, PowerType powerType);
    List<ProductMember> findProductMembersByMemberIdOrderByIndexNum(Long memberId);
    boolean existsProductMembersByMemberEmailAndProductId(String memberEmail, Long productId);
    boolean existsProductMemberByMemberIdAndProductId(Long memberId, Long ProductId);

}
