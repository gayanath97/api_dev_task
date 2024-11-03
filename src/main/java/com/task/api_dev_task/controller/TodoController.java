package com.task.api_dev_task.controller;

import com.task.api_dev_task.dto.TodoDTO;
import com.task.api_dev_task.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping
    public ResponseEntity<Page<TodoDTO>> getUserTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            Principal principal) {
        
        Page<TodoDTO> todos = todoService.getUserTodos(
            principal.getName(), page, size, sortBy, sortDir, search);
        return ResponseEntity.ok(todos);
    }

    @PostMapping
    public ResponseEntity<TodoDTO> createTodo(@RequestBody TodoDTO todoDTO, Principal principal) {
        TodoDTO createdTodo = todoService.createTodo(principal.getName(), todoDTO);
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long id, Principal principal) {
        log.info("Fetching todo with id: {}", id);
        return ResponseEntity.ok(todoService.getTodoById(principal.getName(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id, @RequestBody TodoDTO todoDTO, Principal principal) {
        log.info("Updating todo with id: {}", id);
        return ResponseEntity.ok(todoService.updateTodo(principal.getName(), id, todoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id, Principal principal) {
        log.info("Deleting todo with id: {}", id);
        todoService.deleteTodo(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoDTO> toggleTodoCompletion(@PathVariable Long id, Principal principal) {
        log.info("Toggling completion status for todo with id: {}", id);
        return ResponseEntity.ok(todoService.toggleTodoCompletion(principal.getName(), id));
    }
}
