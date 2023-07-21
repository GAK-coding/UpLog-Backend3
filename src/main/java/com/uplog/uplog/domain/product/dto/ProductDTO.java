package com.uplog.uplog.domain.product.dto;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.team.model.Team;
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
    public static class SaveProductRequest{
        //제품 생성시, 제품 이름과 마스터만 지정됨. pathvariable로 들어오는건 기업인멤버
        private String name;
        //private Team team;
        private String masterEmail;

        public Product toProductEntity(String company){
            return Product.builder()
                    .name(this.name)
                    //.team(this.team)
                    .company(company)
                    .build();
        }
    }






}
