package com.taskflow.model;

import java.time.LocalDate;

public class Task {
    private int id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private TaskStatus status;
    
    // Static counter for generating IDs
    private static int nextId = 1;
    
    public Task(String title, String description) {
        this.id = nextId++;
        this.title = title;
        this.description = description;
        this.priority = Priority.MEDIUM; // This should now resolve correctly
        this.status = TaskStatus.NEW;
        this.dueDate = LocalDate.now().plusDays(7); // Default to one week from now
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return title + " (" + priority + ") - " + status;
    }
}