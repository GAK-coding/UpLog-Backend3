package com.uplog.uplog.domain.product.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductDTO.ProductInfoDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.SaveProductRequest;
import com.uplog.uplog.domain.product.exception.DuplicatedProductNameException;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.application.TeamService;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.TeamDTO.SaveTeamRequest;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    private final TeamService teamService;

    //TODO 의논할 것 - 제품이 생성될 때 멤버 아이디 가져오기 -> 그래야 기업을 판단할 수 있고 따로 멤버를 알아야하나? 컬럼으로 넣어줄건데
    //처음에만 멤버를 받았다가 이름으로 company채우기. -> pathVariable로 하기
    //기업 내에서 제품 이름은 하나만 있어야함.
    //기업이 처음 제품 생성할때
    //기업만 제품을 생성할 수 있음.
    @Transactional
    public Long saveProduct(Long memberId, SaveProductRequest saveProductRequest){
        Member master = memberRepository.findMemberByEmail(saveProductRequest.getMasterEmail()).orElseThrow(NotFoundMemberByEmailException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundIdException::new);
        if(member.getPosition() == Position.COMPANY) {
            List<Product> productList = productRepository.findProductsByCompany(member.getName());
            for(Product p : productList){
                //제품들의 이름이 중복되지 않는지 확인
                if(saveProductRequest.getName().equals(p.getName())){
                    throw new DuplicatedProductNameException("제품 이름이 중복됩니다.");
                }
            }
            //팀 생성 후 프로덕트 생성
            SaveTeamRequest saveTeamRequest = SaveTeamRequest.builder()
                    .teamName(saveProductRequest.getName())
                    .memberEmail(saveProductRequest.getMasterEmail())
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
        }
        else{
            throw new AuthorityException("제품 생성 권한이 없습니다.");
        }

    }

    @Transactional(readOnly = true)
    public ProductInfoDTO asdasd (Long pId) {

        Product product = productRepository.findById(pId).orElseThrow(NotFoundIdException::new);
        return product.toProductInfoDTO(null);
    }


    //TODO 프로젝트 만들어지면 null 말고 arrayList로 넘기기
    @Transactional(readOnly = true)
    public ProductInfoDTO readProductById(Long id){
        Product product = productRepository.findById(id).orElseThrow(NotFoundIdException::new);

        return product.toProductInfoDTO(null);
    }

    //마스터, 리더들이 제품에 멤버 추가할때


    //제품수정(이름,이미지,의뢰인추가->의뢰인추가는 팀에서 관리해야하는거같기도?)


}
