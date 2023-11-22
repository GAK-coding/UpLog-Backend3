package com.uplog.uplog.post;

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
import com.uplog.uplog.domain.post.dto.PostDTO;
import com.uplog.uplog.domain.post.model.PostType;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.dto.ProductDTO;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.team.application.TeamService;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.domain.member.model.Authority;

import com.uplog.uplog.global.exception.NotFoundIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.beans.factory.annotation.Autowired;

import org.assertj.core.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@Transactional
@SpringBootTest
public class PostServiceTest {
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
        return ProjectDTO.CreateProjectRequest.builder()
                .version("프로젝트1")
                .build();
    }
    public ProductDTO.CreateProductRequest createProductRequest(){
        return ProductDTO.CreateProductRequest.builder()
                .name("제품")
                .masterEmail("meufavorr@naver.com")
                .link("asdfgg")
                .build();
    }
    public MenuDTO.CreateMenuRequest createMenuRequest(){
        return MenuDTO.CreateMenuRequest.builder()
                .menuName("menu2")
                .build();
    }
    public PostDTO.CreatePostRequest createpostRequest(){
        return PostDTO.CreatePostRequest.builder()
                .menuId(1L)
                .postType(null)
                .title("제목")
                .content("글")
                .productId(1L)
                .projectId(1L)
                .build();
    }
