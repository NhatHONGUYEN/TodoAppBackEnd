package com.example.ToDoApp.service;

import com.example.ToDoApp.entity.Task;
import com.example.ToDoApp.exception.AccessDeniedException;
import com.example.ToDoApp.exception.TaskNotFoundException;
import com.example.ToDoApp.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    /**
     * Crée une nouvelle tâche en base.
     *
     * @param t la tâche à créer
     * @return la tâche persistée (avec son ID)
     */
    public Task create(Task t) {
        return repo.save(t);
    }

    /**
     * Liste les tâches d'un utilisateur.
     *
     * @param userId    ID du user (sub du token)
     * @param completed filtre : true/false ou null pour "toutes"
     * @param sort      critère de tri (ex : Sort.by("dueDate"))
     * @return la liste des tâches correspondant aux critères
     */
    public List<Task> list(String userId, Boolean completed, Sort sort) {
        if (completed == null) {
            return repo.findByUserId(userId, sort);
        } else {
            return repo.findByUserIdAndCompleted(userId, completed, sort);
        }
    }

    /**
     * Met à jour une tâche existante si elle appartient à l'utilisateur.
     *
     * @param id      ID de la tâche à modifier
     * @param userId  ID du user (sub du token)
     * @param data    objet Task contenant les nouveaux champs
     * @return la tâche mise à jour
     * @throws TaskNotFoundException si la tâche n'est pas trouvée
     * @throws AccessDeniedException si l'utilisateur n'est pas propriétaire de la tâche
     */
    public Task update(Long id, String userId, Task data) {
        Task existingTask = repo.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
            
        if (!existingTask.getUserId().equals(userId)) {
            throw new AccessDeniedException();
        }

        existingTask.setTitle(data.getTitle());
        existingTask.setDescription(data.getDescription());
        existingTask.setDueDate(data.getDueDate());
        existingTask.setCompleted(data.getCompleted());

        return repo.save(existingTask);
    }

    /**
     * Marque une tâche comme terminée (✓).
     *
     * @param id     ID de la tâche
     * @param userId ID du user (sub du token)
     * @return la tâche mise à jour
     * @throws TaskNotFoundException si la tâche n'est pas trouvée
     * @throws AccessDeniedException si l'utilisateur n'est pas propriétaire de la tâche
     */
    public Task markDone(Long id, String userId) {
        Task updateTask = repo.findById(id)
                     .orElseThrow(() -> new TaskNotFoundException(id));
                     
        if (!updateTask.getUserId().equals(userId)) {
            throw new AccessDeniedException();
        }
        
        updateTask.setCompleted(true);
        return repo.save(updateTask);
    }

    /**
     * Supprime une tâche si elle appartient bien à l'utilisateur.
     *
     * @param id     ID de la tâche
     * @param userId ID du user (sub du token)
     * @throws TaskNotFoundException si la tâche n'est pas trouvée
     * @throws AccessDeniedException si l'utilisateur n'est pas propriétaire de la tâche
     */
    public void delete(Long id, String userId) {
        Task task = repo.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
            
        if (!task.getUserId().equals(userId)) {
            throw new AccessDeniedException();
        }
        
        repo.delete(task);
    }
}
