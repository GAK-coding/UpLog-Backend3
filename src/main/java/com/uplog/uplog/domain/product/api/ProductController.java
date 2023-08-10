package com.uplog.uplog.domain.product.api;

import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.product.application.ProductService;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.*;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerListDTO;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

    @PostMapping(value = "/members/{member-id}/products")
    public ResponseEntity<ProductInfoDTO> createProduct(@PathVariable(name = "member-id") Long companyId, @RequestBody @Validated CreateProductRequest createProductRequest) throws Exception {
        Long pId = productService.createProduct(companyId, createProductRequest);
        ProductInfoDTO productInfoDTO = productService.findProductById(pId);
        return new ResponseEntity<>(productInfoDTO, HttpStatus.CREATED);
    }

    //====================read=======================
    @GetMapping(value = "/products/{product-id}")
    public ResponseEntity<ProductInfoDTO> findProductById(@PathVariable(name = "product-id") Long id) {
        ProductInfoDTO productInfoDTO = productService.findProductById(id);
        return new ResponseEntity<>(productInfoDTO, HttpStatus.OK);
    }

    //    @GetMapping(value = "/products/{product-id}/company")
//    public ResponseEntity<List<ProductInfoDTO>> findProductsByCompany(@RequestParam(name = "company", required = false)String company){
//        List<ProductInfoDTO> productInfoDTOList = productService.findProductsByCompany(company);
//        return new ResponseEntity<>(productInfoDTOList, HttpStatus.OK);
//    }
    @GetMapping(value = "/products/{product-id}/company")
    public ResponseEntity<List<ProductInfoDTO>> findProductsByCompany(@PathVariable(name = "product-id")Long productId) {
        List<ProductInfoDTO> productInfoDTOList = productService.findProductsByCompanyId(productId);
        return new ResponseEntity<>(productInfoDTOList, HttpStatus.OK);
    }

    //====================update=====================
    //여기서 조회가 제대로 안되면 실패한것임
    @PatchMapping(value = "/products/{product-id}")
    public ResponseEntity<UpdateProductInfoDTO> updateProduct(@PathVariable(name = "product-id") Long id, @RequestBody @Validated UpdateProductRequest updateProductRequest) throws Exception {
        UpdateResultDTO updateResultDTO = productService.updateProduct(id, updateProductRequest);
        MemberPowerListDTO memberPowerListDTO = productService.findMemberPowerList(id);
        UpdateProductInfoDTO updateProductInfoDTO = UpdateProductInfoDTO.builder()
                .memberPowerListDTO(memberPowerListDTO)
                .updateResultDTO(updateResultDTO)
                .build();
        return new ResponseEntity<>(updateProductInfoDTO, HttpStatus.OK);

    }

}

