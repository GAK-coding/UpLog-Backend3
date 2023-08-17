package com.uplog.uplog.global.method;

import com.uplog.uplog.domain.changedIssue.dao.ChangedIssueRepository;
import com.uplog.uplog.domain.changedIssue.exception.NotFoundPowerByMemberException;
import com.uplog.uplog.domain.changedIssue.model.AccessProperty;
import com.uplog.uplog.domain.comment.exception.MemberAuthorizedException;
import com.uplog.uplog.domain.comment.model.Comment;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.exception.ExistProcessProjectExeption;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.exception.NotFoundMemberByTeamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthorizedMethod {

    private final ChangedIssueRepository changedIssueRepository;
    private final MemberRepository memberRepository;
    private final MemberTeamRepository memberTeamRepository;


    //memberId로 권한 확인->리더인지 마스터인지
    public void powerValidateByMemberId(Long memberId,Team projectRootTeam){

        //PowerType powerType=changedIssueRepository.findMemberPowerTypeByMemberId(memberId);

//        if(powerType==null){
//            throw new NotFoundPowerByMemberException(memberId);
//        }

        if(!memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(memberId, projectRootTeam.getId())){
            throw new AuthorityException("프로젝트에 속하지 않은 멤버로 변경이력 작성 권한이 없습니다.");
        }
        else{
            MemberTeam memberTeam = memberTeamRepository.findMemberTeamByMemberIdAndTeamId(memberId, projectRootTeam.getId()).orElseThrow(NotFoundIdException::new);
            if(memberTeam.getPowerType() == PowerType.CLIENT || memberTeam.getPowerType() == PowerType.DEFAULT){
                throw new AuthorityException("변경이력 작성 권한이 없는 멤버입니다.");
            }
        }


    }
//    //memberId로 권한 확인->클라이언트면 예외처리
//    public void IsNotClientValidateByMemberId(Long memberId ){
//
//        PowerType powerType=changedIssueRepository.findMemberPowerTypeByMemberId(memberId);
//
//        if(powerType==null){
//            throw new NotFoundPowerByMemberException(memberId);
//        }
//
//        if(powerType==PowerType.CLIENT){
//
//            throw new MemberAuthorizedException(memberId);
//        }
//    }



/*
    테스크,포스트,메뉴 생성권한 및 테스크할당자 권한
 */

//- 해당 프로젝트 팀 내에 존재하는 멤버
//- 기업회원이 아닌 개인 회원-
//- 클라이언트가 아닌 멤버
    public void PostTaskValidateByMemberId(Member member, Team projectRootTeam){
        Long memberId=member.getId();

        //기업회원이 아닌 개인 회원(기업이면 예외)
        if(member.getPosition()== Position.COMPANY){
            throw new AuthorityException("기업회원은 권한이 없습니다");
        }

        //현재 프로젝트 팀 내에 존재하는 멤버인지 확인
        //클라이언트가 아닌 멤버(클라이언트면 예외)
        if(!memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(memberId, projectRootTeam.getId())){
            throw new AuthorityException("프로젝트에 속하지 않은 멤버로 권한이 없습니다.");
        }
        else{
            MemberTeam memberTeam = memberTeamRepository.findMemberTeamByMemberIdAndTeamId(memberId, projectRootTeam.getId()).orElseThrow(NotFoundIdException::new);
            if(memberTeam.getPowerType() == PowerType.CLIENT){
                throw new AuthorityException("클라이언트는 해당 권한이 없는 멤버입니다.");
            }
        }

    }

    /*
         댓글 생성 권한 + 좋아요 생성 권한
     */
//해당 프로젝트 팀 내에 존재하는 멤버 ->나중에 윤정이가 줄거임
//기업회원이 아닌 개인 회원
// ->기업은 결과물 메뉴에 있는 포스트글에만 댓글 작성 가능
//클라이언트가 아닌 멤버
// ->클라이언트는 결과물 메뉴에 있는 포스트글에만 댓글 작성 가능
    public void CreateCommentAndLikeValidateByMemberId(String menuName, Member member, Team projectRootTeam){
        Long memberId=member.getId();

        //TODO 현재 프로젝트 팀 내에 존재하는 멤버인지 확인
        if(!memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(memberId, projectRootTeam.getId())){
            throw new AuthorityException("프로젝트에 속하지 않은 멤버입니다");
        }
        //결과물 메뉴가 아닐때
        if(!"결과물".equals(menuName)){
            //기업회원이 아닌 개인 회원(기업이면 예외)
            if(member.getPosition()== Position.COMPANY){
                throw new AuthorityException("기업회원은 권한이 없습니다");
            }

            if(!memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(memberId, projectRootTeam.getId())){
                throw new AuthorityException("프로젝트에 속하지 않은 멤버로 댓글 작성 권한이 없습니다.");
            }
            else{
                MemberTeam memberTeam = memberTeamRepository.findMemberTeamByMemberIdAndTeamId(memberId, projectRootTeam.getId()).orElseThrow(NotFoundIdException::new);
                if(memberTeam.getPowerType() == PowerType.CLIENT){
                    throw new AuthorityException("댓글 작성 권한이 없는 멤버입니다.");
                }
            }
        }


    }







    //projectId로 진행 중 project인지 확인
    public String checkProjectProgress(Long projectId){

        ProjectStatus projectStatus=changedIssueRepository.findProjectStatusByProjectId(projectId);

        if(projectStatus==ProjectStatus.PROGRESS_COMPLETE){
            throw new ExistProcessProjectExeption(projectId,projectStatus);
        }


        return AccessProperty.ACCESS_OK.toString();
    }
}
