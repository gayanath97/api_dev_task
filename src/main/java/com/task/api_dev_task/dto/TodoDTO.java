package com.task.api_dev_task.dto;

import lombok.Data;
import java.util.Date;

@Data
public class TodoDTO {
    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private Date dueDate;
    private String priority;
    private String username;
}
