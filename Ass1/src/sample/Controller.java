package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;


@SuppressWarnings("unused")
public class Controller implements Initializable {
    private Stage stage;
    private File fileOpened;

    @FXML
    private TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    void setStage(Stage stage) {
        this.stage = stage;
        openFile(null);
    }
    @FXML
    private void openFile(File file) {
        fileOpened = file;
        if (fileOpened == null) {
            stage.setTitle("Test Editor");
        } else {
            stage.setTitle(fileOpened.getAbsolutePath());
        }
    }
    @FXML
    private void readFile(File file) {
        if (file == null) {
            textArea.setText("");
            return;
        }
        try {
            textArea.setText(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("file open error: " + e.getMessage());
            alert.show();
        }
    }
    @FXML
    private void saveFileAs(File file) {
        try {
            Files.write(Paths.get(file.getAbsolutePath()), textArea.getText().getBytes());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("file write error: " + e.getMessage());
            alert.show();
        }
        openFile(file);
    }


    @FXML
    private void onNotImplementedItemClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        String text = ((MenuItem) event.getTarget()).getText();
        alert.setContentText(text + " not implemented");
        alert.show();
    }


    @FXML
    private void onFileNewClick(ActionEvent event) {
        openFile(null);
        readFile(null);
    }

    @FXML
    private void onFileOpenClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {

            return;
        }
        openFile(file);
        readFile(file);
    }

    @FXML
    private void onFileSaveClick(ActionEvent event) {
        if (fileOpened == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("no file opened");
            alert.show();
            return;
        }
        saveFileAs(fileOpened);
    }


    @FXML
    private void onFileSaveAsClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File As");
        saveFileAs(fileChooser.showSaveDialog(stage));
    }


    @FXML
    private void onDateClick(ActionEvent event) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());

        textArea.appendText(date);
    }
    @FXML
    private void onAboutClick(){
        Stage stage = new Stage();
        stage.setTitle("About members of work group");
        Label findText = new Label("Ocean Liu & Fengqi Jia");

    }

    @FXML
    private void onCopyClick() {

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();


        String copyText = textArea.getSelectedText();

//        System.out.println("copyText = " + copyText);

        Transferable trans = new StringSelection(copyText);

        clipboard.setContents(trans, null);
    }

    @FXML
    private void onCutClick(){

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();


        String copyText = textArea.getSelectedText();

//        System.out.println("copyText = " + copyText);

        Transferable trans = new StringSelection(copyText);

//        clipboard.setContents(trans, null);
        textArea.replaceSelection("");

    }
    @FXML
    private void onDeleteClick(){
        textArea.replaceSelection("");

    }

    @FXML
    private void onPasteClick(ActionEvent event) {

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();


        Transferable trans = clipboard.getContents(null);

        if (trans != null) {

            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {

                    String text = (String) trans.getTransferData(DataFlavor.stringFlavor);

                    textArea.appendText(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @FXML
    private void onFindClick() {
        Stage stage = new Stage();
        stage.setTitle("Search");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Label findText = new Label("Search");
        grid.add(findText, 0, 1);

        TextField findTextField = new TextField();
        grid.add(findTextField, 1, 1);


        Label replaceText = new Label("Search");
        grid.add(replaceText, 0, 2);

        TextField replaceTextField = new TextField();
        grid.add(replaceTextField, 1, 2);


        Button btn1 = new Button("Search");
        HBox hbBtn1 = new HBox(10);
        hbBtn1.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn1.getChildren().add(btn1);
        grid.add(hbBtn1, 0, 4);


        btn1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                String text = textArea.getText();

                String findWords =  findTextField.getText();

                if(findWords != null && !findWords.isEmpty()) {


                    Text t = new Text(findWords);

                    t.setFill(Color.RED);
                    text = text.replaceAll(findWords, t.toString());

                    textArea.clear();
                    textArea.appendText(text);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("content!");
                    alert.show();
                }
            }
        });

        Button btn2 = new Button("all Search");
        HBox hbBtn2 = new HBox(10);
        hbBtn2.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn2.getChildren().add(btn2);
        grid.add(hbBtn2, 1, 4);


        btn2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                String text = textArea.getText();

                String findWords =  findTextField.getText();

                String replaceWords = replaceTextField.getText();

                if(findWords != null && !findWords.isEmpty() && replaceWords != null && !replaceWords.isEmpty()) {
                    System.out.println("text1 = " + text);

                    text = text.replaceAll(findWords, replaceWords);
                    System.out.println("text2 = " + text);

                    textArea.clear();
                    textArea.appendText(text);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("content!");
                    alert.show();
                }
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    private void onFileQuitClick(ActionEvent event) {
        Platform.exit();
    }
}
