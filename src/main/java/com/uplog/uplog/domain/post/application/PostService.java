package com.uplog.uplog.domain.post.application;

import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.dto.PostDTO.*;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.product.dao.ProductRepository;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.model.Project;
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
    public Post createPost(Long id, CreatePostRequest createPostRequest){
        Member author=memberRepository.findMemberById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Menu menu = menuRepository.findById(createPostRequest.getMenuId())
                .orElseThrow(() -> new RuntimeException("Menu not found"));

        Project project = projectRepository.findById(createPostRequest.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Product product = productRepository.findById(createPostRequest.getPorductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Post post=createPostRequest.toEntity(author,menu,product,project);

        postRepository.save(post);

        return post;

    }

}
