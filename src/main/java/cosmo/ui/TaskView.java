


package cosmo.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record TaskView(
        String title,
        String description,
        String category,
        String priority,
        String status,
        LocalDate startDate,
        LocalTime startTime,
        LocalDate dueDate,
        LocalTime dueTime,
        TaskView.Highlight highlight,
        LocalDateTime createdAt) {

   
    public TaskView(
            String title,
            String description,
            String category,
            String priority,
            String status,
            LocalDate startDate,
            LocalTime startTime,
            LocalDate dueDate,
            LocalTime dueTime,
            Highlight highlight) {

        this(title, description, category, priority, status,
                startDate, startTime, dueDate, dueTime,
                highlight, LocalDateTime.now());
    }

    public enum Highlight { NONE, OVERDUE, TODAY }

    public boolean isCompleted() {
        return "Completed".equals(status);
    }

    public String startText() {
        return formatDate(startDate) + " · " + formatTime(startTime);
    }

    public String dueText() {
        return formatDate(dueDate) + " · " + formatTime(dueTime);
    }

    public static String formatDate(LocalDate date) {
        LocalDate today = LocalDate.now();

        if (date.equals(today)) return "Today";
        if (date.equals(today.plusDays(1))) return "Tomorrow";
        if (date.equals(today.minusDays(1))) return "Yesterday";

        return date.format(DateTimeFormatter.ofPattern("MMM d"));
    }

    public static String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("h:mm a"));
    }
}

