package com.uplog.uplog.domain.product.model;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.dto.MemberProductDTO;
import com.uplog.uplog.domain.product.dto.MemberProductDTO.SimpleMemberProductInfoDTO;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.uplog.uplog.domain.product.dto.MemberProductDTO.*;

@Entity
//@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberProduct{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberProduct_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private PowerType powerType;

    private Long index; //for drag/drop

    public void updatePowerType(PowerType powerType){this.powerType = powerType;}

    public void updateIndex(Long newIndex){this.index = newIndex;}

    @Builder
    public MemberProduct(Long id, Product product, Member member, PowerType powerType, Long index){
        this.id = id;
        this.product = product;
        this.member = member;
        this.powerType = powerType;
        this.index = index;
    }

    public SimpleMemberProductInfoDTO toSimpleMemberProductInfoDTO(){
        return SimpleMemberProductInfoDTO.builder()
                .id(this.id)
                .memberId(this.member.getId())
                .memberName(this.member.getName())
                .productId(this.product.getId())
                .build();
    }

    public MemberProductInfoDTO toMemberProductInfoDTO(){
        return MemberProductInfoDTO.builder()
                .productId(this.id)
                .productName(this.getProduct().getName())
                .memberName(this.member.getName())
                .powerType(this.powerType)
                .index(this.index)
                .build();
    }
}
