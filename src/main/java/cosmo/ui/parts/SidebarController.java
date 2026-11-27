package cosmo.ui.parts;

import cosmo.ui.UiUtil;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

public class SidebarController {

    public enum Page { DASHBOARD, ALL_TASKS }

    @FXML private ToggleButton navDashboard;
    @FXML private ToggleButton navAllTasks;
    @FXML private Label overdueBadge;

    private Consumer<Page> onPageChange = page -> { };

    public void setOnPageChange(Consumer<Page> onPageChange) {
        this.onPageChange = onPageChange;
    }


    public void selectPage(Page page) {
        (page == Page.DASHBOARD ? navDashboard : navAllTasks).setSelected(true);
        onPageChange.accept(page);
    }

    public void setOverdueCount(int count) {
        overdueBadge.setText(String.valueOf(count));
        UiUtil.show(overdueBadge, count > 0);
    }


    @FXML private void onShowDashboard() { selectPage(Page.DASHBOARD); }
    @FXML private void onShowAllTasks()  { selectPage(Page.ALL_TASKS); }
}
