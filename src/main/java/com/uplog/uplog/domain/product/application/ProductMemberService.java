package com.uplog.uplog.domain.product.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.dao.ProductMemberRepository;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.CreateProductMemberRequest;
import com.uplog.uplog.domain.product.dto.ProductMemberDTO.UpdateProductMemberPowerTypeRequest;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.product.model.ProductMember;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.mail.MailDTO;
import com.uplog.uplog.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductMemberService {
    private final MemberRepository memberRepository;
    private final ProductMemberRepository productMemberRepository;
    private final ProductRepository productRepository;

    private final MailService mailService;

    @Transactional
    public Long createProductMember(CreateProductMemberRequest createProductMemberRequest) throws Exception {
        Member member = memberRepository.findMemberByEmail(createProductMemberRequest.getMemberEmail()).orElseThrow(NotFoundMemberByEmailException::new);
        Product product = productRepository.findById(createProductMemberRequest.getProductId()).orElseThrow(NotFoundIdException::new);
        Long index = productMemberRepository.countProductMembersByMemberId(member.getId());
        ProductMember memberProduct = createProductMemberRequest.toProductMember(product, member, index+1);

        productMemberRepository.save(memberProduct);
        //TODO Null point 확인
//        log.info(team.getName());
//        log.info(team.getMemberTeamList()+"null?");
//        log.info(memberTeam+"what null");
        product.getProductMemberList().add(memberProduct);
        //MemberTeamInfoDTO memberTeamInfoDTO = memberTeam.toMemberTeamInfoDTO();
        MailDTO.EmailRequest emailRequest = MailDTO.EmailRequest.builder()
                .email(member.getEmail())
                .type(2)
                .link(createProductMemberRequest.getLink())
                .powerType(createProductMemberRequest.getPowerType())
                .build();
        mailService.sendSimpleMessage(emailRequest);

        return memberProduct.getId();

    }

//    @Transactional(readOnly = true)
//    public MemberPowerListDTO memberPowerList(Long teamId){
//        MemberTeam master;
//        List<Long> leaderList = new ArrayList<>();
//        List<Long> clientList = new ArrayList<>();
//
//        if()
//    }

    @Transactional
    public Long updateMemberPowerType(Long productId, UpdateProductMemberPowerTypeRequest updateMemberPowerTypeRequest) {
        ProductMember memberProduct = productMemberRepository.findProductMemberByMemberIdAndProductId(updateMemberPowerTypeRequest.getMemberId(), productId).orElseThrow(NotFoundIdException::new);
        memberProduct.updatePowerType(updateMemberPowerTypeRequest.getNewPowerType());
        return memberProduct.getId();
    }


}
