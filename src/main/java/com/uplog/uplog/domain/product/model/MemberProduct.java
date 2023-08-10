package com.uplog.uplog.domain.product.model;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Builder
    public MemberProduct(Long id, Product product, Member member, PowerType powerType, Long index){
        this.id = id;
        this.product = product;
        this.member = member;
        this.powerType = powerType;
        this.index = index;
    }

}
