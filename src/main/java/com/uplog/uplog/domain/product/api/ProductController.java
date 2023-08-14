package com.uplog.uplog.domain.product.api;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.product.application.ProductService;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.*;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.ProductMemberInfoDTO;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.ProductMemberPowerDTO;
import com.uplog.uplog.global.util.SecurityUtil;
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
    private final MemberRepository memberRepository;

    @PostMapping(value="/products")
    public ResponseEntity<ProductInfoDTO> createProduct(@RequestBody @Validated CreateProductRequest createProductRequest) throws Exception {
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        Long pId = productService.createProduct(memberId, createProductRequest);
        ProductInfoDTO productInfoDTO = productService.findProductById(pId);
        return new ResponseEntity<>(productInfoDTO, HttpStatus.CREATED);
    }
    //====================read=======================
    @GetMapping(value = "/products/{product-id}")
    public ResponseEntity<ProductInfoDTO> findProductById(@PathVariable(name = "product-id")Long productId){
        ProductInfoDTO productInfoDTO = productService.findProductById(productId);
        return new ResponseEntity<>(productInfoDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/products/company")
    public ResponseEntity<List<ProductInfoDTO>> findProductsByCompany(@RequestParam(name = "company", required = false)String company){
        List<ProductInfoDTO> productInfoDTOList = productService.findProductsByCompany(company);
        return new ResponseEntity<>(productInfoDTOList, HttpStatus.OK);
    }

    @GetMapping(value = "/products/companyId")
    public ResponseEntity<List<ProductInfoDTO>> findProductsByCompany(@RequestBody @Validated Long companyId){
        List<ProductInfoDTO> productInfoDTOList = productService.findProductsByCompanyId(companyId);
        return new ResponseEntity<>(productInfoDTOList, HttpStatus.OK);
    }

    //멤버 아이디로 제품목록 가져오기
    @GetMapping(value = "/products/member")
    public ResponseEntity<List<SimpleProductInfoDTO>> findProductsByMemberId(){
        Long memberId= SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        List<SimpleProductInfoDTO> simpleProductInfoDTOList = productService.findProductByMemberId(memberId);
        return ResponseEntity.ok(simpleProductInfoDTOList);
    }

    //프로덕트에 속한 멤버 리스트 출력하기
    @GetMapping(value = "products/{product-id}/members")
    public ResponseEntity<List<ProductMemberPowerDTO>> findMembersByProductId(@PathVariable(name = "product-id")Long productId){
        List<ProductMemberPowerDTO> productMemberPowerDTOList = productService.findMembersByProductId(productId);
        return ResponseEntity.ok(productMemberPowerDTOList);
    }

    //멤버별로 제품 정렬된 순서로 불러오기
    @GetMapping(value = "/products")
    public ResponseEntity<List<ProductMemberInfoDTO>> sortProjectByMember(){
        Long memberId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        List<ProductMemberInfoDTO> memberProductInfoDTOList = productService.sortProductsByMember(memberId);
        return ResponseEntity.ok(memberProductInfoDTOList);
    }
    //====================update=====================
    //여기서 조회가 제대로 안되면 실패한것임
    @PatchMapping(value = "/products/{product-id}")
    public ResponseEntity<UpdateProductInfoDTO> updateProduct(@PathVariable(name = "product-id") Long productId, @RequestBody @Validated UpdateProductRequest updateProductRequest) throws Exception {
        Long memberId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        UpdateResultDTO updateResultDTO = productService.updateProduct(memberId, productId, updateProductRequest);
        ProductMemberDTO.ProductMemberPowerListDTO memberPowerListDTO = productService.findMemberPowerList(productId);
        UpdateProductInfoDTO updateProductInfoDTO = UpdateProductInfoDTO.builder()
                .memberPowerListDTO(memberPowerListDTO)
                .updateResultDTO(updateResultDTO)
                .build();
        return new ResponseEntity<>(updateProductInfoDTO, HttpStatus.OK);
    }
    //드래그 드랍을 이용한 순서 바꾸기
    @PatchMapping(value = "/products")
    public ResponseEntity<List<ProductMemberInfoDTO>> updateIndex(@RequestBody @Validated UpdateIndexRequest updateIndexRequest){
        Long memberId=SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail).get().getId();
        productService.updateIndex(memberId, updateIndexRequest);
        List<ProductMemberInfoDTO> productMemberInfoDTOList = productService.sortProductsByMember(memberId);
        return ResponseEntity.ok(productMemberInfoDTOList);
    }

}