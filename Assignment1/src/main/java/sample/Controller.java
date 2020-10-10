package main.java.sample;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.javafx.sg.prism.NGText;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;

import com.itextpdf.text.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller extends Application implements Initializable {


    static Stage mainStage = new Stage();
    private NGText EditText;
    private Object Html;

    public static void setStage(Stage stage) {
        mainStage = stage;
    }


    @FXML
    private AnchorPane MainPane;

    @FXML
    private MenuBar Menu;

    @FXML
    private Menu File;

    @FXML
    private MenuItem New;

    @FXML
    private MenuItem Open;

    @FXML
    private MenuItem Save;

    @FXML
    private MenuItem Print;

    @FXML
    private MenuItem Exit;

    @FXML
    private MenuItem EsSave;

    @FXML
    private Menu Search;

    @FXML
    private Menu View;

    @FXML
    private Menu Manage;

    @FXML
    private Menu Help;

    @FXML
    private Menu About;

    @FXML
    public TextArea textPane;

    @FXML
    private CodeArea codeArea;

    @FXML
    private ContextMenu RightClick;

    @FXML
    private MenuItem Select;

    @FXML
    private MenuItem Copy;

    @FXML
    private MenuItem Paste;

    @FXML
    private MenuItem Cut;

    // Format the current date
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public String date = sdf.format(new Date());
    private String path;

    int startIndex = 0;

    public static final String[] KEYWORDS = new String[]{
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    public static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    public static final String PAREN_PATTERN = "\\(|\\)";
    public static final String BRACE_PATTERN = "\\{|\\}";
    public static final String BRACKET_PATTERN = "\\[|\\]";
    public static final String SEMICOLON_PATTERN = "\\;";
    public static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    public static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    public static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    public static final String sampleCode = String.join("\n", new String[]{
            "package com.example;",
            "",
            "import java.util.*;",
            "",
            "public class Foo extends Bar implements Baz {",
            "",
            "    /*",
            "     * multi-line comment",
            "     */",
            "    public static void main(String[] args) {",
            "        // single-line comment",
            "        for(String arg: args) {",
            "            if(arg.length() != 0)",
            "                System.out.println(arg);",
            "            else",
            "                System.err.println(\"Warning: empty string as argument\");",
            "        }",
            "    }",
            "",
            "}"
    });

    public static void main(String[] args) {

        launch(args);

        // Format the current date
/*        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        Controller.textPane.setText(date);*/
    }

    private ExecutorService executor;

    @Override
    public void start(Stage primaryStage) throws IOException {


        // call when no longer need it: `cleanupWhenFinished.unsubscribe();`
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add("java-keywords.css");

        primaryStage.setTitle("Test Editor");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.codeArea.replaceText(date);
    }

/*    @Override
    public void stop() {
        executor.shutdown();
    }*/

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @FXML
    public void clickCopy(ActionEvent event) {
// Gets the system clipboard
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        // Select the text
        String temp = codeArea.getSelectedText();
        // Puts the retrieved text into the system clipboard
        content.putString(temp);
        // Place the content on the text clipboard
        clipboard.setContent(content);
    }

    @FXML
    public void clickCut(ActionEvent event) {
// Gets the system clipboard
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        // Select the text
        String temp = codeArea.getSelectedText();
        // Puts the retrieved text into the system clipboard
        content.putString(temp);
        // Place the content on the text clipboard
        clipboard.setContent(content);
        // Replace the selected string with an empty string
        codeArea.replaceSelection("");
    }

    @FXML
    public void clickExit(ActionEvent event) {

        Platform.exit();

    }

    @FXML
    public void clickFile(ActionEvent event) {
        //codeArea.replaceText(date);
    }

    @FXML
    public void clickHelp(ActionEvent event) {

    }

    @FXML
    public void clickManage(ActionEvent event) {

    }


    @FXML
    public void clickNew(ActionEvent event) throws IOException {
        Controller open = new Controller();
        open.start(new Stage());
        //  textPane.setText(date);
//        //Change window title
//
//        //Sets the path to the open file to NULL
//        path = null;
    }


    boolean save;
    private Stage stage;
    private File fileOpened;

    private void openFile(File file) {
        fileOpened = file;
        if (fileOpened == null) {
            stage.setTitle("CodePad");
        } else {
            stage.setTitle(fileOpened.getAbsolutePath());
        }
    }

    private void saveFileAs(File file) {
        try {
            Files.write(Paths.get(file.getAbsolutePath()), codeArea.getText().getBytes());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("file write error: " + e.getMessage());
            alert.show();
        }
        openFile(file);
    }


    @FXML
    public void clickEsSave(ActionEvent actionEvent) throws IOException, DocumentException {

        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File As");
        saveFileAs(fileChooser.showSaveDialog(stage));*/

        // 1.new construction document object
        Document document = new Document();
        // 2.Create a writer(Writer)associate with a Document object，Documents can be written to disk using a Writer。
        // The first argument to creating the PdfWriter object is a reference to the document object, and the second argument is the actual name of the file, with its output path given in the name。
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/test.pdf"));
        // 3.Open the document
        document.open();
        // 4.Add a content paragraph
        document.add(new Paragraph(codeArea.getText()));
        // 5.Close the document
        document.close();


      /*  Document document = new Document();
        OutputStream os = new FileOutputStream(new File("G:/1111.txt" ));
        PdfWriter.getInstance(document, os);
        document.open();
        //Methods one：Use Windows fonts(TrueType)
        BaseFont baseFont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font font = new Font(baseFont);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(new File("G:/5555.pdf")), "UTf-8");
        BufferedReader bufferedReader = new BufferedReader(isr);
        String str = "";
        while ((str = bufferedReader.readLine()) != null) {
            document.add(new Paragraph(str, font));
        }
        document.close();*/

    }

    @FXML
    public void clickAbout(ActionEvent actionEvent) {
        JOptionPane.showMessageDialog(null, "Work members:Ocean Liu & Fengqi Jia", "About", JOptionPane.PLAIN_MESSAGE);
    }

    public void setSearch(String b) {

        String detail = codeArea.getText();
        // String detail = textPane.getText();
        String findcontant = b;
        if (!findcontant.isEmpty()) {
            while ((startIndex = detail.indexOf(b, startIndex)) >= 0) {
                codeArea.selectRange(startIndex, startIndex + b.length());
                codeArea.setStyleClass(startIndex, startIndex + b.length(), "pink");
                textPane.setStyle("-fx-text-fill: pink");


                //CodeArea.setEditable(false);
                //CodeArea.addEventFilter(MouseEvent.ANY,new EventHandler< MouseEvent>() {
                //@Override
                //public void handle(MouseEvent t)
                //{
                //  t.consume();
                //}
                //});
                startIndex += b.length();

            }
            //  textPane.setEditable(true);
        } else if (b.isEmpty()) {

        }
    }

    @FXML
    public void clickOpen(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("open");

        java.io.File file = fc.showOpenDialog(mainStage);
        if (file != null && file.exists()) {
            try {

                FileInputStream in = new FileInputStream(file);
                byte[] bs = new byte[(int) file.length()];
                in.read(bs);
                codeArea.replaceText(new String(bs));
                executor = Executors.newSingleThreadExecutor();
                codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

                Subscription cleanupWhenDone = codeArea.multiPlainChanges()
                        .successionEnds(Duration.ofMillis(500))
                        .supplyTask(this::computeHighlightingAsync)
                        .awaitLatest(codeArea.multiPlainChanges())
                        .filterMap(t -> {
                            if (t.isSuccess()) {
                                return Optional.of(t.get());
                            } else {
                                t.getFailure().printStackTrace();
                                return Optional.empty();
                            }
                        })
                        .subscribe(this::applyHighlighting);
                /*val colors= mutableListOf("red","purple","green","yellow","blue");
                val color=colors.shuffled().take(1);
                ca.setStyle("-fx-background-color: ${color[0]};-fx-font-size: 20px;")*/
                in.close();

                path = file.getPath();

                int lastIndex = path.lastIndexOf("\\");
/*                String title = path.substring(lastIndex + 1);
                String a = textPane.getText();
                for (String keyword : KEYWORDS) {
                    setSearch(keyword);
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    public void clickSave(ActionEvent actionEvent) {
        if (path == null) {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select File-Save");
            // Gets the file selected by the user
            File file = fc.showSaveDialog(mainStage);
            // If the user selects it and the file exists
            if (file != null && !file.exists()) {
                // Write the contents of the multi-line text box to the file pointed to by file
                try {
                    // Create an output stream
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(codeArea.getText().getBytes());
                    out.flush();
                    out.close();
                    save = true; // I've saved it
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {// Save after opening
            try {
                // Create an output stream
                FileOutputStream out = new FileOutputStream(path);
                out.write(codeArea.getText().getBytes());
                out.flush();
                out.close();
                save = true;// I've saved it
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void clickPrint(ActionEvent actionEvent) {

        System.out.println("Creating a printer job...");

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            System.out.println(job.jobStatusProperty().asString());

            boolean printed = job.printPage(codeArea);
            if (printed) {
                job.endJob();
            } else {
                System.out.println("Printing failed.");
            }
        } else {
            System.out.println("Could not create a printer job.");
        }
    }

    @FXML
    public void clickSearch(ActionEvent actionEvent) {
        Stage stage1 = new Stage();
        TextField find = new TextField();
        Button button = new Button("find");
        GridPane findPage = new GridPane();

        findPage.add(find, 0, 0);
        findPage.add(button, 2, 0);

        findPage.setAlignment(Pos.CENTER);

        Scene scene = new Scene(findPage, 400, 100);
        stage1.setTitle("search");
        stage1.setScene(scene);
        stage1.show();

        button.setOnAction(item -> {
            String detail = codeArea.getText();
            String findcontant = find.getText();
            if (!findcontant.isEmpty()) {
                if (detail.contains(findcontant)) {
                    if (startIndex == -1) {
                        Alert alert1 = new Alert(Alert.AlertType.WARNING);
                        alert1.titleProperty().set("Error");
                        alert1.headerTextProperty().set("Can't find relevant content already!!");
                        alert1.show();
                    }
                    startIndex = codeArea.getText().indexOf(find.getText(), startIndex);
                    if (startIndex >= 0 && startIndex < codeArea.getText().length()) {
                        codeArea.selectRange(startIndex, startIndex + find.getText().length());
                        startIndex += find.getText().length();
                    }
                }
                if (!detail.contains(findcontant)) {
                    Alert alertNull = new Alert(Alert.AlertType.WARNING);
                    alertNull.titleProperty().set("Error");
                    alertNull.headerTextProperty().set("Can't find relevant content already!!");
                    alertNull.show();
                }
            } else if (find.getText().isEmpty()) {
                Alert alertEmpty = new Alert(Alert.AlertType.WARNING);
                alertEmpty.titleProperty().set("Error");
                alertEmpty.headerTextProperty().set("The input is empty!");
                alertEmpty.show();
            }
        });
    }


    @FXML
    public void clickView(ActionEvent actionEvent) {
    }

    @FXML
    public void clickRight(ActionEvent actionEvent) {
    }

    @FXML
    public void clickSelect(ActionEvent actionEvent) {
        codeArea.selectAll();
    }

    @FXML
    public void clickPaste(ActionEvent actionEvent) {
        // Gets the system clipboard
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        Clipboard c = clipboard.getSystemClipboard();
        if (c.hasContent(DataFormat.PLAIN_TEXT)) {
            String s = c.getContent(DataFormat.PLAIN_TEXT).toString();
            if (codeArea.getSelectedText() != null) { // If you want to paste, the mouse has already selected a character
                codeArea.replaceSelection(s);
            } else { // If the mouse does not select a character to paste, paste directly from behind the cursor
                int mouse = codeArea.getCaretPosition(); // Gets the location of the text field mouse
                codeArea.insertText(mouse, s);
            }
        }
    }
}
