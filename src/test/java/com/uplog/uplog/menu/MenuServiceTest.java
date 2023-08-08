package com.uplog.uplog.menu;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.menu.application.MenuService;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.menu.exception.*;
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
import com.uplog.uplog.global.exception.NotFoundIdException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import com.uplog.uplog.domain.menu.dto.MenuDTO.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    public ProjectDTO.CreateProjectRequest createProject(){
//        Product product=new Product();
        return ProjectDTO.CreateProjectRequest.builder()
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
        Project project = createProject().toEntity(product);
        projectRepository.save(project);

        CreateMenuRequest createMenuRequest = createMenuRequest();

        // When
        MenuInfoDTO menuInfoDTO = menuService.createMenu(project.getId(), createMenuRequest);

        // Then
        Assertions.assertThat(menuRepository.existsById(menuInfoDTO.getId())).isEqualTo(true);

    }

    @Test
    @DisplayName("중복된 메뉴 생성 시도-실패테스트")
    public void testCreateDuplicateMenu() {
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
        Project project = createProject().toEntity(product);
        projectRepository.save(project);

        CreateMenuRequest createMenuRequest1 = createMenuRequest();
        menuService.createMenu(project.getId(),createMenuRequest1);

        // 중복된 메뉴 이름
        String duplicateMenuName = "menu2";
        CreateMenuRequest createMenuRequest2 = CreateMenuRequest.builder()
                .menuName(duplicateMenuName)
                .build();

        //when & then
        org.junit.jupiter.api.Assertions.assertThrows(DuplicatedMenuNameInProjectException.class, () -> {
                    MenuDTO.MenuInfoDTO menuInfoDTO = menuService.createMenu(project.getId(),createMenuRequest2);
                }
        );
    }

    @Test
    @DisplayName("메뉴 생성 제한 수 초과 시도-실패테스트")
    public void testCreateExceededMaxMenuCount() {
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
        Project project = createProject().toEntity(product);
        projectRepository.save(project);

        //15개 생성
        int numberOfMenusToCreate = 15;
        for (int i = 1; i <= numberOfMenusToCreate; i++) {
            String menuName = "menu" + i;
            CreateMenuRequest createMenuRequest2 = CreateMenuRequest.builder()
                    .menuName(menuName)
                    .build();
            menuService.createMenu(project.getId(), createMenuRequest2);
        }

        // 새로운 메뉴 이름
        String newMenuName = "menu16";
        CreateMenuRequest createMenuRequest2 = CreateMenuRequest.builder()
                .menuName(newMenuName)
                .build();

        //when & then
        org.junit.jupiter.api.Assertions.assertThrows(ExceededMaxMenuCountException.class, () -> {
                    MenuDTO.MenuInfoDTO menuInfoDTO = menuService.createMenu(project.getId(),createMenuRequest2);
                }
        );
    }

    @Test
    @DisplayName("프로젝트에 속하지 않은 메뉴 정보 조회 시도-실패테스트")
    public void testFindMenuByIdNotBelongsToProject() {
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
        Project project = createProject().toEntity(product);
        projectRepository.save(project);

        CreateMenuRequest createMenuRequest1 = createMenuRequest();
        menuService.createMenu(project.getId(),createMenuRequest1);

        // 존재하지 않는 메뉴 ID
        Long nonExistentMenuId = 999L;

        // When & Then
        assertThatExceptionOfType(NotFoundIdException.class)
                .isThrownBy(() -> menuService.findMenuById(nonExistentMenuId));
    }

    @Test
    @DisplayName("메뉴 이름 변경 성공")
    public void testUpdateMenuNameSuccess() {
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
        Project project = createProject().toEntity(product);
        projectRepository.save(project);

        CreateMenuRequest createMenuRequest1 = createMenuRequest();
        MenuInfoDTO menuInfoDTO=menuService.createMenu(project.getId(),createMenuRequest1);

        // 변경할 메뉴 ID
        Long menuIdToUpdate = menuInfoDTO.getId();
        String updatedMenuName = "Updated Menu";

        UpdateMenuNameRequest updateMenuNameRequest = UpdateMenuNameRequest.builder()
                .updatemenuName(updatedMenuName)
                .build();

        // When
        MenuInfoDTO updatedMenuInfoDTO = menuService.updateMenuName(menuIdToUpdate, updateMenuNameRequest);

        // Then
        assertThat(updatedMenuInfoDTO.getMenuName()).isEqualTo(updatedMenuName);
    }

    @Test
    @DisplayName("결과물 메뉴 이름 변경 시도-실패테스트")
    public void testUpdateMenuNameNotAllowedForResultsMenu() {
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
        Project project = createProject().toEntity(product);
        projectRepository.save(project);

        // 변경할 메뉴 ID (결과물 메뉴)
        String duplicateMenuName = "결과물";
        CreateMenuRequest createMenuRequest2 = CreateMenuRequest.builder()
                .menuName(duplicateMenuName)
                .build();
        MenuInfoDTO menuInfoDTO=menuService.createMenu(project.getId(),createMenuRequest2);

        Long resultsMenuId = menuInfoDTO.getId();
        String updatedMenuName = "Updated Menu";

        UpdateMenuNameRequest updateMenuNameRequest = UpdateMenuNameRequest.builder()
                .updatemenuName(updatedMenuName)
                .build();

        // When & Then
        assertThatExceptionOfType(MenuUpdateNotAllowedException.class)
                .isThrownBy(() -> menuService.updateMenuName(resultsMenuId, updateMenuNameRequest));
    }

    @Test
    @DisplayName("메뉴 삭제 성공")
    public void testDeleteMenuSuccess() {
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
        Project project = createProject().toEntity(product);
        projectRepository.save(project);

        CreateMenuRequest createMenuRequest1 = createMenuRequest();
        MenuInfoDTO menuInfoDTO=menuService.createMenu(project.getId(),createMenuRequest1);

        // 삭제할 메뉴 ID
        Long menuIdToDelete = menuInfoDTO.getId();

        // When
        menuService.deleteMenu(menuIdToDelete);

        // Then
        assertThat(menuRepository.existsById(menuIdToDelete)).isFalse();
    }

    }