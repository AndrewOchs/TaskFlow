package com.taskflow.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;

public class TaskService {
    private List<Task> tasks = new ArrayList<>();
    
    public Task createTask(String title, String description) {
        Task task = new Task(title, description);
        tasks.add(task);
        return task;
    }
    
    public Task updateTask(Task task) {
        // Find the task with the same ID and replace it
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                return task;
            }
        }
        return null; // Task not found
    }
    
    public boolean deleteTask(int id) {
        return tasks.removeIf(task -> task.getId() == id);
    }
    
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks); // Return a copy to prevent modification
    }
    
    public List<Task> getTasksByStatus(TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }
}