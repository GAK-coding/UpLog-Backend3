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

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

    @PostMapping(value="/products/{member-id}")
    public ResponseEntity<ProductInfoDTO> saveProduct(@PathVariable(name = "member-id") Long companyId, @RequestBody @Validated SaveProductRequest saveProductRequest) throws Exception {
        Long pId = productService.saveProduct(companyId,saveProductRequest);
        ProductInfoDTO productInfoDTO = productService.readProductById(pId);
        return new ResponseEntity<>(productInfoDTO, HttpStatus.CREATED);
    }
    //====================read=======================
    @GetMapping(value = "/products/{product-id}")
    public ResponseEntity<ProductInfoDTO> readProductById(@PathVariable(name = "product-id")Long id){
        ProductInfoDTO productInfoDTO = productService.readProductById(id);
        return new ResponseEntity<>(productInfoDTO, HttpStatus.OK);
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
