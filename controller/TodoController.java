// ========================================================
// TodoController.java
// REST API Controller for Todo Application
// ========================================================

package com.softsyntax.todo.controller;

import com.softsyntax.todo.model.Todo;
import com.softsyntax.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Todo REST Controller
 * Modern Spring Boot REST API for Todo Management
 */
@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // ====================== GET ALL TODOS ======================
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos(
            @RequestParam(required = false) Boolean completed) {
        
        List<Todo> todos = (completed != null) 
                ? todoService.getTodosByStatus(completed) 
                : todoService.getAllTodos();
        
        return ResponseEntity.ok(todos);
    }

    // ====================== GET TODO BY ID ======================
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ====================== CREATE TODO ======================
    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Todo savedTodo = todoService.createTodo(todo);
        return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
    }

    // ====================== UPDATE TODO ======================
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        try {
            Todo updatedTodo = todoService.updateTodo(id, todoDetails);
            return ResponseEntity.ok(updatedTodo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====================== TOGGLE COMPLETION ======================
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Todo> toggleTodoStatus(@PathVariable Long id) {
        try {
            Todo updatedTodo = todoService.toggleTodoStatus(id);
            return ResponseEntity.ok(updatedTodo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====================== DELETE TODO ======================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        try {
            todoService.deleteTodo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====================== DELETE ALL COMPLETED ======================
    @DeleteMapping("/completed")
    public ResponseEntity<Map<String, String>> deleteAllCompleted() {
        int deletedCount = todoService.deleteAllCompleted();
        return ResponseEntity.ok(Map.of(
            "message", "Successfully deleted " + deletedCount + " completed todos"
        ));
    }

    // ====================== STATISTICS ======================
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = Map.of(
            "total", todoService.getTotalCount(),
            "completed", todoService.getCompletedCount(),
            "pending", todoService.getPendingCount(),
            "completionRate", todoService.getCompletionRate()
        );
        return ResponseEntity.ok(stats);
    }
}
