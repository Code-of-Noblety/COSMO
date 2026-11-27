package cosmo.ui.pages;

import cosmo.ui.TaskActions;
import cosmo.ui.TaskView;
import cosmo.ui.UiUtil;
import cosmo.ui.parts.ScheduleRowController;
import cosmo.ui.parts.TaskDetailsController;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 * Dashboard.fxml: the date, the three stat cards and Today's Schedule.
 *
 * This page only SHOWS what it is given (showStats, showSchedule).
 * Deciding WHICH tasks are overdue or due today is your model code, called from MainController.
 */
public class DashboardController {

    @FXML private Label dateLabel;
    @FXML private Label totalValue;
    @FXML private Label completedValue;
    @FXML private Label completedCount;
    @FXML private ProgressBar completedBar;
    @FXML private Label highValue;
    @FXML private Label nowLabel;
    @FXML private VBox scheduleList;
    @FXML private TaskDetailsController taskDetailsController;

    private TaskActions actions;
    private ScheduleRowController selectedRow;

    @FXML
    private void initialize() {
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH)));
        nowLabel.setText("Now: " + LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)));
        taskDetailsController.setOnClosed(this::clearSelection);
    }



    public void setActions(TaskActions actions) {
        this.actions = actions;
        taskDetailsController.setActions(actions);
    }



    /** The three stat cards. */
    public void showStats(int total, int completed, int pendingHighPriority) {
        int percent = total == 0 ? 0 : Math.round(completed * 100f / total);
        totalValue.setText(String.valueOf(total));
        completedValue.setText(percent + "%");
        completedCount.setText(completed + " of " + total);
        completedBar.setProgress(percent / 100.0);
        highValue.setText(String.valueOf(pendingHighPriority));
    }


    public void showSchedule(List<TaskView> overdue, List<TaskView> today) {
        taskDetailsController.hide();
        selectedRow = null;
        scheduleList.getChildren().clear();

        if (!overdue.isEmpty()) {
            addSection("OVERDUE  " + overdue.size(), true);
            overdue.forEach(task -> addRow(task, true));
        }
       
        if (today.isEmpty()) {
           Label emptyLabel = new Label("No tasks due today. What a beast!");
           emptyLabel.getStyleClass().add("empty-state");
           emptyLabel.setMaxWidth(Double.MAX_VALUE);
           scheduleList.getChildren().add(emptyLabel);
       } else {
           addSection("TODAY  " + today.size(), false);
           today.forEach(task -> addRow(task, false));
       }
    }



    private void addSection(String text, boolean overdue) {
        Label label = new Label(text);
        label.getStyleClass().add("section-label");
        UiUtil.toggleClass(label, "section-overdue", overdue);
        label.setMaxWidth(Double.MAX_VALUE);
        scheduleList.getChildren().add(label);
    }

    private void addRow(TaskView task, boolean overdueSection) {
        ScheduleRowController row = ScheduleRowController.create(task, overdueSection, actions, this::select);
        scheduleList.getChildren().add(row.getNode());
    }



    private void select(ScheduleRowController row) {
        clearSelection();
        selectedRow = row;
        row.setSelected(true);
        taskDetailsController.show(row.getTask());
    }

    private void clearSelection() {
        if (selectedRow != null) {
            selectedRow.setSelected(false);
            selectedRow = null;
        }
    }
}
