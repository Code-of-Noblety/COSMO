package cosmo.ui.pages;

import cosmo.ui.TaskActions;
import cosmo.ui.TaskOptions;
import cosmo.ui.TaskView;
import cosmo.ui.parts.TaskDetailsController;
import cosmo.ui.parts.TaskTableCells;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.ArrayList;



public class AllTasksController {


    private static final double ROW_HEIGHT = 56;
    private static final double HEADER_HEIGHT = 38;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> priorityFilter;
    @FXML private ComboBox<String> sortBox;
    @FXML private TableView<TaskView> taskTable;
    @FXML private TableColumn<TaskView, TaskView> doneColumn;
    @FXML private TableColumn<TaskView, TaskView> titleColumn;
    @FXML private TableColumn<TaskView, TaskView> categoryColumn;
    @FXML private TableColumn<TaskView, TaskView> priorityColumn;
    @FXML private TableColumn<TaskView, TaskView> startColumn;
    @FXML private TableColumn<TaskView, TaskView> dueColumn;
    @FXML private TableColumn<TaskView, TaskView> statusColumn;
    @FXML private TaskDetailsController taskDetailsController;    // the included side panel

    private TaskActions actions;
    private ObservableList<TaskView> sourceTasks;

    private FilteredList<TaskView> filteredTasks;
    private SortedList<TaskView> sortedTasks;
    private final ListChangeListener<TaskView> categoryChanges =
            change -> refreshCategoryFilter();

    @FXML
    private void initialize() {
        setUpFilters();
        setUpTable();
        keepTableHeightToRows();
        taskDetailsController.setOnClosed(() -> taskTable.getSelectionModel().clearSelection());
    }



    public void setActions(TaskActions actions) {
        this.actions = actions;
        taskDetailsController.setActions(actions);
    }



public void setTasks(ObservableList<TaskView> tasks) {

    if (sourceTasks == tasks && filteredTasks != null) {
        refreshCategoryFilter();
        applyFilters();
        return;
    }

    if (sourceTasks != null) {
        sourceTasks.removeListener(categoryChanges);
    }

    sourceTasks = tasks;
    sourceTasks.addListener(categoryChanges);

    filteredTasks = new FilteredList<>(sourceTasks);
    sortedTasks = new SortedList<>(filteredTasks);

    taskTable.setItems(sortedTasks);

    refreshCategoryFilter();
    applyFilters();
}

    private void refreshCategoryFilter() {

        String selected = categoryFilter.getValue();

        List<String> categories = new ArrayList<>(TaskOptions.CATEGORIES);

        if (sourceTasks != null) {
            for (TaskView task : sourceTasks) {

                String category = task.category();

                if (category != null
                        && !category.isBlank()
                        && !categories.contains(category)) {

                    categories.add(category);
                }
            }
        }

        categoryFilter.getItems().setAll(
                TaskOptions.withAll(categories)
        );

        if (selected != null && categoryFilter.getItems().contains(selected)) {
            categoryFilter.setValue(selected);
        } else {
            categoryFilter.setValue("All");
        }
    }

    //  filters

    private void setUpFilters() {
        statusFilter.getItems().setAll(TaskOptions.STATUS_FILTERS);
        categoryFilter.getItems().setAll(TaskOptions.withAll(TaskOptions.CATEGORIES));
        priorityFilter.getItems().setAll(TaskOptions.withAll(TaskOptions.PRIORITIES));
        sortBox.getItems().setAll(TaskOptions.SORTS);

        statusFilter.setValue("All");
        categoryFilter.setValue("All");
        priorityFilter.setValue("All");
        sortBox.setValue(TaskOptions.SORTS.get(0));

        // The closed dropdown reads "Status: All" while the open list shows just "All".
        showPrefix(statusFilter, "Status: ");
        showPrefix(categoryFilter, "Category: ");
        showPrefix(priorityFilter, "Priority: ");
        showPrefix(sortBox, "Sort: ");


        searchField.textProperty().addListener(
                (obs, oldText, newText) -> applyFilters()
        );

        statusFilter.valueProperty().addListener(
                (obs, oldValue, newValue) -> applyFilters()
        );

        categoryFilter.valueProperty().addListener(
                (obs, oldValue, newValue) -> applyFilters()
        );

        priorityFilter.valueProperty().addListener(
                (obs, oldValue, newValue) -> applyFilters()
        );

        sortBox.valueProperty().addListener(
                (obs, oldValue, newValue) -> applyFilters()
        );
    }

