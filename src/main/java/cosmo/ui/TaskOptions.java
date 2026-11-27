package cosmo.ui;

import java.util.ArrayList;
import java.util.List;


public final class TaskOptions {

    public static final List<String> STATUSES = List.of("Pending", "In Progress", "Completed");
    public static final List<String> PRIORITIES = List.of("High", "Medium", "Low");
    public static final List<String> CATEGORIES = List.of("Academic", "Personal", "Work");

    public static final List<String> STATUS_FILTERS = List.of("All", "Active", "Completed");
    public static final List<String> SORTS = List.of("Creation order", "Priority", "Due date", "Start date");


    public static final String NEW_CATEGORY = "+ New category…";

    private TaskOptions() {
    }

    public static List<String> withAll(List<String> options) {
        List<String> result = new ArrayList<>();
        result.add("All");
        result.addAll(options);
        return result;
    }



    /**
     * Categories used by existing tasks, plus the option to create a new one.
     */
    public static List<String> categoriesWithNew() {
        List<String> result = new ArrayList<>(CATEGORIES);

        for (TaskView task : DemoData.tasks) {
            String category = task.category();

            if (category != null
                    && !category.isBlank()
                    && !result.contains(category)) {
                result.add(category);
            }
        }

        result.add(NEW_CATEGORY);
        return result;
    }

}