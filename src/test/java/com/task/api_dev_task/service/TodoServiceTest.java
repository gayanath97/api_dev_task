package com.task.api_dev_task.service;

import com.task.api_dev_task.dto.TodoDTO;
import com.task.api_dev_task.entity.Todo;
import com.task.api_dev_task.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo sampleTodo;

    @BeforeEach
    void setUp() {
        sampleTodo = new Todo();
        sampleTodo.setId(1L);
        sampleTodo.setTitle("Test Todo");
        sampleTodo.setCompleted(false);
    }

    @Test
    void getAllTodos_ShouldReturnListOfTodos() {
        // Arrange
        Todo secondTodo = new Todo();
        secondTodo.setId(2L);
        secondTodo.setTitle("Second Todo");
        Page<Todo> todoPage = new PageImpl<>(Arrays.asList(sampleTodo, secondTodo));
        when(todoRepository.findAll(any(PageRequest.class))).thenReturn(todoPage);

        // Act
        Page<TodoDTO> result = todoService.getUserTodos(0, 10, "dueDate", "desc", null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(sampleTodo.getTitle(), result.getContent().get(0).getTitle());
        assertEquals(secondTodo.getTitle(), result.getContent().get(1).getTitle());
    }

    @Test
    void createTodo_ShouldReturnCreatedTodo() {
        // Arrange
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        // Act
        Todo newTodo = new Todo();
        newTodo.setTitle("Test Todo");
        Todo result = todoService.createTodo(newTodo);

        // Assert
        assertNotNull(result);
        assertEquals(sampleTodo.getTitle(), result.getTitle());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_WhenTodoExists_ShouldReturnUpdatedTodo() {
        // Arrange
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        // Act
        Todo updatedTodo = new Todo();
        updatedTodo.setTitle("Updated Title");
        updatedTodo.setCompleted(true);
        Todo result = todoService.updateTodo(1L, updatedTodo);

        // Assert
        assertNotNull(result);
        assertEquals(updatedTodo.getTitle(), result.getTitle());
        assertEquals(updatedTodo.isCompleted(), result.isCompleted());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void deleteTodo_WhenTodoExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        doNothing().when(todoRepository).deleteById(1L);

        // Act & Assert
        assertDoesNotThrow(() -> todoService.deleteTodo(1L));
        verify(todoRepository, times(1)).deleteById(1L);
    }

    @Test
    void getTodoById_WhenTodoDoesNotExist_ShouldThrowException() {
        // Arrange
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> todoService.getTodoById(999L));
    }
} 
