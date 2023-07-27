package com.uplog.uplog.domain.product.dto;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerDTO;
import com.uplog.uplog.domain.team.model.MemberTeam;
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
        private String link;

        public Product toProductEntity(String company, Team team){
            return Product.builder()
                    .name(this.name)
                    .team(team)
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
        private Long teamId;
        private List<Long> projectListId;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProductRequest{
        private String link;
        private String newName;
        private List<String> clientEmailList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateResultDTO{
        private int failCnt;
        private List<String> failMemberList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProductInfoDTO{
        private Long id;
        private String name;
        private List<MemberPowerDTO> memberPowerDTOList;
        private UpdateResultDTO updateResultDTO;
    }






}
