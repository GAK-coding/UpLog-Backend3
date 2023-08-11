package com.uplog.uplog.domain.task.dao;

import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.task.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findById(Long id);
    List<Task> findByTaskStatus(TaskStatus taskStatus);
    List<Task> findByMenuId(Long menuId);
    Page<Task> findByMenuId(Long menuId, Pageable pageable);
}
