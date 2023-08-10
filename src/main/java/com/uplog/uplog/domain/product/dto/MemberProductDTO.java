package com.uplog.uplog.domain.product.dto;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.model.MemberProduct;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MemberProductDTO {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateMemberProductRequest{
        private String memberEmail;
        private Long memberId;
        private Long productId;
        private PowerType powerType;
        private String link;
        //private int type;//0이면 프로덕트 생성 1이면 프로젝트 그룹 생성 -> 아이디, 이메일로 멤버 찾는거 구분때문에 만듬.

        public MemberProduct toMemberProduct(Product product, Member member, Long index){
            return MemberProduct.builder()
                    .product(product)
                    .member(member)
                    .powerType(this.powerType)
                    .index(index)
                    .build();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberProductInfoDTO{
        private Long id;
        private Long memberId;
        private String memberName;
        private Long productId;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberPowerDTO{
        private String memberEmail;
        private PowerType powerType;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberPowerListDTO{
        private Long poductId;
        private String productName;
        private String master;
        private int leaderCnt;
        private List<String> leaderList;
        private int workerCnt;
        private List<String> workerList;
        private int clientCnt;
        private List<String> clientList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateMemberPowerTypeRequest{
        private PowerType newPowerType;
        private Long memberId;
        private Long productId;
    }
}
