package com.uplog.uplog.domain.product.dao;

import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.model.MemberTeam;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"team"})
    Optional<Product> findById(Long id);
    List<Product> findProductsByCompany(String company);

    //List<Product> findProductsByCompanyAndOrderBy

}
