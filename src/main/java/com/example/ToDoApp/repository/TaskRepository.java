package com.example.ToDoApp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ToDoApp.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
  // toutes les tâches d'un user, triées
  List<Task> findByUserId(String userId, Sort sort);

  // tâches d'un user filtrées par état (terminées ou non), triées
  List<Task> findByUserIdAndCompleted(String userId, Boolean completed, Sort sort);
  
  // Méthodes avec pagination
  Page<Task> findByUserId(String userId, Pageable pageable);
  
  Page<Task> findByUserIdAndCompleted(String userId, Boolean completed, Pageable pageable);
}