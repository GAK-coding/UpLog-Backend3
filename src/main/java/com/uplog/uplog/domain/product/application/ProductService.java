package com.uplog.uplog.domain.product.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.product.dao.ProductMemberRepository;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.*;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.CreateProductMemberRequest;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.ProductMemberInfoDTO;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.ProductMemberPowerListDTO;
import com.uplog.uplog.domain.product.exception.DuplicatedProductNameException;
import com.uplog.uplog.domain.product.exception.MasterException;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.product.model.ProductMember;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.team.application.MemberTeamService;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

//TODO ProjectList null로 넣어놓은거 수정하기 -> 프로젝트가 완료 되면!!
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final ProductMemberRepository productMemberRepository;


    private final MemberTeamService memberTeamService;
    private final MailService mailService;
    private final ProductMemberService productMemberService;

    //========================================Create=============================================
    //처음에만 멤버를 받았다가 이름으로 company채우기. -> pathVariable로 하기
    //기업 내에서 제품 이름은 하나만 있어야함.
    //기업이 처음 제품 생성할때
    //기업만 제품을 생성할 수 있음.
    @Transactional
    public Long createProduct(Long memberId, CreateProductRequest createProductRequest) throws Exception {
       // Member master = memberRepository.findMemberByEmail(createProductRequest.getMasterEmail()).orElseThrow(NotFoundMemberByEmailException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundIdException::new);
        //기업인 사람만 제품을 생성할 수 있음.
        if (member.getPosition() == Position.COMPANY) {
            List<Product> productList = productRepository.findProductsByCompany(member.getName());
            for (Product p : productList) {
                //제품들의 이름이 중복되지 않는지 확인
                if (createProductRequest.getName().equals(p.getName())) {
                    throw new DuplicatedProductNameException("제품 이름이 중복됩니다.");
                }
            }
            Product product = createProductRequest.toProductEntity(member.getName(), member.getId());
            productRepository.save(product);

            CreateProductMemberRequest createProductMemberRequest = CreateProductMemberRequest.builder()
                    .memberEmail(createProductRequest.getMasterEmail())
                    .productId(product.getId())
                    .link(createProductRequest.getLink())
                    .powerType(PowerType.MASTER)
                    .build();
            productMemberService.createProductMember(createProductMemberRequest);



            return product.getId();
        } else {
            throw new AuthorityException("제품 생성 권한이 없습니다.");
        }

    }


    //=====================================Read================================================
    //프로덕트 내에 멤버 리스트 출력
    @Transactional(readOnly = true)
    public ProductMemberPowerListDTO findMemberPowerList(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(NotFoundIdException::new);
        List<String> leaderList = new ArrayList<>();
        List<String> clientList = new ArrayList<>();
        List<String> workerList = new ArrayList<>();
        String master = "";

        List<ProductMember> masterL = productMemberRepository.findProductMembersByProductIdAndPowerType(product.getId(), PowerType.MASTER);
        List<ProductMember> leaderL = productMemberRepository.findProductMembersByProductIdAndPowerType(product.getId(), PowerType.LEADER);
        List<ProductMember>clientL = productMemberRepository.findProductMembersByProductIdAndPowerType(product.getId(), PowerType.CLIENT);
        List<ProductMember> workerL = productMemberRepository.findProductMembersByProductIdAndPowerType(product.getId(), PowerType.DEFAULT);

        //마스터
        for (ProductMember m : masterL) {
            master = m.getMember().getEmail();
        }
        //리더 리스트
        for (ProductMember l : leaderL) {
            leaderList.add(l.getMember().getEmail());
        }
        //작업자 리스트
        for (ProductMember w : workerL) {
            workerList.add(w.getMember().getEmail());
        }
        //의뢰인 리스트
        for (ProductMember c : clientL) {
            clientList.add(c.getMember().getEmail());
        }
        return ProductMemberPowerListDTO.builder()
                .productId(productId)
                .productName(product.getName())
                .master(master)
                .leaderCnt(leaderList.size())
                .leaderList(leaderList)
                .workerCnt(workerList.size())
                .workerList(workerList)
                .clientCnt(clientList.size())
                .clientList(clientList)
                .build();
    }

    //멤버아이디로 제품 목록 리스트 뽑기
    @Transactional(readOnly = true)
    public List<SimpleProductInfoDTO> findProductByMemberId(Long memberId){
        List<ProductMember> productMemberList = productMemberRepository.findProductMembersByMemberId(memberId);
        List<SimpleProductInfoDTO> simpleProductInfoDTOList = new ArrayList<>();

        for(ProductMember pm : productMemberList){
            simpleProductInfoDTOList.add(pm.getProduct().toSimpleProductInfoDTO());
        }
        return simpleProductInfoDTOList;
    }


    //TODO 프로젝트 만들어지면 null 말고 arrayList로 넘기기
    @Transactional(readOnly = true)
    public ProductInfoDTO findProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(NotFoundIdException::new);

        return product.toProductInfoDTO(null);
    }

    //기업별로 제품 목록 불러오기 -> 이름으로 찾는건 비효율적.
    @Transactional(readOnly = true)
    public List<ProductInfoDTO> findProductsByCompany(String company){
        List<ProductInfoDTO> productInfoDTOList = new ArrayList<>();
        List<Product> productList = productRepository.findProductsByCompany(company);
        for(Product p : productList){
            productInfoDTOList.add(p.toProductInfoDTO(null));
        }
        return productInfoDTOList;
    }

    //기업 아이디로 제품 찾기
    @Transactional(readOnly = true)
    public List<ProductInfoDTO> findProductsByCompanyId(Long companyId){
        List<ProductInfoDTO> productInfoDTOList = new ArrayList<>();
        List<Product> productList = productRepository.findProductsByCompanyId(companyId);
        for(Product p : productList){
            productInfoDTOList.add(p.toProductInfoDTO(null));
        }
        return productInfoDTOList;
    }
    //============================Update==================================
    //제품 수정
    @Transactional
    public UpdateResultDTO updateProduct(Long memberId, Long productId, UpdateProductRequest updateProductRequest) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow(NotFoundIdException::new);
        ProductMember memberProduct = productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, productId).orElseThrow(NotFoundIdException::new);

        List<String> failMemberList = new ArrayList<>();
        List<String> duplicatedMemberList = new ArrayList<>();
        //TODO 초대는 마스터와 리더만 할 수 있음.
        if(memberProduct.getPowerType()==PowerType.CLIENT||memberProduct.getPowerType()==PowerType.DEFAULT){
            throw new AuthorityException("제품 수정은 마스터와 리더만 가능합니다.");
        }

        //마스터 권한으로 초대시, 제한 -> 마스터는 한명임.
        if(updateProductRequest.getPowerType()==PowerType.MASTER){
            throw new MasterException();
        }

        if (updateProductRequest.getNewName() != null) {
            product.updateName(updateProductRequest.getNewName());
        }
        if (!updateProductRequest.getMemberEmailList().isEmpty()) {
            for (String s : updateProductRequest.getMemberEmailList()) {
                //존재하지 않는 멤버라면 리스트에 저장하고 출력
                if (memberRepository.existsByEmail(s)) {
                    //팀 멤버 내에 초대된 사람인지 중복 확인
                    if(!productMemberRepository.existsProductMembersByMemberEmailAndProductId( s,product.getId())) {
                        CreateProductMemberRequest createMemberProductRequest = CreateProductMemberRequest.builder()
                                .memberEmail(s)
                                .productId(productId)
                                .powerType(updateProductRequest.getPowerType())
                                .link(updateProductRequest.getLink())
                                .build();
                        productMemberService.createProductMember(createMemberProductRequest);
                        //제품에 초대되면 프로젝트에도 추가되어야함.
                        //제일 루트 팀에 초대되어야함.
                        //진행중인 팀을 찾아야함. ->
                        for(Project p : product.getProjectList()) {
                            if(p.getProjectStatus() == ProjectStatus.PROGRESS_IN){
                                Team team = teamRepository.findByProjectIdAndName(p.getId(), p.getVersion()).orElseThrow(NotFoundIdException::new);
                                CreateMemberTeamRequest createMemberTeamRequest = CreateMemberTeamRequest.builder()
                                        .memberEmail(s)
                                        .teamId(team.getId())
                                        .powerType(updateProductRequest.getPowerType() == PowerType.MASTER || updateProductRequest.getPowerType() == PowerType.LEADER ? PowerType.LEADER : updateProductRequest.getPowerType())
                                        .build();
                                memberTeamService.createMemberTeam(createMemberTeamRequest);
                            }

                        }

                    }
                    else{
                        duplicatedMemberList.add(s);
                    }
                } else {
                    failMemberList.add(s);
                }
            }

        }
        return UpdateResultDTO.builder()
                .failCnt(failMemberList.size())
                .failMemberList(failMemberList)
                .duplicatedCnt(duplicatedMemberList.size())
                .duplicatedMemberList(duplicatedMemberList)
                .build();

    }

    @Transactional(readOnly = true)
    public List<ProductMemberInfoDTO> sortProductsByMember(Long memberId){
        List<ProductMember> productMemberList = productMemberRepository.findProductMembersByMemberIdOrderByIndexNum(memberId);
        List<ProductMemberInfoDTO> simpleProductMemberInfoDTOList = new ArrayList<>();
        for(ProductMember mp : productMemberList){
            simpleProductMemberInfoDTOList.add(mp.toProductMemberInfoDTO());
        }
        return simpleProductMemberInfoDTOList;
    }

    //for drag/drop
    //제일 처음 원래 순서대로 목록을 부름
    //updateIndexRequest에는 0번부터 프로젝트의 객체 아이디가 순서대로 들어가있음.
    @Transactional
    public void updateIndex(Long memberId, UpdateIndexRequest updateIndexRequest) {
        List<ProductMember> productMemberList = productMemberRepository.findProductMembersByMemberIdOrderByIndexNum(memberId);
        Long cnt = productMemberRepository.countProductMembersByMemberId(memberId);
        for(int i = 0 ; i < cnt ; i++ ){
            ProductMember productMember = productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, updateIndexRequest.getUpdateIndexList().get(i)).orElseThrow(NotFoundIdException::new);
            productMember.updateIndex(new Long(i));
        }
    }


    //마스터, 리더들이 제품에 멤버 추가할때


}