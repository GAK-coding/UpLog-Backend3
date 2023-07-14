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


//    public ProductDTO createProduct(ProductDTO productDTO) {
//
//    }



}
