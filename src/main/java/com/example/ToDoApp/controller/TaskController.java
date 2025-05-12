package com.example.ToDoApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @GetMapping
    public ResponseEntity<List<String>> all() {
        // retourne une liste fixe juste pour tester que le JWT est accepté
        return ResponseEntity.ok(List.of("Tâche A", "Tâche B"));
    }
}