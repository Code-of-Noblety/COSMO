package cosmo;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("/cosmo/fxml/App.fxml"));


        Scene scene = new Scene(root);


        scene.getStylesheets().addAll(root.getStylesheets());
        root.getStylesheets().clear();

        stage.setTitle("COSMO - To-Do System");
        stage.setResizable(false);
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.getIcons().add(
            new Image(Main.class.getResourceAsStream("/cosmo/images/cosmo.png"))
        );
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
