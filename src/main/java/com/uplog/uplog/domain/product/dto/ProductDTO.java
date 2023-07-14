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
        private Long id;
        private Team team;
        private String company;

        public Product toProductEntity(){
            return Product.builder()
                    .id(id)
                    .team(team)
                    .company(company)
                    .build();
        }
    }






}
