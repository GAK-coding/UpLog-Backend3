//package com.uplog.uplog.domain.product.application;
//
//import com.uplog.uplog.domain.member.dao.MemberRepository;
//import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
//import com.uplog.uplog.domain.member.model.Member;
//import com.uplog.uplog.domain.member.model.Position;
//import com.uplog.uplog.domain.product.dao.ProductRepository;
//import com.uplog.uplog.domain.product.dto.ProductDTO;
//import com.uplog.uplog.domain.product.dto.ProductDTO.CreateProductRequest;
//import com.uplog.uplog.domain.product.dto.ProductDTO.ProductInfoDTO;
//import com.uplog.uplog.domain.product.dto.ProductDTO.UpdateProductRequest;
//import com.uplog.uplog.domain.product.dto.ProductDTO.UpdateResultDTO;
//import com.uplog.uplog.domain.product.exception.DuplicatedProductNameException;
//import com.uplog.uplog.domain.product.model.Product;
//import com.uplog.uplog.domain.team.application.MemberTeamService;
//import com.uplog.uplog.domain.team.application.TeamService;
//import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
//import com.uplog.uplog.domain.team.dao.TeamRepository;
//import com.uplog.uplog.domain.team.dto.TeamDTO;
//import com.uplog.uplog.domain.team.dto.TeamDTO.CreateTeamRequest;
//import com.uplog.uplog.domain.team.dto.memberTeamDTO;
//import com.uplog.uplog.domain.team.dto.memberTeamDTO.CreateMemberTeamRequest;
//import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerListDTO;
//import com.uplog.uplog.domain.team.model.MemberTeam;
//import com.uplog.uplog.domain.team.model.PowerType;
//import com.uplog.uplog.domain.team.model.Team;
//import com.uplog.uplog.global.exception.AuthorityException;
//import com.uplog.uplog.global.exception.NotFoundIdException;
//import com.uplog.uplog.global.mail.MailDTO;
//import com.uplog.uplog.global.mail.MailDTO.EmailRequest;
//import com.uplog.uplog.global.mail.MailService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//
////TODO ProjectList null로 넣어놓은거 수정하기 -> 프로젝트가 완료 되면!!
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ProductService {
//    private final ProductRepository productRepository;
//    private final MemberRepository memberRepository;
//    private final TeamRepository teamRepository;
//    private final MemberTeamRepository memberTeamRepository;
//
//    private final TeamService teamService;
//    private final MemberTeamService memberTeamService;
//    private final MailService mailService;
//
//    //========================================Create=============================================
//    //TODO 의논할 것 - 제품이 생성될 때 멤버 아이디 가져오기 -> 그래야 기업을 판단할 수 있고 따로 멤버를 알아야하나? 컬럼으로 넣어줄건데
//    //처음에만 멤버를 받았다가 이름으로 company채우기. -> pathVariable로 하기
//    //기업 내에서 제품 이름은 하나만 있어야함.
//    //기업이 처음 제품 생성할때
//    //기업만 제품을 생성할 수 있음.
//    @Transactional
//    public Long createProduct(Long memberId, CreateProductRequest createProductRequest) throws Exception {
//        Member master = memberRepository.findMemberByEmail(createProductRequest.getMasterEmail()).orElseThrow(NotFoundMemberByEmailException::new);
//        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundIdException::new);
//        if (member.getPosition() == Position.COMPANY) {
//            List<Product> productList = productRepository.findProductsByCompanyId(memberId);
//            for (Product p : productList) {
//                //제품들의 이름이 중복되지 않는지 확인
//                if (createProductRequest.getName().equals(p.getName())) {
//                    throw new DuplicatedProductNameException("제품 이름이 중복됩니다.");
//                }
//            }
//            //팀 생성 후 프로덕트 생성
//            CreateTeamRequest saveTeamRequest = CreateTeamRequest.builder()
//                    .teamName(createProductRequest.getName())
//                    .memberEmail(createProductRequest.getMasterEmail())
//                    .link(createProductRequest.getLink())
//                    .mailType(2)
//                    .build();
//            Long teamId = teamService.saveTeam(saveTeamRequest);
//
//            //이게 없으면 널값이 들어감.
////            log.info(teamId.toString());
////            Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
////            product.addTeamToProduct(team);
//
//
//            //return product.toProductInfoDTO(null);
//            Long index = productRepository.countProductsByCompanyId(memberId);
//            Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
//            Product product = createProductRequest.toProductEntity(member.getName(),memberId, team, index);
//            productRepository.save(product);
//
//            return product.getId();
//        } else {
//            throw new AuthorityException("제품 생성 권한이 없습니다.");
//        }
//
//    }
//
//
//    //=====================================Read================================================
//    //프로덕트 내에 멤버 리스트 출력
//    @Transactional(readOnly = true)
//    public MemberPowerListDTO findMemberPowerList(Long productId) {
//        Product product = productRepository.findById(productId).orElseThrow(NotFoundIdException::new);
//        Team team = teamRepository.findById(product.getTeam().getId()).orElseThrow(NotFoundIdException::new);
//        List<String> leaderList = new ArrayList<>();
//        List<String> clientList = new ArrayList<>();
//        List<String> workerList = new ArrayList<>();
//        String master = "";
//
//        List<MemberTeam> masterL = memberTeamRepository.findMemberTeamsByTeamIdAndPowerType(team.getId(), PowerType.MASTER);
//        List<MemberTeam> leaderL = memberTeamRepository.findMemberTeamsByTeamIdAndPowerType(team.getId(), PowerType.LEADER);
//        List<MemberTeam> clientL = memberTeamRepository.findMemberTeamsByTeamIdAndPowerType(team.getId(), PowerType.CLIENT);
//        List<MemberTeam> workerL = memberTeamRepository.findMemberTeamsByTeamIdAndPowerType(team.getId(), PowerType.DEFAULT);
//
//        //마스터
//        for (MemberTeam m : masterL) {
//            master = m.getMember().getEmail();
//        }
//        //리더 리스트
//        for (MemberTeam l : leaderL) {
//            leaderList.add(l.getMember().getEmail());
//        }
//        //작업자 리스트
//        for (MemberTeam w : workerL) {
//            workerList.add(w.getMember().getEmail());
//        }
//        //의뢰인 리스트
//        for (MemberTeam c : clientL) {
//            clientList.add(c.getMember().getEmail());
//        }
//        return MemberPowerListDTO.builder()
//                .poductId(productId)
//                .productName(product.getName())
//                .master(master)
//                .leaderCnt(leaderList.size())
//                .leaderList(leaderList)
//                .workerCnt(workerList.size())
//                .workerList(workerList)
//                .clientCnt(clientList.size())
//                .clientList(clientList)
//                .build();
//    }
//
//
//
//    //TODO 프로젝트 만들어지면 null 말고 arrayList로 넘기기
//    @Transactional(readOnly = true)
//    public ProductInfoDTO findProductById(Long id) {
//        Product product = productRepository.findById(id).orElseThrow(NotFoundIdException::new);
//
//        return product.toProductInfoDTO(null);
//    }
//
//    //기업별 멤버가 속한 그룹 순서대로 출력
//
//
//    //기업별로 제품 목록 불러오기 -> 이름으로 찾음
//    //기업별로 제품 목록 불러오기 -> 이름으로 찾는건 비효율적.
////    @Transactional(readOnly = true)
////    public List<ProductInfoDTO> findProductsByCompany(String company){
////        List<ProductInfoDTO> productInfoDTOList = new ArrayList<>();
////        List<Product> productList = productRepository.findProductsByCompany(company);
////        for(Product p : productList){
////            productInfoDTOList.add(p.toProductInfoDTO(null));
////        }
////        return productInfoDTOList;
////    }
//    @Transactional(readOnly = true)
//    public List<ProductInfoDTO> findProductsByCompany(String company){
//        List<ProductInfoDTO> productInfoDTOList = new ArrayList<>();
//        List<Product> productList = productRepository.findProductsByCompany(company);
//        for(Product p : productList){
//            productInfoDTOList.add(p.toProductInfoDTO(null));
//        }
//        return productInfoDTOList;
//    }
//    //============================Update==================================
//    //제품 수정
//    @Transactional
//    public UpdateResultDTO updateProduct(Long productId, UpdateProductRequest updateProductRequest) throws Exception {
//        Product product = productRepository.findById(productId).orElseThrow(NotFoundIdException::new);
//        List<String> failMemberList = new ArrayList<>();
//        List<String> duplicatedMemberList = new ArrayList<>();
//
//        if (updateProductRequest.getNewName() != null) {
//            product.updateName(updateProductRequest.getNewName());
//        }
//        if (!updateProductRequest.getMemberEmailList().isEmpty()) {
//            for (String s : updateProductRequest.getMemberEmailList()) {
//                //존재하지 않는 멤버라면 리스트에 저장하고 출력
//                if (memberRepository.existsByEmail(s)) {
//                    //팀 멤버 내에 초대된 사람인지 중복 확인
//                    if(!memberTeamRepository.existsMemberTeamByMember_EmailAndTeamId( s,product.getTeam().getId())) {
//                        CreateMemberTeamRequest saveMemberTeamRequest = CreateMemberTeamRequest.builder()
//                                .teamId(product.getTeam().getId())
//                                .memberEmail(s)
//                                .powerType(updateProductRequest.getPowerType())
//                                .mailType(2)
//                                .build();
//                        memberTeamService.createMemberTeam(saveMemberTeamRequest);
////                        EmailRequest emailRequest = EmailRequest.builder()
////                                .email(s)
////                                .type(2)
////                                .link(updateProductRequest.getLink())
////                                .powerType(updateProductRequest.getPowerType())
////                                .build();
////                        mailService.sendSimpleMessage(emailRequest);
//                    }
//                    else{
//                        duplicatedMemberList.add(s);
//                    }
//                } else {
//                    failMemberList.add(s);
//                }
//            }
//
//        }
//        return UpdateResultDTO.builder()
//                .failCnt(failMemberList.size())
//                .failMemberList(failMemberList)
//                .duplicatedCnt(duplicatedMemberList.size())
//                .duplicatedMemberList(duplicatedMemberList)
//                .build();
//
//    }
//
//    //마스터, 리더들이 제품에 멤버 추가할때
//
//
//    //제품수정(이름,이미지,의뢰인추가->의뢰인추가는 팀에서 관리해야하는거같기도?)
//
//
//}
//
