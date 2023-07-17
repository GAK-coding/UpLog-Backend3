package com.uplog.uplog.domain.scrap.dao;

import com.uplog.uplog.domain.scrap.model.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
}
