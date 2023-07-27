package com.uplog.uplog.domain.post.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.dto.PostDTO.*;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.post.model.PostType;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.task.exception.handler.NotFoundTaskByIdException;
import com.uplog.uplog.global.exception.AuthorityException;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProjectRepository projectRepository;
    private final MenuRepository menuRepository;

    //  ================================CREATE=================================
    @Transactional
    public Post createPost(Long id, CreatePostRequest createPostRequest) {
        Member author = memberRepository.findMemberById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Menu menu = menuRepository.findById(createPostRequest.getMenuId())
                .orElseThrow(() -> new RuntimeException("Menu not found"));

        Project project = projectRepository.findById(createPostRequest.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Product product = productRepository.findById(createPostRequest.getPorductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));


        Post post = createPostRequest.toEntity(author, menu, product, project);

        postRepository.save(post);

        return post;

    }


    //  ================================DELETE=================================
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundIdException::new);
        postRepository.delete(post);
    }

//  ================================UPDATE=================================
//TODO update 권한 설정해야
    @Transactional
    public Post updatePostTitle(Long id, UpdatePostTitleRequest updatePostTitleRequest,Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
          if(post.getAuthor().getId().equals(currentUserId)){
              post.updatePostTitle(updatePostTitleRequest.getUpdateTitle());
              return post;
        }
        else{
            throw new AuthorityException();
        }

    }

    @Transactional
    public Post updatePostContent(Long id, UpdatePostContentRequest updatePostContentRequest,Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        if(post.getAuthor().getId().equals(currentUserId)){
            post.updatePostContent(updatePostContentRequest.getUpdateContent());
            return post;
        }
        else{
            throw new AuthorityException();
        }

    }

    //TODO Enum수정
    @Transactional
    public Post updatePostType(Long id, UpdatePostTypeRequest updatePostTypeRequest,Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        if(post.getAuthor().getId().equals(currentUserId)){
            post.updatePostContent(updatePostTypeRequest.getUpdatePostType());
            return post;
        }
        else{
            throw new AuthorityException();
        }

    }

    @Transactional
    public Post updatePostMenu(Long id, UpdatePostMenuRequest updatePostMenuRequest,Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        Menu menu = menuRepository.findById(updatePostMenuRequest.getUpdateMenuId())
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        if(post.getAuthor().getId().equals(currentUserId)){
            post.updatePostMenu(menu);
            return post;
        }
        else{
            throw new AuthorityException();
        }

    }


    //TODO 이건 나중에 제품 수정할때 같이 불러야하는 서비스임
    @Transactional
    public Post updateProductName(Long id, UpdatePostProductRequest updatePostProductRequest,Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        if(post.getAuthor().getId().equals(currentUserId)){
            post.updatePostProductName(updatePostProductRequest.getUpdateProductName());
            return post;
        }
        else{
            throw new AuthorityException();
        }

    }

    //TODO 이건 나중에 프로젝트 수정할때 같이 불러야하는 서비스임

    @Transactional
    public Post updateVersion(Long id, UpdatePostVersionRequest updatePostVersionRequest,Long currentUserId) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundTaskByIdException::new);
        if(post.getAuthor().getId().equals(currentUserId)){
            post.updatePostVersion(updatePostVersionRequest.getUpdateVersion());
            return post;
        }
        else{
            throw new AuthorityException();
        }

    }




}
