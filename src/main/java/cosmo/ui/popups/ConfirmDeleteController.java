package cosmo.ui.popups;

import cosmo.ui.UiUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;


public class ConfirmDeleteController {

    @FXML private StackPane overlay;
    @FXML private Label messageLabel;

    private Runnable confirmAction = () -> { };

    public void open(String taskTitle, Runnable confirmAction) {
        this.confirmAction = confirmAction;
        messageLabel.setText("“" + taskTitle + "” will be permanently removed.");
        UiUtil.show(overlay, true);
    }

    @FXML
    private void onConfirmClicked() {
        UiUtil.show(overlay, false);
        confirmAction.run();
    }

    @FXML
    private void onCancelClicked() {
        UiUtil.show(overlay, false);
    }
}
