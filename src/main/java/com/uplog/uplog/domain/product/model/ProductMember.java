package com.uplog.uplog.domain.product.model;

import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.ProductMemberInfoDTO;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.ProductMemberPowerDTO;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.SimpleProductMemberInfoDTO;
import com.uplog.uplog.domain.team.model.PowerType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ProductMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productMember_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private PowerType powerType;

    private Long indexNum; //for drag/drop

    public void updatePowerType(PowerType powerType) {
        this.powerType = powerType;
    }

    public void updateIndex(Long newIndex) {
        this.indexNum = newIndex;
    }

    @Builder
    public ProductMember(Long id, Product product, Member member, PowerType powerType, Long indexNum) {
        this.id = id;
        this.product = product;
        this.member = member;
        this.powerType = powerType;
        this.indexNum = indexNum;
    }

    public SimpleProductMemberInfoDTO toSimpleProductMemberInfoDTO() {
        return SimpleProductMemberInfoDTO.builder()
                .id(this.id)
                .memberId(this.member.getId())
                .memberName(this.member.getName())
                .memberNickname(this.member.getNickname())
                .productId(this.product.getId())
                .build();
    }

    public ProductMemberInfoDTO toProductMemberInfoDTO() {
        return ProductMemberInfoDTO.builder()
                .productId(this.product.getId())
                .productName(this.getProduct().getName())
                .memberName(this.member.getName())
                .memberNickname(this.member.getNickname())
                .powerType(this.powerType)
                .indexNum(this.indexNum)
                .build();
    }

    public ProductMemberPowerDTO toProductMemberPowerDTO(){
        return ProductMemberPowerDTO.builder()
                .memberId(this.member.getId())
                .memberEmail(this.member.getEmail())
                .memberName(this.member.getName())
                .memberNickName(this.member.getNickname())
                .powerType(this.powerType)
                .build();
    }
}
