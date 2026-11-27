package cosmo.ui;

import cosmo.ui.TaskView.Highlight;
//import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalTime;


public final class DemoData {

    private DemoData() { }


//collection of datas

    public static ObservableList<TaskView> tasks =
            FXCollections.observableArrayList(
                    new TaskView("Submit Networking Worksheet", "Upload to the class portal.", "Academic", "High", "Pending",
                            LocalDate.now().minusDays(8), LocalTime.of(9, 0),
                            LocalDate.now().minusDays(1), LocalTime.of(23, 59), Highlight.OVERDUE),

                    new TaskView("Finish Digital Logic Design", "Truth tables and K-maps.", "Academic", "High", "In Progress",
                            LocalDate.now(), LocalTime.of(9, 0),
                            LocalDate.now(), LocalTime.of(17, 0), Highlight.TODAY),

                    new TaskView("Review Database Lesson", "", "Academic", "Medium", "Pending",
                            LocalDate.now(), LocalTime.of(14, 0),
                            LocalDate.now().plusDays(1), LocalTime.of(12, 0), Highlight.NONE),

                    new TaskView("Maligo", "", "Personal", "Low", "Pending",
                            LocalDate.now(), LocalTime.of(18, 0),
                            LocalDate.now(), LocalTime.of(19, 0), Highlight.TODAY),

                    new TaskView("Read OOP Chapter 4", "", "Academic", "Medium", "Completed",
                            LocalDate.now(), LocalTime.of(8, 0),
                            LocalDate.now(), LocalTime.of(10, 0), Highlight.NONE),

                    new TaskView("Prepare ER diagram", "For the DB project.", "Academic", "High", "Pending",
                            LocalDate.now().minusDays(3), LocalTime.of(14, 0),
                            LocalDate.now().minusDays(1), LocalTime.of(23, 59), Highlight.NONE),

                    new TaskView("Team standup notes", "", "Work", "Medium", "Pending",
                            LocalDate.now(), LocalTime.of(16, 0),
                            LocalDate.now(), LocalTime.of(16, 30), Highlight.TODAY),

                    new TaskView("Group project outline", "", "Academic", "Low", "Pending",
                            LocalDate.now().plusDays(2), LocalTime.of(10, 0),
                            LocalDate.now().plusDays(9), LocalTime.of(12, 0), Highlight.NONE),

                    new TaskView("Laundry", "", "Personal", "Low", "Pending",
                            LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                            LocalDate.now().plusDays(1), LocalTime.of(20, 0), Highlight.NONE)
            );
        }



