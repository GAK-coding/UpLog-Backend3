package com.uplog.uplog.domain.product.application;

import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductDTO;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.model.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;


    //기업이 처음 제품 생성할때
    public ProductDTO createProduct(ProductDTO productDTO) {


    }

    //마스터, 리더들이 제품에 멤버 추가할때



    //제품수정(이름,이미지,의뢰인추가->의뢰인추가는 팀에서 관리해야하는거같기도?)



}
