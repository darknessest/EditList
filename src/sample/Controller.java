package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


//TODO add edit/save button in record window
//TODO moving from one record to another
//TODO test database file
//TODO add different messages to statuslabel
//TODO proper opening and closing sequences

public class Controller implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RecordInfoPane.setDisable(true);
        RecordButtonsBox.setDisable(true);
        RecordButtonsBox.setVisible(false);
    }

    //************************************
    //***           VARIABLES          ***
    //************************************
    private List<Record> AllRecords = new ArrayList<Record>();
    private long currentRecordNum;
    @FXML
    AnchorPane RecordInfoPane;
    @FXML
    HBox RecordButtonsBox;
    @FXML
    ListView<String> RecordsList;
    @FXML
    TextField StuNumField, NameField, PhoneField, EmailField;
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
            try {
                openDatabase(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void CreateNewFile() {
        RecordInfoPane.setDisable(false);
        RecordButtonsBox.setDisable(false);
        RecordButtonsBox.setVisible(true);

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

    @FXML
    public void chooseItemInList() {
        System.out.println(RecordsList.getSelectionModel().getSelectedIndex());
        previewRecord(AllRecords.get(RecordsList.getSelectionModel().getSelectedIndex()));
    }

    //************************************
    //***       SERVING METHODS        ***
    //************************************
    private void openDatabase(File file) throws FileNotFoundException {
        System.out.println("hello " + file.getName());

        String tempStuNum = "", tempName = "", tempPhone = "", tempEmail = "";
        Scanner filescanner = new Scanner(file);
        //saving database info into AllRecords ArrayList
        while (filescanner.hasNextLine()) {
            String str = filescanner.nextLine();
            Scanner linescanner = new Scanner(str);
            linescanner.useDelimiter("[|]");
            while (linescanner.hasNext()) {
                tempStuNum = linescanner.next();
                tempName = linescanner.next();
                tempPhone = linescanner.next();
                tempEmail = linescanner.next();
            }
            AllRecords.add(new Record(tempStuNum, tempName, tempPhone, tempEmail));
            linescanner.close();
        }
        filescanner.close();

        int i = 1;
        for (Record x : AllRecords)
            RecordsList.getItems().add(i++ + ") " + x.getStuNum());
    }


    private void saveToFile(List<Record> content, File file) {
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
        AllRecords.add(new Record(StuNumField.getText(), NameField.getText(), PhoneField.getText(), EmailField.getText()));
    }

    private void previewRecord(Record one) {
        RecordInfoPane.setDisable(false);

        StuNumField.setText(one.getStuNum());
        NameField.setText(one.getName());
        PhoneField.setText(one.getPhone());
        EmailField.setText(one.getEmail());
    }
}
