package com.task.api_dev_task.service;

import com.task.api_dev_task.dto.TodoDTO;
import com.task.api_dev_task.entity.Todo;
import com.task.api_dev_task.entity.User;
import com.task.api_dev_task.exception.TodoException;
import com.task.api_dev_task.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserService userService;

    public Page<TodoDTO> getUserTodos(String username, int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        User user = userService.getUserByUsername(username);
        
        Page<Todo> todos;
        if (search != null && !search.isEmpty()) {
            todos = todoRepository.findByUserIdAndTitleContainingIgnoreCase(user.getId(), search, pageRequest);
        } else {
            todos = todoRepository.findByUserId(user.getId(), pageRequest);
        }
        
        return todos.map(this::convertToDTO);
    }

    public TodoDTO createTodo(String username, TodoDTO todoDTO) {
        validateTodoDTO(todoDTO);
        
        User user = userService.getUserByUsername(username);
        Todo todo = new Todo();
        updateTodoFromDTO(todo, todoDTO);
        todo.setCompleted(false);
        
        Todo savedTodo = todoRepository.save(todo);
        log.info("Created new todo with id: {} for user: {}", savedTodo.getId(), username);
        return convertToDTO(savedTodo);
    }

    public TodoDTO updateTodo(String username, Long todoId, TodoDTO todoDTO) {
        validateTodoDTO(todoDTO);
        
        User user = userService.getUserByUsername(username);
        Todo todo = todoRepository.findByIdAndUserId(todoId, user.getId())
                .orElseThrow(() -> new TodoException("Todo not found with id: " + todoId + " for user: " + username));

        updateTodoFromDTO(todo, todoDTO);
        Todo updatedTodo = todoRepository.save(todo);
        log.info("Updated todo with id: {} for user: {}", todoId, username);
        return convertToDTO(updatedTodo);
    }

    public TodoDTO toggleTodoCompletion(String username, Long todoId) {
        User user = userService.getUserByUsername(username);
        Todo todo = todoRepository.findByIdAndUserId(todoId, user.getId())
                .orElseThrow(() -> new TodoException("Todo not found with id: " + todoId + " for user: " + username));
        
        todo.setCompleted(!todo.isCompleted());
        Todo updatedTodo = todoRepository.save(todo);
        log.info("Toggled completion status for todo with id: {} for user: {}", todoId, username);
        return convertToDTO(updatedTodo);
    }

    public TodoDTO getTodoById(String username, Long todoId) {
        User user = userService.getUserByUsername(username);
        Todo todo = todoRepository.findByIdAndUserId(todoId, user.getId())
                .orElseThrow(() -> new TodoException("Todo not found with id: " + todoId + " for user: " + username));

        return convertToDTO(todo);
    }

    public void deleteTodo(String username, Long todoId) {
        User user = userService.getUserByUsername(username);
        Todo todo = todoRepository.findByIdAndUserId(todoId, user.getId())
                .orElseThrow(() -> new TodoException("Todo not found with id: " + todoId + " for user: " + username));

        todoRepository.delete(todo);
        log.info("Deleted todo with id: {} for user: {}", todoId, username);
    }

    private void validateTodoDTO(TodoDTO todoDTO) {
        if (todoDTO.getTitle() == null || todoDTO.getTitle().trim().isEmpty()) {
            throw new TodoException("Todo title cannot be empty");
        }
        if (todoDTO.getDueDate() != null && todoDTO.getDueDate().before(new Date())) {
            throw new TodoException("Due date cannot be in the past");
        }
    }

    private void updateTodoFromDTO(Todo todo, TodoDTO todoDTO) {
        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());
        todo.setDueDate(todoDTO.getDueDate());
    }

    private TodoDTO convertToDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        return dto;
    }
}
