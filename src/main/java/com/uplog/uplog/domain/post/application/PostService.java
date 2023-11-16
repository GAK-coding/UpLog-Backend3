package com.uplog.uplog.domain.post.application;

import com.uplog.uplog.domain.comment.dao.CommentRepository;
import com.uplog.uplog.domain.like.dao.PostLikeRepository;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.dto.PostDTO.*;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.post.model.PostType;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.tag.application.TagService;
import com.uplog.uplog.domain.tag.dao.PostTagRepository;
import com.uplog.uplog.domain.tag.dao.TagRepository;
import com.uplog.uplog.domain.tag.dto.TagDTO;
import com.uplog.uplog.domain.tag.dto.TagDTO.TagInfoDTO;
import com.uplog.uplog.domain.tag.model.PostTag;
import com.uplog.uplog.domain.tag.model.Tag;
import com.uplog.uplog.domain.task.exception.NotFoundTaskByIdException;
import com.uplog.uplog.domain.team.dao.MemberTeamRepository;
import com.uplog.uplog.domain.team.dao.TeamRepository;
import com.uplog.uplog.domain.team.model.Team;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import com.uplog.uplog.global.method.AuthorizedMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final MemberTeamRepository memberTeamRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProjectRepository projectRepository;
    private final MenuRepository menuRepository;
    private final AuthorizedMethod authorizedMethod;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final TeamRepository teamRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final TagService tagService;
