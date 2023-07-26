package com.uplog.uplog.domain.product.api;

import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.product.application.ProductService;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.ProductInfoDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.SaveProductRequest;
import com.uplog.uplog.domain.product.model.Product;
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

    @PostMapping(value="/products/{member_id}")
    public ResponseEntity<ProductInfoDTO> saveProduct(@PathVariable(name = "member_id") Long companyId, @RequestBody @Validated SaveProductRequest saveProductRequest) {
        Long pId = productService.saveProduct(companyId,saveProductRequest);
        ProductInfoDTO productInfoDTO = productService.readProductById(pId);
        return new ResponseEntity<>(productInfoDTO, HttpStatus.CREATED);
    }

}
