package com.uplog.uplog.domain.product.model;

import com.uplog.uplog.domain.product.dto.ProductDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.ProductInfoDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.SimpleProductInfoDTO;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.project.dto.ProjectDTO.VerySimpleProjectInfoDTO;
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

    private String image;


    public void updateCompany(String company){this.company = company;}

    public void updateImage(String image){this.image = image;}

    public void updateName(String name){this.name = name;}



    @Builder
    public Product(Long id, String company, Long companyId, String name, String image){
        this.id = id;
        this.image = image;
        this.company = company;
        this.companyId = companyId;
        this.name = name;
    }

    public ProductInfoDTO toProductInfoDTO(List<VerySimpleProjectInfoDTO> verySimpleProjectInfoDTOList){
        return ProductInfoDTO.builder()
                .id(this.id)
                .image(this.image)
                .name(this.name)
                .company(this.company)
                .verySimpleProjectInfoDTOList(verySimpleProjectInfoDTOList)
                .build();
    }

    public SimpleProductInfoDTO toSimpleProductInfoDTO(){
        return SimpleProductInfoDTO.builder()
                .id(this.id)
                .image(this.image)
                .name(this.name)
                .company(this.company)
                .build();
    }






}