package com.uplog.uplog.task;

import com.uplog.uplog.domain.member.application.MemberService;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.menu.application.MenuService;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.task.application.TaskService;
import com.uplog.uplog.domain.task.dao.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TaskServiceTest {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskService taskService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    MenuService menuService;


    @BeforeEach
    public void deleteAll(){
        taskRepository.deleteAll();
    }


}
