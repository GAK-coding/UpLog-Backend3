package com.uplog.uplog.domain.product.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.product.dao.ProductMemberRepository;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.*;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.*;
import com.uplog.uplog.domain.product.exception.DuplicatedProductNameException;
import com.uplog.uplog.domain.product.exception.MasterException;
import com.uplog.uplog.domain.product.exception.UpdatePowerTypeException;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.product.model.ProductMember;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.project.dto.ProjectDTO.VerySimpleProjectInfoDTO;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.project.model.QProject;
import com.uplog.uplog.domain.team.application.MemberTeamService;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.UpdateMemberPowerTypeRequest;
import com.uplog.uplog.domain.team.model.*;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
    private final MemberTeamRepository memberTeamRepository;
    private final ProjectRepository projectRepository;
    private final PostRepository postRepository;
    private final MenuRepository menuRepository;


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
        log.info("가");
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
            log.info("나");
            //기업에 들어갈 시에는 클라이언트로 들어감.
            CreateProductMemberRequest createProductMemberRequest2 = CreateProductMemberRequest.builder()
                    .memberEmail(member.getEmail())
                    .productId(product.getId())
                    .link(createProductRequest.getLink())
                    .powerType(PowerType.CLIENT)
                    .build();
            productMemberService.createProductMember(createProductMemberRequest2);
            log.info("다");

            //마스터 생성
            CreateProductMemberRequest createProductMemberRequest = CreateProductMemberRequest.builder()
                    .memberEmail(createProductRequest.getMasterEmail())
                    .productId(product.getId())
                    .link(createProductRequest.getLink())
                    .powerType(PowerType.MASTER)
                    .build();
            productMemberService.createProductMember(createProductMemberRequest);
            log.info("라");


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

    //제품에 속한 사람들 출력
    @Transactional(readOnly = true)
    public List<ProductMemberPowerDTO> findMembersByProductId(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(NotFoundIdException::new);
        List<ProductMemberPowerDTO> productMemberPowerDTOList = new ArrayList<>();
        for(ProductMember pm : product.getProductMemberList()){
            productMemberPowerDTOList.add(pm.toProductMemberPowerDTO());
        }
        return productMemberPowerDTOList;
    }

    //프로덕트 아이디로 프로덕트 찾기
    @Transactional(readOnly = true)
    public ProductInfoDTO findProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(NotFoundIdException::new);
        List<VerySimpleProjectInfoDTO> verySimpleProjectInfoDTOList = new ArrayList<>();
        List<Project> projectList = projectRepository.findProjectsByProductId(id);
        for(Project p : projectList){
            verySimpleProjectInfoDTOList.add(p.toVerySimpleProjectInfoDTO());
        }

        return product.toProductInfoDTO(verySimpleProjectInfoDTOList);
    }

    //프로덕트 아이디로 간단한 프로덕트 정보 넘겨주기
    @Transactional(readOnly = true)
    public SimpleProductInfoDTO findSimpleProductById(Long id){
        Product product = productRepository.findById(id).orElseThrow(NotFoundIdException::new);
        return product.toSimpleProductInfoDTO();
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

        //초대는 마스터와 리더만 할 수 있음.
        if(memberProduct.getPowerType()==PowerType.CLIENT||memberProduct.getPowerType()==PowerType.DEFAULT){
            throw new AuthorityException("제품 수정은 마스터와 리더만 가능합니다.");
        }

        //마스터 권한으로 초대시, 제한 -> 마스터는 한명임.
        if(updateProductRequest.getPowerType()==PowerType.MASTER){
            throw new MasterException();
        }

        //제품 이름이 변경되면, 포스트에 있는 이름도 변경해야함.
        //TODO 관련 로직 나중에 성능을 위한 고도화 작업 할 것.
        if (updateProductRequest.getNewName() != null) {
            product.updateName(updateProductRequest.getNewName());
            if(!product.getProjectList().isEmpty()) {
                Project project = projectRepository.findProjectByProductIdAndProjectStatus(productId, ProjectStatus.PROGRESS_IN).orElseThrow();
                List<Menu> menuList = menuRepository.findByProjectId(project.getId());
                for (Menu m : menuList) {
                    List<Post> postList = postRepository.findByMenuId(m.getId());
                    for (Post p : postList) {
                        p.updatePostProductName(updateProductRequest.getNewName());
                    }
                }
            }


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

    //index순서대로 정렬하기 -> 드래그 드랍 반영
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
        for(int i = 0 ; i < updateIndexRequest.getUpdateIndexList().size() ; i++ ){
            ProductMember productMember = productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, updateIndexRequest.getUpdateIndexList().get(i)).orElseThrow(NotFoundIdException::new);
            log.info(productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, updateIndexRequest.getUpdateIndexList().get(i)).orElseThrow(NotFoundIdException::new)+"d");
            log.info(updateIndexRequest.getUpdateIndexList().get(i)+"d");
            log.info(memberId+"d");
            productMember.updateIndex(new Long(i));
        }
    }

    //멤버 권한 바꾸기
    //마스터로 바뀌면 프로젝트에는 리더로 바뀌게됨.
    @Transactional
    public void updateMemberPowerType(Long memberId, Long productId, UpdateProductMemberPowerTypeRequest updateProductMemberPowerTypeRequest){
        ProductMember productMember = productMemberRepository.findProductMemberByMemberIdAndProductId(memberId, productId).orElseThrow(NotFoundIdException::new);
        ProductMember changeMember = productMemberRepository.findProductMemberByMemberIdAndProductId(updateProductMemberPowerTypeRequest.getMemberId(), productId).orElseThrow(NotFoundIdException::new);
        List<ProductMember> master = productMemberRepository.findProductMembersByProductIdAndPowerType(productId, PowerType.MASTER);
        //권한은 마스터와 리더만 바꿀 수 있음.
        if(productMember.getPowerType()==PowerType.CLIENT || productMember.getPowerType() == PowerType.DEFAULT){
            throw new AuthorityException("권한 설정은 마스터와 리더만 가능합니다.");
        }
        //마스터 권한을 바꿀 수 없음.
        if(updateProductMemberPowerTypeRequest.getMemberId().equals(master.get(0).getId())){
            throw new AuthorityException("마스터의 권한은 바꿀 수 없습니다.");
        }
        //마스터로 권한을 변경하는 것을 불가능.
        if(updateProductMemberPowerTypeRequest.getNewPowerType()==PowerType.MASTER){
            throw new UpdatePowerTypeException("마스터 권한은 한 명만 가능하며, 권한 설정이 불가능합니다.");
        }
        changeMember.updatePowerType(updateProductMemberPowerTypeRequest.getNewPowerType());
        //제품에서 권한이 바뀌면, 프로젝트 권한도 자동스럽게 바뀌어야한다.
        //즉, 멤버팀의 권한이 바껴야함. -> 프로젝트 -> 제품에서 프로젝트 가져오기.
        //현재 프로젝트 찾기
        Project project = projectRepository.findProjectByProductIdAndProjectStatus(productId, ProjectStatus.PROGRESS_IN).orElseThrow(NotFoundIdException::new);
        //프로젝트에 멤버가 속한 memberTeam 찾기
        List<MemberTeam> memberTeamList = memberTeamRepository.findMemberTeamsByMemberIdAndProjectId(updateProductMemberPowerTypeRequest.getMemberId(), project.getId());
        for(MemberTeam mt : memberTeamList){
            mt.updatePowerType(updateProductMemberPowerTypeRequest.getNewPowerType());
        }
    }


    //마스터, 리더들이 제품에 멤버 추가할때


}