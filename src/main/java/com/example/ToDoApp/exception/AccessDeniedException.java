package com.example.ToDoApp.exception;

/**
 * Exception levée quand un utilisateur tente d'accéder à une ressource qui ne lui appartient pas
 */
public class AccessDeniedException extends BusinessException {
    
    public AccessDeniedException() {
        super("Vous n'êtes pas autorisé à accéder à cette ressource", "ACCESS_DENIED");
    }
    
    public AccessDeniedException(String message) {
        super(message, "ACCESS_DENIED");
    }
} 