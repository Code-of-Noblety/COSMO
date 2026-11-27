package cosmo.ui.parts;

import cosmo.ui.TaskOptions;
import cosmo.ui.TaskView;
import cosmo.ui.UiUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;


public final class TaskTableCells {

    private TaskTableCells() { }

    private static TaskView.Highlight getCurrentHighlight(TaskView task) {
        if (task.isCompleted()) {
            return TaskView.Highlight.NONE;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDateTime =
                LocalDateTime.of(task.dueDate(), task.dueTime());

        if (dueDateTime.isBefore(now)) {
            return TaskView.Highlight.OVERDUE;
        }

        if (task.dueDate().equals(LocalDate.now())) {
            return TaskView.Highlight.TODAY;
        }

        return TaskView.Highlight.NONE;
    }


    public static TableCell<TaskView, TaskView> doneCell(Consumer<TaskView> onToggle) {
        return new TableCell<>() {
            private final ToggleButton circle = new ToggleButton();

            {
                circle.getStyleClass().add("check-circle");
                circle.setOnAction(event -> {
                    if (getItem() != null) {
                        onToggle.accept(getItem());
                    }
                });
            }

            @Override
            protected void updateItem(TaskView task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                    return;
                }
                circle.setSelected(task.isCompleted());
                setGraphic(circle);
            }
        };
    }

    /** Title text finished tasks are gray and crossed out. */
    public static TableCell<TaskView, TaskView> titleCell() {
        return new TableCell<>() {
            {
                getStyleClass().add("title-cell");
            }

            @Override
            protected void updateItem(TaskView task, boolean empty) {
                super.updateItem(task, empty);
                boolean show = !empty && task != null;
                setText(show ? task.title() : null);
                UiUtil.toggleClass(this, "done-text", show && task.isCompleted());
            }
        };
    }


    public static TableCell<TaskView, TaskView> categoryCell() {
        return new TableCell<>() {
            private final Label pill = new Label();

            {
                pill.getStyleClass().addAll("pill", "table-pill");
            }

            @Override
            protected void updateItem(TaskView task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                    return;
                }
                pill.setText(task.category());
                UiUtil.replaceClass(pill, "pill-", UiUtil.categoryClass(task.category()));
                setGraphic(pill);
            }
        };
    }

    /** Plain text colored by priority (High = purple, Medium = normal, Low = gray). */
    public static TableCell<TaskView, TaskView> priorityCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(TaskView task, boolean empty) {
                super.updateItem(task, empty);
                boolean show = !empty && task != null;
                setText(show ? task.priority() : null);
                getStyleClass().removeIf(c -> c.startsWith("priority-"));
                if (show) {
                    getStyleClass().add(UiUtil.priorityClass(task.priority()));
                }
            }
        };
    }

    public static TableCell<TaskView, TaskView> dateCell(boolean due) {
        return new TableCell<>() {
            private final Label date = new Label();
            private final Label time = new Label();
            private final Label tag = new Label();
            private final VBox box = new VBox(date, time, tag);

            {
                date.getStyleClass().add("cell-date");
                time.getStyleClass().add("cell-time");
                tag.getStyleClass().add("cell-tag");
            }

            @Override
            protected void updateItem(TaskView task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                    return;
                }

                date.setText(TaskView.formatDate(due ? task.dueDate() : task.startDate()));
                time.setText(TaskView.formatTime(due ? task.dueTime() : task.startTime()));



                TaskView.Highlight highlight = getCurrentHighlight(task);

                boolean overdue = due && highlight == TaskView.Highlight.OVERDUE;
                boolean today = due && highlight == TaskView.Highlight.TODAY;
                tag.setText(overdue ? "⚠ Overdue" : "Due today");
                UiUtil.show(tag, overdue || today);
                UiUtil.toggleClass(box, "due-overdue", overdue);
                UiUtil.toggleClass(box, "due-today", today);
                setGraphic(box);
            }
        };
    }

    public static TableCell<TaskView, TaskView> statusCell(BiConsumer<TaskView, String> onChange) {
        return new TableCell<>() {
            private final ComboBox<String> box = new ComboBox<>();
            private boolean filling;

            {
                box.getItems().setAll(TaskOptions.STATUSES);
                box.getStyleClass().add("combo-status");
                box.setOnAction(event -> {
                    TaskView task = getItem();
                    if (!filling && task != null && !box.getValue().equals(task.status())) {
                        onChange.accept(task, box.getValue());
                    }
                });
            }

            @Override
            protected void updateItem(TaskView task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                    return;
                }
                filling = true;
                box.setValue(task.status());
                filling = false;
                UiUtil.replaceClass(box, "status-", UiUtil.statusClass(task.status()));
                setGraphic(box);
            }
        };
    }

    public static TableRow<TaskView> tableRow() {
        return new TableRow<>() {
            @Override
            protected void updateItem(TaskView task, boolean empty) {
                super.updateItem(task, empty);
                boolean show = !empty && task != null;


                TaskView.Highlight highlight = show
                        ? getCurrentHighlight(task)
                        : TaskView.Highlight.NONE;

                UiUtil.toggleClass(this, "row-overdue",
                        show && highlight == TaskView.Highlight.OVERDUE);

                UiUtil.toggleClass(this, "row-today",
                        show && highlight == TaskView.Highlight.TODAY);
            }
        };
    }
}
