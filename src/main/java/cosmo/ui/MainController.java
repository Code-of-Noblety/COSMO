package cosmo.ui;

import cosmo.ui.pages.AllTasksController;
import cosmo.ui.pages.DashboardController;
import cosmo.ui.parts.SidebarController;
import cosmo.ui.parts.SidebarController.Page;
import cosmo.ui.popups.ConfirmDeleteController;
import cosmo.ui.popups.TaskFormController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;



public class MainController implements TaskActions {

    // The pages (the <fx:include> elements in App.fxml) and their controllers.
    @FXML private Node dashboard;
    @FXML private Node allTasks;
    @FXML private SidebarController sidebarController;
    @FXML private DashboardController dashboardController;
    @FXML private AllTasksController allTasksController;
    @FXML private TaskFormController taskFormController;
    @FXML private ConfirmDeleteController confirmDeleteController;


    @FXML
    private void initialize() {
        sidebarController.setOnPageChange(this::showPage);
        dashboardController.setActions(this);
        allTasksController.setActions(this);

        showPage(Page.DASHBOARD);
        refreshAll();
    }

    private void showPage(Page page) {
        UiUtil.show(dashboard, page == Page.DASHBOARD);
        UiUtil.show(allTasks, page == Page.ALL_TASKS);
    }


    // REFRESH: call this after EVERY change (add, edit, delete, status change)




private void refreshAll() {
    LocalDateTime now = LocalDateTime.now();

    var overdueTasks = DemoData.tasks.stream()
            .filter(t -> !t.isCompleted())
            .filter(t -> LocalDateTime.of(t.dueDate(), t.dueTime()).isBefore(now))
            .toList();

    var todayTasks = DemoData.tasks.stream()
            .filter(t -> !t.isCompleted())
            .filter(t -> t.dueDate().equals(LocalDate.now()))
            .filter(t -> !LocalDateTime.of(t.dueDate(), t.dueTime()).isBefore(now))
            .toList();

    int completed = (int) DemoData.tasks.stream()
            .filter(TaskView::isCompleted)
            .count();

    int pendingHigh = (int) DemoData.tasks.stream()
            .filter(t -> t.status().equals("Pending")
                    && t.priority().equals("High"))
            .count();

    allTasksController.setTasks(DemoData.tasks);

    dashboardController.showStats(
            DemoData.tasks.size(),
            completed,
            pendingHigh
    );

    sidebarController.setOverdueCount(overdueTasks.size());

    dashboardController.showSchedule(overdueTasks, todayTasks);
}


    // TaskActions: what the screens ask for. Fill in the TODOs.

    @Override
    public void newTask() {
        taskFormController.openForNew(input -> saveTask(null, input));
    }

    @Override
    public void editTask(TaskView task) {
        taskFormController.openForEdit(task, input -> saveTask(task, input));
    }

    @Override
    public void deleteTask(TaskView task) {
        confirmDeleteController.open(task.title(), () -> {
            DemoData.tasks.remove(task);
            refreshAll();
        });
    }

    @Override
    public void toggleCompleted(TaskView task) {
        String newStatus = task.isCompleted() ? "Pending" : "Completed";
        changeStatus(task, newStatus);
//        refreshAll();
    }

    @Override
    public void changeStatus(TaskView task, String newStatus) {

        TaskView updatedTask = new TaskView(
                task.title(),
                task.description(),
                task.category(),
                task.priority(),
                newStatus,
                task.startDate(),
                task.startTime(),
                task.dueDate(),
                task.dueTime(),
                task.highlight()
        );

        int index = DemoData.tasks.indexOf(task);

        if (index != -1) {
            DemoData.tasks.set(index, updatedTask);
        }

        refreshAll();
    }

    private void saveTask(TaskView existing, TaskInput input) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);



        LocalTime start;
        LocalTime due;

        try {
            start = LocalTime.parse(input.startTime(), formatter);
            due = LocalTime.parse(input.dueTime(), formatter);

        } catch (DateTimeParseException e) {
            taskFormController.showError(
                    "Invalid time. Please enter a valid time, such as 9:30 AM."
            );
            return;
        }
        if (input.startDate() == null || input.dueDate() == null) {
            taskFormController.showError(
                    "Please select both start and due dates."
            );
            return;
        }

        LocalDateTime startDateTime =
                LocalDateTime.of(input.startDate(), start);

        LocalDateTime dueDateTime =
                LocalDateTime.of(input.dueDate(), due);

        TaskView.Highlight highlight = TaskView.Highlight.NONE;

        if (!input.status().equals("Completed")) {
            if (dueDateTime.isBefore(LocalDateTime.now())) {
                highlight = TaskView.Highlight.OVERDUE;
            } else if (input.dueDate().equals(LocalDate.now())) {
                highlight = TaskView.Highlight.TODAY;
            }
        }

        if (dueDateTime.isBefore(startDateTime)) {
            taskFormController.showError(
                    "Due date and time cannot be before the start."
            );
            return;
        }

        TaskView task = new TaskView(
                input.title(),
                input.description(),
                input.category(),
                input.priority(),
                input.status(),
                input.startDate(),
                start,
                input.dueDate(),
                due,
                highlight
        );


        if (existing == null) {
            DemoData.tasks.add(task);
        } else {
            int index = DemoData.tasks.indexOf(existing);

            if (index != -1) {
                DemoData.tasks.set(index, task);
            }
        }


        taskFormController.close();
        refreshAll();
    }

}
