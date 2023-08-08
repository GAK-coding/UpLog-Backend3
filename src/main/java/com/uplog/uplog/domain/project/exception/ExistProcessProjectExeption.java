package com.uplog.uplog.domain.project.exception;

import com.uplog.uplog.domain.project.model.ProjectStatus;

public class ExistProcessProjectExeption extends IllegalArgumentException {

    public ExistProcessProjectExeption(String m){super(m);}
    public ExistProcessProjectExeption(Long ProjectId){super("Id : "+ProjectId+"가 진행 중입니다. 진행을 완료 해주세요 ");}
    public ExistProcessProjectExeption(Long ProjectId, ProjectStatus projectStatus){super("Id : "+ProjectId+"가 " +projectStatus.toString()+" 입니다. ");}
}