//    private final MenuService menuService;

    /*
    Create
     */
    @Transactional
    public Long createPost(Long id, CreatePostRequest createPostRequest) {
        Member author = memberRepository.findMemberById(id)
                .orElseThrow(() -> new NotFoundIdException("해당 멤버는 존재하지 않습니다."));

        Menu menu = menuRepository.findById(createPostRequest.getMenuId())
                .orElseThrow(() -> new NotFoundIdException("해당 메뉴는 존재하지 않습니다."));

        Project project = projectRepository.findById(createPostRequest.getProjectId())
                .orElseThrow(() -> new NotFoundIdException("해당 프로젝트는 존재하지 않습니다."));
        log.info("가");

        Product product = productRepository.findById(createPostRequest.getProductId())
                .orElseThrow(() -> new NotFoundIdException("해당 제품은 존재하지 않습니다."));
        Team rootTeam = teamRepository.findByProjectIdAndName(project.getId(), project.getVersion()).orElseThrow(NotFoundIdException::new);


        //현재 진행중인 프로젝트가 아니면 예외
        authorizedMethod.checkProjectProgress(project.getId());
        //현재 프로젝트 팀 내에 존재하는 멤버,기업이 아닌 회원,클라이언트가 아닌 멤버 확인


        //TODO 프로젝트팀 넘겨주기
        authorizedMethod.PostTaskValidateByMemberId(author,rootTeam);

//        if(!memberTeamRepository.existsMemberTeamByMemberIdAndTeamId(id, rootTeam.getId())){
//            throw new AuthorityException("프로젝트에 속하지 않은 멤버로 포스트 작성 권한이 없습니다.");
//        }
//        else{
//            MemberTeam memberTeam = memberTeamRepository.findMemberTeamByMemberIdAndTeamId(id, rootTeam.getId()).orElseThrow(NotFoundIdException::new);
//            if(memberTeam.getPowerType() == PowerType.CLIENT){
//                throw new AuthorityException("포스트 작성 권한이 없는 멤버입니다.");
//            }
//        }



        // Post post = createPostRequest.toEntity(author, menu, product, project);
        PostType postType = PostType.DEFAULT; // 기본값으로 설정

        String requestType = createPostRequest.getPostType();
        if (requestType != null) {
            // requestType이 null이 아닐 때만 비교
            if (requestType.equals(PostType.REQUEST_READ.name())) {
                postType = PostType.REQUEST_READ;
            } else if (requestType.equals(PostType.REQUEST_REQUIREMENT.name())) {
                postType = PostType.REQUEST_REQUIREMENT;
            } else if(requestType.equals(PostType.DEFAULT.name())){
                postType=PostType.DEFAULT;
            }
            else {
                throw new IllegalArgumentException("Invalid PostType: " + requestType);
            }
        }


        // 포스트 생성
        Post post = createPostRequest.toEntity(author, menu, product, project, postType);
        postRepository.save(post);
        System.out.println(post.getPostTagList()+"ddddddd");

        List<String> tagContents = createPostRequest.getTagContents(); // 태그 내용 리스트 받아오기
        List<TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성

        for (String tagContent : tagContents) {
            if (!tagRepository.existsByContent(tagContent)) {
                // 존재하지 않는 경우 새로운 태그 생성
                Long tagId = tagService.createTag(post.getId(), TagDTO.CreateTagRequest.builder().content(tagContent).build());
                Tag tag = tagRepository.findById(tagId).orElseThrow(NotFoundIdException::new);
                postTags.add(tag.toTagInfoDTO());
            } else {
                Tag tag = tagRepository.findByContent(tagContent);
                // 이미 존재하는 태그인 경우 포스트태그 생성 및 연결
                //PostTag postTag = new PostTag(post, existingTag);
                //postTagRepository.save(postTag);
                PostTag postTag = PostTag.builder()
                        .post(post)
                        .tag(tag)
                        .build();
                postTagRepository.save(postTag);
                post.getPostTagList().add(postTag);
                postTags.add(tag.toTagInfoDTO());

            }
        }
        return post.toPostInfoDTO(postTags).getId();

    }


    /*
    Delete
     */
    @Transactional
    public String deletePost(Long id,Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundIdException::new);
        System.out.println(post.getId()+"dsafadfasf");

        //해당 게시글이 현재 메뉴의 공지글이라면 그 공지글 리셋해야함
        if (post.getMenu() != null && post.getMenu().getNoticePost() != null) {
            if (post.getMenu().getNoticePost().getId() != null && post.getMenu().getNoticePost().getId().equals(id)) {
                deleteNoticePostInPostService(post.getMenu().getId());
            }
        }

        if(post.getAuthor().getId().equals(currentUserId)){
            postRepository.delete(post);
            return "delete 완료";
        }
        else{
            throw new AuthorityException("작성자와 일치하지 않아 삭제 권한이 없습니다.");
        }
    }

    /*
        update
    */
    //TODO update 권한 설정해야,==으로 바꾸기
    @Transactional
    public Long updatePostTitle(Long id, UpdatePostTitleRequest updatePostTitleRequest, Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        int likeCount = postLikeRepository.countByPostId(post.getId());
        int commentCount = commentRepository.countByPostId(post.getId());

        List<TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성
        for(PostTag pt : post.getPostTagList()){
            postTags.add(pt.getTag().toTagInfoDTO());
        }


        if(post.getAuthor().getId().equals(currentUserId)){
            post.updatePostTitle(updatePostTitleRequest.getUpdateTitle());
            return post.toPostDetailInfoDTO(postTags, likeCount, commentCount).getId();
        }
        else{
            throw new AuthorityException("작성자와 일치하지 않아 수정 권한이 없습니다.");
        }

    }

    @Transactional
    public Long updatePostContent(Long id, UpdatePostContentRequest updatePostContentRequest, Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        int likeCount = postLikeRepository.countByPostId(post.getId());
        int commentCount = commentRepository.countByPostId(post.getId());

        List<TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성
        for(PostTag pt : post.getPostTagList()){
            postTags.add(pt.getTag().toTagInfoDTO());
        }
        if(post.getAuthor().getId().equals(currentUserId)){
            post.updatePostContent(updatePostContentRequest.getUpdateContent());
            return post.toPostDetailInfoDTO(postTags, likeCount, commentCount).getId();
        }
        else{
            throw new AuthorityException("작성자와 일치하지 않아 수정 권한이 없습니다.");
        }

    }

    //TODO Enum수정
    @Transactional
    public Long updatePostType(Long id, UpdatePostTypeRequest updatePostTypeRequest, Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        int likeCount = postLikeRepository.countByPostId(post.getId());
        int commentCount = commentRepository.countByPostId(post.getId());

        List<TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성
        for(PostTag pt : post.getPostTagList()){
            postTags.add(pt.getTag().toTagInfoDTO());
        }
        PostType updatepostType = PostType.DEFAULT; // 기본값으로 설정

        String requestType = updatePostTypeRequest.getUpdatePostType();
        if(post.getAuthor().getId().equals(currentUserId)) {
            if (requestType != null) {
                // requestType이 null이 아닐 때만 비교
                if (requestType.equals(PostType.REQUEST_READ.name())) {
                    updatepostType = PostType.REQUEST_READ;
                } else if (requestType.equals(PostType.REQUEST_REQUIREMENT.name())) {
                    updatepostType = PostType.REQUEST_REQUIREMENT;
                } else if(requestType.equals(PostType.DEFAULT.name())){
                    updatepostType=PostType.DEFAULT;
                } else {
                    throw new IllegalArgumentException("Invalid PostType: " + requestType);
                }
            }
            post.updatePostType(updatepostType);
            return post.toPostDetailInfoDTO(postTags, likeCount, commentCount).getId();
        }
        else{
            throw new AuthorityException("작성자와 일치하지 않아 수정 권한이 없습니다.");
        }

    }

    @Transactional
    public Long updatePostMenu(Long id, UpdatePostMenuRequest updatePostMenuRequest, Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        int likeCount = postLikeRepository.countByPostId(post.getId());
        int commentCount = commentRepository.countByPostId(post.getId());

        List<TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성
        for(PostTag pt : post.getPostTagList()){
            postTags.add(pt.getTag().toTagInfoDTO());
        }
        Menu menu = menuRepository.findById(updatePostMenuRequest.getUpdateMenuId())
                .orElseThrow(() -> new NotFoundIdException("해당 메뉴는 존재하지 않습니다."));

        if(post.getAuthor().getId().equals(currentUserId)){
            //메뉴를 업데이트 하려는데 해당 게시글이 현재 메뉴의 공지글이라면 그 공지글 리셋해야함
            if (post.getMenu().getNoticePost().getId() != null) {
                if (post.getMenu().getNoticePost().getId().equals(id)) {
                    deleteNoticePostInPostService(post.getMenu().getId());
                }
            }
            post.updatePostMenu(menu);
            return post.toPostDetailInfoDTO(postTags,likeCount,commentCount).getId();
        }

        else{
            throw new AuthorityException("작성자와 일치하지 않아 수정 권한이 없습니다.");
        }

    }

    @Transactional
    public Long updatePostInfo(Long id, UpdatePostRequest updatePostRequest, Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        int likeCount = postLikeRepository.countByPostId(post.getId());
        int commentCount = commentRepository.countByPostId(post.getId());

        List<TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성
        for(PostTag pt : post.getPostTagList()){
            postTags.add(pt.getTag().toTagInfoDTO());
        }

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new AuthorityException("작성자와 일치하지 않아 수정 권한이 없습니다.");
        }

        if (updatePostRequest.getUpdatePostTitle() != null) {
            post.updatePostTitle(updatePostRequest.getUpdatePostTitle());
        }

        if (updatePostRequest.getUpdatePostContent() != null) {
            post.updatePostContent(updatePostRequest.getUpdatePostContent());
        }

        if (updatePostRequest.getUpdatePostType() != null) {
            String requestType = updatePostRequest.getUpdatePostType();
            PostType updatedPostType;

            switch (requestType) {
                case "DEFAULT":
                    updatedPostType=PostType.DEFAULT;
                    break;
                case "REQUEST_READ":
                    updatedPostType = PostType.REQUEST_READ;
                    break;
                case "REQUEST_REQUIREMENT":
                    updatedPostType = PostType.REQUEST_REQUIREMENT;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid PostType: " + requestType);
            }

            post.updatePostType(updatedPostType);
        }

        if (updatePostRequest.getUpdateMenuId() != null) {
            Menu menu = menuRepository.findById(updatePostRequest.getUpdateMenuId())
                    .orElseThrow(() -> new NotFoundIdException("해당 메뉴는 존재하지 않습니다."));

            //메뉴를 업데이트 하려는데 해당 게시글이 현재 메뉴의 공지글이라면 그 공지글 리셋해야함
            if (post.getMenu() != null && post.getMenu().getNoticePost() != null) {
                if (post.getMenu().getNoticePost().getId().equals(id)) {
                    deleteNoticePostInPostService(post.getMenu().getId());
                }
            }


            post.updatePostMenu(menu);
        }

        return post.toPostDetailInfoDTO(postTags, likeCount, commentCount).getId();
    }

    //일단 임시
    @Transactional
    public MenuDTO.MenuInfoDTO deleteNoticePostInPostService(Long menuId){
        Menu menu=menuRepository.findById(menuId).orElseThrow(NotFoundIdException::new);

        if (menu.getNoticePost() != null) {
            menu.updateNoticePost(null);
        }
        return menu.toMenuInfoDTO();
    }


    //TODO 이건 나중에 제품 수정할때 같이 불러야하는 서비스
    @Transactional
    public PostDetailInfoDTO updateProductName(Long id, UpdatePostProductRequest updatePostProductRequest, Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        int likeCount = postLikeRepository.countByPostId(post.getId());
        int commentCount = commentRepository.countByPostId(post.getId());

        List<TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성
        for(PostTag pt : post.getPostTagList()){
            postTags.add(pt.getTag().toTagInfoDTO());
        }
        post.updatePostProductName(updatePostProductRequest.getUpdateProductName());
        return post.toPostDetailInfoDTO(postTags, likeCount,commentCount);


    }

    //TODO 이건 나중에 프로젝트 수정할때 같이 불러야하는 서비스임
    @Transactional
    public void updateVersion(Long id, UpdatePostVersionRequest updatePostVersionRequest, Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        post.updatePostVersion(updatePostVersionRequest.getUpdateVersion());
    }

    /*
    Get
     */

    @Transactional(readOnly = true)
    public PostDetailInfoDTO findById(Long Id){
        Post post=postRepository.findById(Id).orElseThrow(NotFoundIdException::new);;
        int likeCount = postLikeRepository.countByPostId(post.getId());
        int commentCount = commentRepository.countByPostId(post.getId());

        List<TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성
        for(PostTag pt : post.getPostTagList()){
            postTags.add(pt.getTag().toTagInfoDTO());
        }
        return post.toPostDetailInfoDTO(postTags, likeCount, commentCount);
    }
    @Transactional(readOnly = true)
    public List<PostDetailInfoDTO> findPostInfoByMenuId(Long menuId){
        List<Post> postList=postRepository.findByMenuId(menuId);

        List<PostDetailInfoDTO> postInfoDTOs=new ArrayList<>();
        for(Post post:postList){
            int likeCount = postLikeRepository.countByPostId(post.getId());
            int commentCount = commentRepository.countByPostId(post.getId());

            List<TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성
            for(PostTag pt : post.getPostTagList()){
                postTags.add(pt.getTag().toTagInfoDTO());
            }
            PostDetailInfoDTO postInfoDTO=post.toPostDetailInfoDTO(postTags, likeCount, commentCount);
            postInfoDTOs.add(postInfoDTO);
        }
        return postInfoDTOs;

    }
    //이건 안쓰는거->메뉴에서 씀
    public List<Post> findPostsByMenuId(Long menuId) {
        List<Post> postList = postRepository.findByMenuId(menuId);
        return postList;
    }

    @Transactional(readOnly = true)
    public Page<Post> findPageByMenuId(Long menuId, Pageable pageable) {
        return postRepository.findByMenuId(menuId, pageable);
    }


}
