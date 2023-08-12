package com.uplog.uplog.domain.product.dto;

import com.uplog.uplog.domain.product.dto.ProductMemberDTO.ProductMemberPowerListDTO;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerListDTO;
import com.uplog.uplog.domain.team.model.PowerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProductDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest{
        //제품 생성시, 제품 이름과 마스터만 지정됨. pathvariable로 들어오는건 기업인멤버
        private String name;
        //private Team team;x
        private String masterEmail;
        private String link;
       // private int mailType; -> 백에서 처리해도 될 것 같음.

        public Product toProductEntity(String company, Long companyId){
            return Product.builder()
                    .name(this.name)
                    .companyId(companyId)
                    .company(company)
                    .build();
        }

    }

    //TODO project list 추가하기
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductInfoDTO{
        private Long id;
        private String name;
        private String company;
        private MemberPowerListDTO memberPowerListDTO;
        private List<Long> projectListId;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleProductInfoDTO{
        private Long id;
        private String name;
        private String company;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateIndexRequest{
        List<Long> updateIndexList;
    }
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProductRequest{
        private String link;
        private String newName;
        private List<String> memberEmailList;
        private PowerType powerType;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateResultDTO{
        private int failCnt;
        private List<String> failMemberList;
        private int duplicatedCnt;
        private List<String> duplicatedMemberList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProductInfoDTO{
        //        private Long id;
//        private String name;
        private ProductMemberPowerListDTO memberPowerListDTO;
        private UpdateResultDTO updateResultDTO;
    }






}