//    public TeamDTO.CreateTeamRequest createTeamRequest(){
//        return TeamDTO.CreateTeamRequest.builder()
//                .memberIdList()
//    }

    @Test
    @DisplayName("프스트 생성 성공 테스트")
    public void successCreatePost(){
        //Given
        //멤버생성(회사,개인)
        //TODO authority랑 passwordEncoder를 null로 처리하고 해도 될지 모르겠음
        Member company = createCompanyRequest().toMemberEntity(null,null);
        Member member = createMemberRequest().toMemberEntity(null,null);
        memberRepository.save(company);
        memberRepository.save(member);

        //프로덕트정보로 팀생성(마스터만 존재하는 팀)
        TeamDTO.CreateTeamRequest saveTeamRequest = TeamDTO.CreateTeamRequest.builder()
//                .memberIdList()
//                .teamName(createProductRequest().getName())
//                .memberEmail(createProductRequest().getMasterEmail())
                .link(createProductRequest().getLink())
                .build();
//        Team team = saveTeamRequest.toEntity();
//        teamRepository.save(team);

        //프로덕트 생성
        Product product = createProductRequest().toProductEntity(company.getName(), team);
        productRepository.save(product);

        //프로젝트생성
        Project project = createProject().toEntity(product);
        projectRepository.save(project);

        //메뉴 생성
        Menu menu=createMenuRequest().toEntity(project);
        menuRepository.save(menu);

        PostDTO.CreatePostRequest createPostRequest=createpostRequest();

        //WHEN
        Long postInfoDTO=postService.createPost(member.getId(),createPostRequest);

        //THEN
        Assertions.assertThat(postRepository.existsById(postInfoDTO));
    }

    @Test
    @DisplayName("포스트 삭제 성공 테스트")
    public void successDeletePost() {
        // Given
        //Given
        //멤버생성(회사,개인)
        Member company = createCompanyRequest().toMemberEntity(null,null);
        Member member = createMemberRequest().toMemberEntity(null,null);
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

        //메뉴 생성
        Menu menu=createMenuRequest().toEntity(project);
        menuRepository.save(menu);

        //포스트 생성
        PostDTO.CreatePostRequest createPostRequest = createpostRequest();
        PostDTO.PostInfoDTO postInfoDTO = postService.createPost(member.getId(), createPostRequest);

        // WHEN: 포스트 삭제
        postService.deletePost(postInfoDTO.getId());

        // THEN: 포스트가 삭제되었는지 확인
        Assertions.assertThat(postRepository.existsById(postInfoDTO.getId()));
    }

    @Test
    @DisplayName("포스트 제목 업데이트 성공 테스트")
    public void successUpdatePostTitle() {
        //Given
        //멤버생성(회사,개인)
        Member company = createCompanyRequest().toMemberEntity(null,null);
        Member member = createMemberRequest().toMemberEntity(null,null);
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

        //메뉴 생성
        Menu menu=createMenuRequest().toEntity(project);
        menuRepository.save(menu);

        //포스트 생성
        PostDTO.CreatePostRequest createPostRequest = createpostRequest();
        PostDTO.PostInfoDTO postInfoDTO = postService.createPost(member.getId(), createPostRequest);

        String updatedTitle = "수정된 제목";

        //WHEN: 포스트 제목 업데이트
        PostDTO.UpdatePostTitleRequest updatePostTitleRequest = PostDTO.UpdatePostTitleRequest.builder()
                .updateTitle(updatedTitle)
                .build();

        PostDTO.PostInfoDTO updatedPostInfoDTO = postService.updatePostTitle(
                postInfoDTO.getId(), updatePostTitleRequest, member.getId());

        //THEN: 업데이트 된 제목이 기대한 값과 일치하는지 확인
        Assertions.assertThat(updatedPostInfoDTO.getTitle()).isEqualTo(updatedTitle);
    }

    @Test
    @DisplayName("포스트 타입 업데이트 성공 테스트")
    public void successUpdatePostType() {
        //Given
        //멤버생성(회사,개인)
        Member company = createCompanyRequest().toMemberEntity(null,null);
        Member member = createMemberRequest().toMemberEntity(null,null);
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

        //메뉴 생성
        Menu menu=createMenuRequest().toEntity(project);
        menuRepository.save(menu);

        //포스트 생성
        PostDTO.CreatePostRequest createPostRequest = createpostRequest();
        PostDTO.PostInfoDTO postInfoDTO = postService.createPost(member.getId(), createPostRequest);

        PostType updatedPostType = PostType.REQUEST_READ;

        //WHEN: 포스트 유형 업데이트
        PostDTO.UpdatePostTypeRequest updatePostTypeRequest = PostDTO.UpdatePostTypeRequest.builder()
                .updatePostType(String.valueOf(updatedPostType))
                .build();

        PostDTO.PostInfoDTO updatedPostInfoDTO = postService.updatePostType(
                postInfoDTO.getId(), updatePostTypeRequest, member.getId());

        //THEN: 업데이트 된 유형이 기대한 값과 일치하는지 확인
        Assertions.assertThat(updatedPostInfoDTO.getPostType()).isEqualTo(updatedPostType);
    }

    /*
    실패
     */
    @Test
    @DisplayName("포스트 삭제 실패 테스트- 존재하지 않는 포스트")
    public void failDeletePost_NotFound() {
        Long nonExistentPostId = 999L;

        //WHEN, THEN: 존재하지 않는 포스트 삭제 시도 및 예이 발생
        assertThatExceptionOfType(NotFoundIdException.class)
                .isThrownBy(() -> postService.findById(nonExistentPostId));
    }

    @Test
    @DisplayName("포스트 제목 업데이트 실패 테스트-해당 글 작성자가 아닐 경우")
    public void failUpdatePostTitle_Unauthorized() {
        //Given
        //Given
        //멤버생성(회사,개인)
        Member company = createCompanyRequest().toMemberEntity(null,null);
        Member member = createMemberRequest().toMemberEntity(null,null);
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

        //메뉴 생성
        Menu menu=createMenuRequest().toEntity(project);
        menuRepository.save(menu);

        //포스트 생성
        PostDTO.CreatePostRequest createPostRequest = createpostRequest();
        PostDTO.PostInfoDTO postInfoDTO = postService.createPost(member.getId(), createPostRequest);

        String updatedTitle = "수정된 제목";

        // WHEN: 다른 사용자가 포스트 제목 업데이트 시도
        Long anotherUserId = 999L; // 다른 사용자 ID

        PostDTO.UpdatePostTitleRequest updatePostTitleRequest = PostDTO.UpdatePostTitleRequest.builder()
                .updateTitle(updatedTitle)
                .build();

        // THEN: 권한 없어서 예외
        Assertions.assertThatExceptionOfType(AuthorityException.class)
                .isThrownBy(() -> postService.updatePostTitle(postInfoDTO.getId(), updatePostTitleRequest, anotherUserId))
                .withMessage("작성자와 일치하지 않아 수정 권한이 없습니다.");
    }
    //두가지방식 다 가능 위에가 더 간단해서 위에껄로 통일함
//        Assertions.assertThatThrownBy(() -> postService.updatePostTitle(
//                        postInfoDTO.getId(), updatePostTitleRequest, anotherUserId))
//                .isInstanceOf(AuthorityException.class)
//                .hasMessage("작성자와 일치하지 않아 수정 권한이 없습니다.");
//    }

}
