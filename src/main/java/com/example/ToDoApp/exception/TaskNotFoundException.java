package com.example.ToDoApp.exception;

/**
 * Exception levée quand une tâche n'est pas trouvée
 */
public class TaskNotFoundException extends BusinessException {
    
    public TaskNotFoundException(Long id) {
        super("La tâche avec l'ID " + id + " n'a pas été trouvée", ErrorCode.TASK_NOT_FOUND);
    }
    
    public TaskNotFoundException(String message) {
        super(message, ErrorCode.TASK_NOT_FOUND);
    }
} 