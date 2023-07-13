package com.uplog.uplog.domain.group.dao;

import com.uplog.uplog.domain.group.model.PowerGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<PowerGroup, Long> {
}
