package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;


public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        stage.setTitle("Test Editor");
//        stage.setScene(new Scene(root, 800, 600));
//        stage.show();






//        URL location = getClass().getResource("sample.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
//        Parent root =FXMLLoader.load(getClass().getResource("sample.fxml"));
//
//
       Scene scene = new Scene(root);


        stage.setScene(scene);

        Controller controller = fxmlLoader.getController();
        controller.setStage(stage);





//        URL location = getClass().getResource("sample.fxml");
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(location);
//        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
//        Parent root = fxmlLoader.load();

//        stage.setTitle("Hello World");
//        Scene scene = new Scene(root, 400, 500);

//        //scene.getStylesheets().add(getClass().getResource("style1.css").toExternalForm());
//        stage.setScene(scene);
//        Controller controller = fxmlLoader.getController();

////        controller.Init();
        stage.show();

    }

}
