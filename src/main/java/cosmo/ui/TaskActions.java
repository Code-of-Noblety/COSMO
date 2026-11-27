package cosmo.ui;


public interface TaskActions {

    void newTask();

    void editTask(TaskView task);

    void deleteTask(TaskView task);

    /** The check circle: switch between Completed and Pending. */
    void toggleCompleted(TaskView task);

    /** The status dropdown on a row. */
    void changeStatus(TaskView task, String newStatus);
}
