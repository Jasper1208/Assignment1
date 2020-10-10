package main.java.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    static Stage stage = new Stage();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../main/java/sample/sample.fxml"));
        stage.setTitle("Test Editor");
        stage.setScene(new Scene(root, 600, 400));

        stage.show();

    }

    public static void main(String[] args) {

        launch(args);
        Controller.setStage(stage);

        // Format the current date
/*        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        Controller.textPane.setText(date);*/
    }
}
