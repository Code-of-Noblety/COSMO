package cosmo.ui.popups;

import cosmo.ui.TaskInput;
import cosmo.ui.TaskOptions;
import cosmo.ui.TaskView;
import cosmo.ui.UiUtil;
import java.time.LocalDate;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;


public class TaskFormController {

    @FXML private StackPane overlay;
    @FXML private Label formTitle;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> categoryBox;
    @FXML private ComboBox<String> priorityBox;
    @FXML private VBox newCategoryBox;
    @FXML private TextField newCategoryField;
    @FXML private DatePicker startDate;
    @FXML private DatePicker dueDate;
    @FXML private TextField startTime;
    @FXML private TextField dueTime;
    @FXML private ComboBox<String> startAmPm;
    @FXML private ComboBox<String> dueAmPm;
    @FXML private ComboBox<String> statusBox;
    @FXML private Label errorLabel;

    private Consumer<TaskInput> onSave = input -> { };

    @FXML
    private void initialize() {
        categoryBox.getItems().setAll(TaskOptions.categoriesWithNew());
        priorityBox.getItems().setAll(TaskOptions.PRIORITIES);
        statusBox.getItems().setAll(TaskOptions.STATUSES);

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        startAmPm.getItems().addAll("AM", "PM");
        dueAmPm.getItems().addAll("AM", "PM");

        startAmPm.setValue("AM");
        dueAmPm.setValue("PM");
    }

    // open / close

    public void openForNew(Consumer<TaskInput> onSave) {
        reset();
        open("New Task", onSave);
    }



    public void openForEdit(TaskView task, Consumer<TaskInput> onSave) {
        reset();

        titleField.setText(task.title());
        descriptionArea.setText(task.description());

        categoryBox.setValue(task.category());
        priorityBox.setValue(task.priority());
        statusBox.setValue(task.status());

        startDate.setValue(task.startDate());
        dueDate.setValue(task.dueDate());

        DateTimeFormatter timeFormatter =
                DateTimeFormatter.ofPattern("h:mm");

        DateTimeFormatter amPmFormatter =
                DateTimeFormatter.ofPattern("a", java.util.Locale.US);

        startTime.setText(task.startTime().format(timeFormatter));
        startAmPm.setValue(task.startTime().format(amPmFormatter));

        dueTime.setText(task.dueTime().format(timeFormatter));
        dueAmPm.setValue(task.dueTime().format(amPmFormatter));

        open("Edit Task", onSave);
    }

    public void close() {
        UiUtil.show(overlay, false);
    }


    public void showError(String message) {
        errorLabel.setText(message);
    }

    private void open(String title, Consumer<TaskInput> onSave) {
        this.onSave = onSave;
        formTitle.setText(title);
        UiUtil.show(overlay, true);
        titleField.requestFocus();
    }

    /** Puts the form back to its starting values. */
    private void reset() {
        titleField.clear();
        descriptionArea.clear();
//        categoryBox.setValue(TaskOptions.CATEGORIES.get(0));
        categoryBox.getItems().setAll(TaskOptions.categoriesWithNew());
        categoryBox.setValue(TaskOptions.CATEGORIES.get(0));
        priorityBox.setValue("Medium");
        statusBox.setValue("Pending");
        startDate.setValue(LocalDate.now());
        dueDate.setValue(LocalDate.now());
//        startTime.setValue("09:00 AM");
//        dueTime.setValue("5:00 PM");
        startTime.setText("9:00");
        dueTime.setText("5:00");
        startAmPm.setValue("AM");
        dueAmPm.setValue("PM");
        newCategoryField.clear();
        UiUtil.show(newCategoryBox, false);
        errorLabel.setText("");
    }

    // buttons

    /** Shows the "New category name" box only when "+ New category…" is picked. */
    @FXML
    private void onCategoryPicked() {
        if (newCategoryBox == null) {
            return;     // can fire once while the FXML is loading, before the fields exist
        }
        UiUtil.show(newCategoryBox, TaskOptions.NEW_CATEGORY.equals(categoryBox.getValue()));
    }

    @FXML
    private void onSaveClicked() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Please enter a title.");
            return;
        }
        String category = categoryBox.getValue();
        if (TaskOptions.NEW_CATEGORY.equals(category)) {
            category = newCategoryField.getText().trim();
            if (category.isEmpty()) {
                showError("Please enter a name for the new category.");
                return;
            }
        }


        onSave.accept(new TaskInput(
                title,
                descriptionArea.getText().trim(),
                category,
                priorityBox.getValue(),
                startDate.getValue(),
                startTime.getText().trim() + " " + startAmPm.getValue(),
                dueDate.getValue(),
                dueTime.getText().trim() + " " + dueAmPm.getValue(),
                statusBox.getValue()
        ));
    }

    @FXML
    private void onCancelClicked() {
        close();
    }
}
