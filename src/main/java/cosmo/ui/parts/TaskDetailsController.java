package cosmo.ui.parts;

import cosmo.ui.TaskActions;
import cosmo.ui.TaskView;
import cosmo.ui.UiUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


public class TaskDetailsController {

    @FXML private VBox panel;
    @FXML private Label titleLabel;
    @FXML private Label categoryPill;
    @FXML private Label statusPill;
    @FXML private Label priorityLabel;
    @FXML private Label noteLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label startsLabel;
    @FXML private Label dueLabel;
    @FXML private Button completeButton;

    private TaskView task;
    private TaskActions actions;
    private Runnable onClosed = () -> { };

    public void setActions(TaskActions actions) {
        this.actions = actions;
    }

    public void setOnClosed(Runnable onClosed) {
        this.onClosed = onClosed;
    }

    public void show(TaskView task) {
        this.task = task;

        titleLabel.setText(task.title());
        categoryPill.setText(task.category());
        statusPill.setText(task.status());
        priorityLabel.setText("⚑ " + task.priority());
        descriptionLabel.setText(task.description().isBlank() ? "No description." : task.description());
        startsLabel.setText(task.startText());
        dueLabel.setText(task.dueText());
        completeButton.setText(task.isCompleted() ? "Reopen" : "Mark complete");

        UiUtil.replaceClass(categoryPill, "pill-", UiUtil.categoryClass(task.category()));
        UiUtil.replaceClass(statusPill, "status-", UiUtil.statusClass(task.status()));
        UiUtil.replaceClass(priorityLabel, "priority-", UiUtil.priorityClass(task.priority()));

        // the little  Overdue / ● Due today note we preferred these "●" so no need to look for some icons
        boolean overdue = task.highlight() == TaskView.Highlight.OVERDUE;
        boolean today = task.highlight() == TaskView.Highlight.TODAY;
        noteLabel.setText(overdue ? "● Overdue" : "● Due today");
        UiUtil.toggleClass(noteLabel, "note-overdue", overdue);
        UiUtil.toggleClass(noteLabel, "note-today", today);
        UiUtil.show(noteLabel, overdue || today);

        UiUtil.show(panel, true);
    }

    public void hide() {
        task = null;
        UiUtil.show(panel, false);
    }

    @FXML
    private void onClose() {
        hide();
        onClosed.run();
    }

    @FXML private void onToggleCompleted() { if (task != null) actions.toggleCompleted(task); }
    @FXML private void onEdit()            { if (task != null) actions.editTask(task); }
    @FXML private void onDelete()          { if (task != null) actions.deleteTask(task); }
}
