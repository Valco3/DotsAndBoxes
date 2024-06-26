package wrapper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DotsAndBoxes extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root
                = FXMLLoader.load(getClass().getResource("gui.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("Game");
        stage.sizeToScene();
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
            launch(args);
    }
}