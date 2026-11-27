package cosmo.ui.parts;

import cosmo.ui.TaskActions;
import cosmo.ui.TaskOptions;
import cosmo.ui.TaskView;
import cosmo.ui.UiUtil;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;


public class ScheduleRowController {

    @FXML private HBox root;
    @FXML private Label timeLabel;
    @FXML private ToggleButton doneToggle;
    @FXML private Label titleLabel;
    @FXML private Label priorityLabel;
    @FXML private Label dueLabel;
    @FXML private Label categoryPill;
    @FXML private ComboBox<String> statusBox;

    private TaskView task;
    private TaskActions actions;
    private boolean filling;


    public static ScheduleRowController create(TaskView task, boolean overdueSection,
                                               TaskActions actions, Consumer<ScheduleRowController> onClick) {
        try {
            FXMLLoader loader = new FXMLLoader(ScheduleRowController.class.getResource("/cosmo/fxml/ScheduleRow.fxml"));
            loader.load();
            ScheduleRowController row = loader.getController();
            row.fill(task, overdueSection, actions, onClick);
            return row;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load ScheduleRow.fxml", e);
        }
    }

    public Node getNode() {
        return root;
    }

    public TaskView getTask() {
        return task;
    }

    /** Highlights the row while its details are open. */
    public void setSelected(boolean selected) {
        UiUtil.toggleClass(root, "selected", selected);
    }

    private void fill(TaskView task, boolean overdueSection, TaskActions actions, Consumer<ScheduleRowController> onClick) {
        this.task = task;
        this.actions = actions;


        timeLabel.setText(overdueSection
                ? TaskView.formatDate(task.startDate())
                : TaskView.formatTime(task.startTime()));
        titleLabel.setText(task.title());
        UiUtil.toggleClass(titleLabel, "done-text", task.isCompleted());
        doneToggle.setSelected(task.isCompleted());

        priorityLabel.setText("⚑ " + task.priority());
        UiUtil.replaceClass(priorityLabel, "priority-", UiUtil.priorityClass(task.priority()));

        dueLabel.setText("Due " + TaskView.formatDate(task.dueDate())
                + ", " + TaskView.formatTime(task.dueTime()));
        UiUtil.toggleClass(dueLabel, "due-late", overdueSection);

        categoryPill.setText(task.category());
        UiUtil.replaceClass(categoryPill, "pill-", UiUtil.categoryClass(task.category()));

        filling = true;
        statusBox.getItems().setAll(TaskOptions.STATUSES);
        statusBox.setValue(task.status());
        filling = false;
        UiUtil.replaceClass(statusBox, "status-", UiUtil.statusClass(task.status()));

        root.setOnMouseClicked(event -> onClick.accept(this));
    }

    @FXML
    private void onToggleDone() {
        if (task != null) {
            actions.toggleCompleted(task);
        }
    }

    @FXML
    private void onStatusPicked() {
        // onAction also fires when the code calls setValue(), so ignore that case.
        if (task != null && !filling && !statusBox.getValue().equals(task.status())) {
            actions.changeStatus(task, statusBox.getValue());
        }
    }
}
