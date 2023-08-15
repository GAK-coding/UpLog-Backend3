package com.uplog.uplog.domain.product.dto;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.product.model.ProductMember;
import com.uplog.uplog.domain.team.model.PowerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProductMemberDTO {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateProductMemberRequest{
        private String memberEmail;
        //private Long memberId;
        private Long productId;
        private PowerType powerType;
        private String link;
        //private int type;//0이면 프로덕트 생성 1이면 프로젝트 그룹 생성 -> 아이디, 이메일로 멤버 찾는거 구분때문에 만듬.

        public ProductMember toProductMember(Product product, Member member, Long indexNum){
            return ProductMember.builder()
                    .product(product)
                    .member(member)
                    .powerType(this.powerType)
                    .indexNum(indexNum)
                    .build();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleProductMemberInfoDTO{
        private Long id;
        private Long memberId;
        private String memberName;
        private String memberNickname;
        private Long productId;
    }
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductMemberInfoDTO{
        private Long productId;
        private String productName;
        private String memberName;
        private String memberNickname;
        private PowerType powerType;
        private Long indexNum;

    }
//    @Builder
//    @Getter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class SortedMemberProductInfoDTO{
//        private Long productId;
//        priv
//        private String memberName;
//        private PowerType powerType;
//        private Long index;
//
//    }



    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductMemberPowerDTO{
        private String memberEmail;
        private Long memberId;
        private String memberName;
        private String memberNickName;
        private PowerType powerType;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductMemberPowerListDTO{
        private Long productId;
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
    public static class UpdateProductMemberPowerTypeRequest{
        private PowerType newPowerType;
        private Long memberId;
        //private Long productId;
    }
}
