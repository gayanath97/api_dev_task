package com.task.api_dev_task.repository;

import com.task.api_dev_task.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findByUserId(Long userId, Pageable pageable);
    Page<Todo> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title, Pageable pageable);
    Optional<Todo> findByIdAndUserId(Long id, Long userId);
}
