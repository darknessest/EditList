package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Controller implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RecordInfoPane.setDisable(true);
    }

    //************************************
    //***           VARIABLES          ***
    //************************************
    private Record AllRecords[];
    private long currentRecordNum;
    @FXML
    AnchorPane RecordInfoPane;
    @FXML
    ListView RecordsList;
    @FXML
    TextField StuNumfield, NameField, PhoneField, EmailField;
    @FXML
    Button AddNewRecordButton, NextRecordButton, PrevRecordButton;
    @FXML
    Label StateLabel;

    //************************************
    //***           FUNCTIONS          ***
    //************************************
    @FXML
    public void OpenFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
                , new FileChooser.ExtensionFilter("Database Files", "*.rdb"));
        if (file != null) {
            openDatabase(file);
        }
    }

    @FXML
    public void CreateNewFile() {
        RecordInfoPane.setDisable(false);

    }

    @FXML
    public void CloseFile() {

    }

    @FXML
    public void SaveAs() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt")
                , new FileChooser.ExtensionFilter("Database Files", "*.rdb"));

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            saveToFile(AllRecords, file);
        }

    }

    //************************************
    //***       SERVING METHODS        ***
    //************************************
    private void openDatabase(File file) {
        System.out.println("hello " + file.getName());
        int i = 1;

        for (Record x : AllRecords)
            RecordsList.getItems().add(i++ + ") " + x.getStuNum());
    }


    private void saveToFile(Record[] content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            for (Record x : content)
                writer.println(x.toString());
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addNewRecord() {

    }

    private void loadUpRecord() {

    }
}
