package com.example.ToDoApp.controller;

import com.example.ToDoApp.entity.Task;
import com.example.ToDoApp.exception.TaskNotFoundException;
import com.example.ToDoApp.service.TaskService;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Classe utilitaire contenant des méthodes communes pour les contrôleurs de tâches
 */
public class TaskControllerUtils {

    private TaskControllerUtils() {
        // Empêche l'instanciation
    }
    
    /**
     * Convertit le statut en texte en valeur booléenne pour le filtre
     */
    public static Boolean getCompletedFilterValue(String status) {
        if ("pending".equalsIgnoreCase(status)) return false;
        if ("done".equalsIgnoreCase(status)) return true;
        return null; // Pour "all" ou toute autre valeur
    }
    
    /**
     * Trouve une tâche par ID ou lance une exception si elle n'existe pas
     * ou n'appartient pas à l'utilisateur spécifié
     */
    public static Task findTaskOrThrow(Long id, String userId, TaskService taskService) {
        List<Task> tasks = taskService.list(userId, null, Sort.by("id"));
        return tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
} 