    private void applyFilters() {

        if (filteredTasks == null) {
            return;
        }

        String search = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String selectedStatus = statusFilter.getValue();
        String selectedCategory = categoryFilter.getValue();
        String selectedPriority = priorityFilter.getValue();

        filteredTasks.setPredicate(task -> {

            // Search
            boolean matchesSearch =
                    search.isEmpty()
                            || task.title().toLowerCase().contains(search)
                            || task.description().toLowerCase().contains(search);

            // Status
            boolean matchesStatus =
                    selectedStatus.equals("All")
                            || (selectedStatus.equals("Active") && !task.isCompleted())
                            || (selectedStatus.equals("Completed") && task.isCompleted());

            // Category
            boolean matchesCategory =
                    selectedCategory.equals("All")
                            || task.category().equals(selectedCategory);

            // Priority
            boolean matchesPriority =
                    selectedPriority.equals("All")
                            || task.priority().equals(selectedPriority);

            return matchesSearch
                    && matchesStatus
                    && matchesCategory
                    && matchesPriority;
        });

        applySort();
    }

    private void applySort() {
        String sort = sortBox.getValue();

        Comparator<TaskView> comparator = null;

        if ("Priority".equals(sort)) {
            comparator = Comparator.comparingInt(task -> {
                return switch (task.priority()) {
                    case "High" -> 1;
                    case "Medium" -> 2;
                    case "Low" -> 3;
                    default -> 4;
                };
            });

        } else if ("Due date".equals(sort)) {
            comparator = Comparator.comparing(
                    task -> LocalDateTime.of(task.dueDate(), task.dueTime())
            );

        } else if ("Start date".equals(sort)) {
            comparator = Comparator.comparing(
                    task -> LocalDateTime.of(task.startDate(), task.startTime())
            );
        }

        sortedTasks.setComparator(comparator);
    }

    private static void showPrefix(ComboBox<String> box, String prefix) {
        box.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : prefix + item);
            }
        });
    }

    //  table

    private void setUpTable() {
        List<TableColumn<TaskView, TaskView>> columns =
                List.of(doneColumn, titleColumn, categoryColumn, priorityColumn, startColumn, dueColumn, statusColumn);
        for (TableColumn<TaskView, TaskView> column : columns) {
            // every cell gets the whole task, so each column can draw what it needs
            column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        }

        // The two clickable cells report to TaskActions (it is set later, so it is read at click time).
        doneColumn.setCellFactory(column -> TaskTableCells.doneCell(task -> actions.toggleCompleted(task)));
        titleColumn.setCellFactory(column -> TaskTableCells.titleCell());
        categoryColumn.setCellFactory(column -> TaskTableCells.categoryCell());
        priorityColumn.setCellFactory(column -> TaskTableCells.priorityCell());
        startColumn.setCellFactory(column -> TaskTableCells.dateCell(false));
        dueColumn.setCellFactory(column -> TaskTableCells.dateCell(true));
        statusColumn.setCellFactory(column ->
                TaskTableCells.statusCell((task, status) -> actions.changeStatus(task, status)));
        taskTable.setRowFactory(table -> TaskTableCells.tableRow());

        // Selecting a row opens the details panel.
        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldTask, task) -> {
            if (task == null) {
                taskDetailsController.hide();
            } else {
                taskDetailsController.show(task);
            }
        });
    }


    private void keepTableHeightToRows() {
        taskTable.setFixedCellSize(ROW_HEIGHT);

        Runnable resize = () -> {
            int rows = Math.max(taskTable.getItems().size(), 1);      // at least 1 row of room for "No tasks match"
            taskTable.setPrefHeight(rows * ROW_HEIGHT + HEADER_HEIGHT + 2);
        };
        ListChangeListener<TaskView> onRowsChanged = change -> resize.run();

        taskTable.getItems().addListener(onRowsChanged);
        taskTable.itemsProperty().addListener((obs, oldItems, newItems) -> {   // someone called setItems(...)
            oldItems.removeListener(onRowsChanged);
            newItems.addListener(onRowsChanged);
            resize.run();
        });
        resize.run();
    }

    // buttons

    @FXML
    private void onNewTaskClicked() {
        actions.newTask();
    }
}
