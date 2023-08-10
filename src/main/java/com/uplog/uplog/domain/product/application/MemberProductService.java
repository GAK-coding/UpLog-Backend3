package com.uplog.uplog.domain.product.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.dao.MemberProductRepository;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.MemberProductDTO;
import com.uplog.uplog.domain.product.dto.MemberProductDTO.CreateMemberProductRequest;
import com.uplog.uplog.domain.product.model.MemberProduct;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.Team;
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
public class MemberProductService {
    private final MemberRepository memberRepository;
    private final MemberProductRepository memberProductRepository;
    private final ProductRepository productRepository;

    private final MailService mailService;

    @Transactional
    public Long createMemberProduct(CreateMemberProductRequest createMemberProductRequest) throws Exception {
        Member member = memberRepository.findMemberByEmail(createMemberProductRequest.getMemberEmail()).orElseThrow(NotFoundMemberByEmailException::new);
        Product product = productRepository.findById(createMemberProductRequest.getProductId()).orElseThrow(NotFoundIdException::new);
        Long index = memberProductRepository.countMemberProductsByMemberId(member.getId());
        MemberProduct memberProduct = createMemberProductRequest.toMemberProduct(product, member, index+1);

        memberProductRepository.save(memberProduct);
        //TODO Null point 확인
//        log.info(team.getName());
//        log.info(team.getMemberTeamList()+"null?");
//        log.info(memberTeam+"what null");
        product.getMemberProductList().add(memberProduct);
        //MemberTeamInfoDTO memberTeamInfoDTO = memberTeam.toMemberTeamInfoDTO();
        MailDTO.EmailRequest emailRequest = MailDTO.EmailRequest.builder()
                .email(member.getEmail())
                .type(2)
                .link(createMemberProductRequest.getLink())
                .powerType(createMemberProductRequest.getPowerType())
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
    public Long updateMemberPowerType(memberTeamDTO.UpdateMemberPowerTypeRequest updateMemberPowerTypeRequest) {
        MemberProduct memberProduct = memberProductRepository.findMemberProductsByMemberIdAndProductId(updateMemberPowerTypeRequest.getMemberId(), updateMemberPowerTypeRequest.getTeamId()).orElseThrow(NotFoundIdException::new);
        memberProduct.updatePowerType(updateMemberPowerTypeRequest.getNewPowerType());
        return memberProduct.getId();
    }


}
