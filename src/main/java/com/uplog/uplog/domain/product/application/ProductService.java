package com.uplog.uplog.domain.product.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.ProductInfoDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.SaveProductRequest;
import com.uplog.uplog.domain.product.dto.ProductDTO.UpdateProductRequest;
import com.uplog.uplog.domain.product.dto.ProductDTO.UpdateResultDTO;
import com.uplog.uplog.domain.product.exception.DuplicatedProductNameException;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.application.MemberTeamService;
import com.uplog.uplog.domain.team.application.TeamService;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.TeamDTO.SaveTeamRequest;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerListDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.SaveMemberTeamRequest;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.mail.MailDTO;
import com.uplog.uplog.global.mail.MailDTO.EmailRequest;
import com.uplog.uplog.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final MemberTeamRepository memberTeamRepository;

    private final TeamService teamService;
    private final MemberTeamService memberTeamService;
    private final MailService mailService;

    //TODO 의논할 것 - 제품이 생성될 때 멤버 아이디 가져오기 -> 그래야 기업을 판단할 수 있고 따로 멤버를 알아야하나? 컬럼으로 넣어줄건데
    //처음에만 멤버를 받았다가 이름으로 company채우기. -> pathVariable로 하기
    //기업 내에서 제품 이름은 하나만 있어야함.
    //기업이 처음 제품 생성할때
    //기업만 제품을 생성할 수 있음.
    @Transactional
    public Long saveProduct(Long memberId, SaveProductRequest saveProductRequest) {
        Member master = memberRepository.findMemberByEmail(saveProductRequest.getMasterEmail()).orElseThrow(NotFoundMemberByEmailException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundIdException::new);
        if (member.getPosition() == Position.COMPANY) {
            List<Product> productList = productRepository.findProductsByCompany(member.getName());
            for (Product p : productList) {
                //제품들의 이름이 중복되지 않는지 확인
                if (saveProductRequest.getName().equals(p.getName())) {
                    throw new DuplicatedProductNameException("제품 이름이 중복됩니다.");
                }
            }
            //팀 생성 후 프로덕트 생성
            SaveTeamRequest saveTeamRequest = SaveTeamRequest.builder()
                    .teamName(saveProductRequest.getName())
                    .memberEmail(saveProductRequest.getMasterEmail())
                    .link(saveProductRequest.getLink())
                    .build();
            Long teamId = teamService.saveTeam(saveTeamRequest);

            //이게 없으면 널값이 들어감.
//            log.info(teamId.toString());
//            Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
//            product.addTeamToProduct(team);


            //return product.toProductInfoDTO(null);
            Team team = teamRepository.findById(teamId).orElseThrow(NotFoundIdException::new);
            Product product = saveProductRequest.toProductEntity(member.getName(), team);
            productRepository.save(product);

            return product.getId();
        } else {
            throw new AuthorityException("제품 생성 권한이 없습니다.");
        }

    }


    //TODO 프로젝트 만들어지면 null 말고 arrayList로 넘기기
    @Transactional(readOnly = true)
    public ProductInfoDTO readProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(NotFoundIdException::new);

        return product.toProductInfoDTO(null);
    }

    //제품 수정
    @Transactional
    public UpdateResultDTO updateProduct(Long productId, UpdateProductRequest updateProductRequest) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow(NotFoundIdException::new);
        List<String> failMemberList = new ArrayList<>();
        if (updateProductRequest.getNewName() != null) {
            product.updateName(updateProductRequest.getNewName());
        }
        if (!updateProductRequest.getMemberEmailList().isEmpty()) {
            for (String s : updateProductRequest.getMemberEmailList()) {
                //존재하지 않는 멤버라면 리스트에 저장하고 출력
                if (memberRepository.existsByEmail(s)) {
                    SaveMemberTeamRequest saveMemberTeamRequest = SaveMemberTeamRequest.builder()
                            .teamId(product.getTeam().getId())
                            .memberEmail(s)
                            .powerType(updateProductRequest.getPowerType())
                            .build();
                    memberTeamService.saveMemberTeam(saveMemberTeamRequest);
                    EmailRequest emailRequest = EmailRequest.builder()
                            .email(s)
                            .type(2)
                            .link(updateProductRequest.getLink())
                            .build();
                    mailService.sendSimpleMessage(emailRequest);
                } else {
                    failMemberList.add(s);
                }
            }

        }
        return UpdateResultDTO.builder()
                .failCnt(failMemberList.size())
                .failMemberList(failMemberList)
                .build();

    }

    //프로덕트 내에 멤버 리스트 출력
    @Transactional(readOnly = true)
    public MemberPowerListDTO findMemberPowerList(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(NotFoundIdException::new);
        Team team = teamRepository.findById(product.getTeam().getId()).orElseThrow(NotFoundIdException::new);
        List<String> leaderList = new ArrayList<>();
        List<String> clientList = new ArrayList<>();
        List<String> workerList = new ArrayList<>();
        String master = "";

        List<MemberTeam> masterL = memberTeamRepository.findMemberTeamsByTeamIdAndPowerType(team.getId(), PowerType.MASTER);
        List<MemberTeam> leaderL = memberTeamRepository.findMemberTeamsByTeamIdAndPowerType(team.getId(), PowerType.LEADER);
        List<MemberTeam> clientL = memberTeamRepository.findMemberTeamsByTeamIdAndPowerType(team.getId(), PowerType.CLIENT);
        List<MemberTeam> workerL = memberTeamRepository.findMemberTeamsByTeamIdAndPowerType(team.getId(), PowerType.DEFAULT);

        //마스터
        for (MemberTeam m : masterL) {
            master = m.getMember().getEmail();
        }
        //리더 리스트
        for (MemberTeam l : leaderL) {
            leaderList.add(l.getMember().getEmail());
        }
        //작업자 리스트
        for (MemberTeam w : workerL) {
            workerList.add(w.getMember().getEmail());
        }
        //의뢰인 리스트
        for (MemberTeam c : clientL) {
            clientList.add(c.getMember().getEmail());
        }
        return MemberPowerListDTO.builder()
                .poductId(productId)
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

    //마스터, 리더들이 제품에 멤버 추가할때


    //제품수정(이름,이미지,의뢰인추가->의뢰인추가는 팀에서 관리해야하는거같기도?)


}
