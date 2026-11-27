package cosmo.ui;

import java.time.LocalDate;

/**
 * What the user typed into the New / Edit task form, exactly as entered.
 */
public record TaskInput(
        String title,
        String description,
        String category,
        String priority,
        LocalDate startDate,
        String startTime,
        LocalDate dueDate,
        String dueTime,
        String status) { }
