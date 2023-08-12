package com.uplog.uplog.domain.product.model;

import com.uplog.uplog.domain.product.dto.ProductDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.ProductInfoDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.SimpleProductInfoDTO;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @OneToMany(mappedBy = "product")
    private List<ProductMember> productMemberList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Project> projectList = new ArrayList<>();

    private String company;

    private Long companyId;

    private String name;


    public void updateCompany(String company){this.company = company;}

    public void updateName(String name){this.name = name;}



    @Builder
    public Product(Long id, String company, Long companyId, String name){
        this.id = id;
        this.company = company;
        this.companyId = companyId;
        this.name = name;
    }

    public ProductInfoDTO toProductInfoDTO(List<Long> projectListId){
        return ProductInfoDTO.builder()
                .id(this.id)
                .name(this.name)
                .company(this.company)
                .projectListId(projectListId)
                .build();
    }

    public SimpleProductInfoDTO toSimpleProductInfoDTO(){
        return SimpleProductInfoDTO.builder()
                .id(this.id)
                .name(this.name)
                .company(this.company)
                .build();
    }





}