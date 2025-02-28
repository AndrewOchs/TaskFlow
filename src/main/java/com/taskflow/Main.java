package com.taskflow;

import java.time.LocalDate;

import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.service.TaskService;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private TaskService taskService = new TaskService();
    private ListView<Task> taskListView;
    private ObservableList<Task> taskObservableList;
    
    @Override
    public void start(Stage primaryStage) {
        // Create a BorderPane as the root of the scene
        BorderPane root = new BorderPane();
        
        // Create the header
        HBox header = createHeader();
        root.setTop(header);
        
        // Create the task list view for the center
        taskObservableList = FXCollections.observableArrayList();
        taskListView = new ListView<>(taskObservableList);
        taskListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        taskListView.setPrefHeight(400);
        
        // Task details form
        VBox taskDetails = createTaskDetailsForm();
        
        // Create split pane with list and details
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(taskListView, taskDetails);
        splitPane.setDividerPositions(0.4);
        root.setCenter(splitPane);
        
        // Add some sample tasks
        loadSampleTasks();
        
        // Create the scene and show the stage
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TaskFlow - Task Management System");
        primaryStage.show();
    }
    
    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(15, 12, 15, 12));
        header.setStyle("-fx-background-color: #336699;");
        
        Label title = new Label("TaskFlow");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        
        // Add some spacing and push buttons to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Button newTaskBtn = new Button("New Task");
        newTaskBtn.setOnAction(e -> showNewTaskDialog());
        
        header.getChildren().addAll(title, spacer, newTaskBtn);
        return header;
    }
    
    private VBox createTaskDetailsForm() {
        VBox detailsForm = new VBox(10);
        detailsForm.setPadding(new Insets(15));
        
        Label detailsHeader = new Label("Task Details");
        detailsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Add form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 0, 0, 0));
        
        Label titleLabel = new Label("Title:");
        TextField titleField = new TextField();
        titleField.setEditable(false);
        
        Label descLabel = new Label("Description:");
        TextArea descArea = new TextArea();
        descArea.setEditable(false);
        descArea.setPrefRowCount(3);
        
        Label priorityLabel = new Label("Priority:");
        ComboBox<Priority> priorityCombo = new ComboBox<>(
                FXCollections.observableArrayList(Priority.values()));
        priorityCombo.setDisable(true);
        
        Label statusLabel = new Label("Status:");
        ComboBox<TaskStatus> statusCombo = new ComboBox<>(
                FXCollections.observableArrayList(TaskStatus.values()));
        statusCombo.setDisable(true);
        
        Label dueDateLabel = new Label("Due Date:");
        DatePicker datePicker = new DatePicker();
        datePicker.setDisable(true);
        
        grid.add(titleLabel, 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(descLabel, 0, 1);
        grid.add(descArea, 1, 1);
        grid.add(priorityLabel, 0, 2);
        grid.add(priorityCombo, 1, 2);
        grid.add(statusLabel, 0, 3);
        grid.add(statusCombo, 1, 3);
        grid.add(dueDateLabel, 0, 4);
        grid.add(datePicker, 1, 4);
        
        // Buttons for edit and save
        HBox buttonBox = new HBox(10);
        Button editButton = new Button("Edit");
        Button saveButton = new Button("Save");
        saveButton.setDisable(true);
        
        editButton.setOnAction(e -> {
            titleField.setEditable(true);
            descArea.setEditable(true);
            priorityCombo.setDisable(false);
            statusCombo.setDisable(false);
            datePicker.setDisable(false);
            saveButton.setDisable(false);
            editButton.setDisable(true);
        });
        
        saveButton.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                selectedTask.setTitle(titleField.getText());
                selectedTask.setDescription(descArea.getText());
                selectedTask.setPriority(priorityCombo.getValue());
                selectedTask.setStatus(statusCombo.getValue());
                selectedTask.setDueDate(datePicker.getValue());
                
                taskService.updateTask(selectedTask);
                
                // Update the list view
                int selectedIndex = taskListView.getSelectionModel().getSelectedIndex();
                taskObservableList.set(selectedIndex, selectedTask);
                
                // Reset UI state
                titleField.setEditable(false);
                descArea.setEditable(false);
                priorityCombo.setDisable(true);
                statusCombo.setDisable(true);
                datePicker.setDisable(true);
                saveButton.setDisable(true);
                editButton.setDisable(false);
            }
        });
        
        buttonBox.getChildren().addAll(editButton, saveButton);
        
        // Listen for selection changes in the list view
        taskListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                titleField.setText(newVal.getTitle());
                descArea.setText(newVal.getDescription());
                priorityCombo.setValue(newVal.getPriority());
                statusCombo.setValue(newVal.getStatus());
                datePicker.setValue(newVal.getDueDate());
                
                // Reset UI state
                titleField.setEditable(false);
                descArea.setEditable(false);
                priorityCombo.setDisable(true);
                statusCombo.setDisable(true);
                datePicker.setDisable(true);
                saveButton.setDisable(true);
                editButton.setDisable(false);
            }
        });
        
        detailsForm.getChildren().addAll(detailsHeader, grid, buttonBox);
        return detailsForm;
    }
    
    private void showNewTaskDialog() {
        // Create a dialog
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("New Task");
        dialog.setHeaderText("Create a new task");
        
        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setPrefRowCount(3);
        
        ComboBox<Priority> priorityCombo = new ComboBox<>(
                FXCollections.observableArrayList(Priority.values()));
        priorityCombo.setValue(Priority.MEDIUM);
        
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(7));
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Priority:"), 0, 2);
        grid.add(priorityCombo, 1, 2);
        grid.add(new Label("Due Date:"), 0, 3);
        grid.add(datePicker, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the title field by default
        titleField.requestFocus();
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Task task = new Task(titleField.getText(), descriptionArea.getText());
                task.setPriority(priorityCombo.getValue());
                task.setDueDate(datePicker.getValue());
                return task;
            }
            return null;
        });
        
        // Show the dialog and get the result
        dialog.showAndWait().ifPresent(task -> {
            taskService.updateTask(task);
            taskObservableList.add(task);
        });
    }
    
    private void loadSampleTasks() {
        Task task1 = taskService.createTask("Create project proposal", "Draft the initial project proposal document");
        task1.setPriority(Priority.HIGH);
        
        Task task2 = taskService.createTask("Set up development environment", "Install and configure all necessary software");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        
        Task task3 = taskService.createTask("Design database schema", "Create ERD and finalize database structure");
        task3.setDueDate(LocalDate.now().plusDays(3));
        
        Task task4 = taskService.createTask("Research API options", "Evaluate different API frameworks for the project");
        task4.setPriority(Priority.LOW);
        
        taskObservableList.addAll(task1, task2, task3, task4);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}