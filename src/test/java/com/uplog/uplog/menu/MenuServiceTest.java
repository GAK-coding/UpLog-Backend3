package com.uplog.uplog.menu;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.menu.application.MenuService;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.application.PostService;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductDTO;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.task.application.TaskService;
import com.uplog.uplog.domain.team.application.TeamService;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.model.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import com.uplog.uplog.domain.menu.dto.MenuDTO.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
public class MenuServiceTest {
    @Autowired
    MenuService menuService;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeamService teamService;

    @Autowired
    PostService postService;
    @Autowired
    TaskService taskService;

    public MemberDTO.CreateMemberRequest createMemberRequest(){
        return MemberDTO.CreateMemberRequest.builder()
                .email("meufavorr@naver.com")
                .name("바글령")
                .nickname("글령")
                .password("12345678")
                .position(Position.INDIVIDUAL)
                .build();
    }
    public MemberDTO.CreateMemberRequest createCompanyRequest(){
        return MemberDTO.CreateMemberRequest.builder()
                .email("company@naver.com")
                .name("회사")
                .nickname("글령")
                .password("12345678")
                .position(Position.COMPANY)
                .build();
    }

    public ProjectDTO.CreateInitInfo createProject(){
//        Product product=new Product();
        return ProjectDTO.CreateInitInfo.builder()
                .id(1L)
                .version("프로젝트1")
                .build();
    }

//
//    public MenuData menuData(){
//        return MenuData.builder()
//                .menuName("menu1")
//                .project(new Project())
//                .build();
//
//    }

    public CreateMenuRequest createMenuRequest(){
        return CreateMenuRequest.builder()
                .menuName("menu2")
                .build();

    }
    public ProductDTO.CreateProductRequest createProductRequest(){
        return ProductDTO.CreateProductRequest.builder()
                .name("제품")
                .masterEmail("meufavorr@naver.com")
                .link("asdfgg")
                .build();
    }
    @Test
    @DisplayName("menu create 성공 테스트")
    public void successCreateMenu() {
        // Given
        //멤버생성(회사,개인)
        Member company = createCompanyRequest().toMemberEntity();
        Member member = createMemberRequest().toMemberEntity();
        memberRepository.save(company);
        memberRepository.save(member);

        //프로덕트정보로 팀생성(마스터만 존재하는 팀)
        TeamDTO.CreateTeamRequest saveTeamRequest = TeamDTO.CreateTeamRequest.builder()
                .teamName(createProductRequest().getName())
                .memberEmail(createProductRequest().getMasterEmail())
                .link(createProductRequest().getLink())
                .build();
        Team team = saveTeamRequest.toEntity();
        teamRepository.save(team);

        //프로덕트 생성
        Product product = createProductRequest().toProductEntity(company.getName(), team);
        productRepository.save(product);

        //프로젝트생성
        Project project = createProject().toEntity(ProjectStatus.PROGRESS_IN, product);
        projectRepository.save(project);

        CreateMenuRequest createMenuRequest = createMenuRequest();

        // When
        MenuInfoDTO menuInfoDTO = menuService.createMenu(project.getId(), createMenuRequest).toMenuInfoDTO();

        // Then
        Assertions.assertThat(menuRepository.existsById(menuInfoDTO.getId())).isEqualTo(true);

    }

}