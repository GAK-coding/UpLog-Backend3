package com.uplog.uplog.global.method;

import com.uplog.uplog.domain.changedIssue.dao.ChangedIssueRepository;
import com.uplog.uplog.domain.changedIssue.exception.NotFoundPowerByMemberException;
import com.uplog.uplog.domain.changedIssue.model.AccessProperty;
import com.uplog.uplog.domain.comment.exception.MemberAuthorizedException;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.exception.ExistProcessProjectExeption;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthorizedMethod {

    private final ChangedIssueRepository changedIssueRepository;


    //memberId로 권한 확인
    public PowerType powerValidateByMemberId(Long memberId ){

        PowerType powerType=changedIssueRepository.findMemberPowerTypeByMemberId(memberId);

        if(powerType==null){
            throw new NotFoundPowerByMemberException(memberId);
        }

        if(powerType==PowerType.DEFAULT || powerType==PowerType.CLIENT){

            throw new MemberAuthorizedException(memberId);
        }

        return powerType;

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
