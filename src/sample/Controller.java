package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO add different messages to statuslabel
//TODO proper opening and closing sequences
//TODO fix buttons in editing and viewing mode
//TODO esc button pressed: preview mode, clear

public class Controller implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RecordInfoPane.setDisable(true);
        RecordButtonsBox.setDisable(true);
        RecordButtonsBox.setVisible(false);
        current_index = 0;
        current_file = null;
        isEditingMode = false;
        isDatabaseOpen = false;
    }

    //************************************
    //***           VARIABLES          ***
    //************************************
    private File current_file;
    private int current_index;
    private List<Record> AllRecords = new ArrayList<Record>();
    private boolean isEditingMode, isDatabaseOpen;
    @FXML
    AnchorPane RecordInfoPane;
    @FXML
    HBox RecordButtonsBox;
    @FXML
    ListView<String> RecordsList;
    @FXML
    TextField StuNumField, NameField, PhoneField, EmailField;
    @FXML
    Label StateLabel;

    //************************************
    //***           FUNCTIONS          ***
    //************************************
    @FXML
    public void OpenFile() {
        FileChooser fileChooser = new FileChooser();
        current_file = fileChooser.showOpenDialog(new Stage());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
                , new FileChooser.ExtensionFilter("Database Files", "*.rdb"));
        if (current_file != null) {
            try {
                openDatabase(current_file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void CreateNewFile() {
        //doesn't actually creates new file
        //file created on exiting
        isDatabaseOpen = true;
        current_index = 0;
        AllRecords.add(new Record());
        updateListView();
        previewRecord(AllRecords.get(0));
        editingMode();
    }

    @FXML
    public void CloseFile() {
        //TODO fix "save changes to file" prompt
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Look, a Confirmation Dialog");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
        } else {
            // ... user chose CANCEL or closed the dialog
        }


        //yes
        saveToFile(AllRecords, current_file);

        //no
        //do nothing

        //either way clear the whole screen
        cancelEditingMode();
        clearRecordPreview();
        RecordsList.getSelectionModel().clearSelection();
        AllRecords.clear(); //works faster than removeAll()
        //and clear listview
        updateListView();

        isDatabaseOpen = false;
    }

    @FXML
    public void SaveFile() {
        saveToFile(AllRecords, current_file);
    }

    @FXML
    public void SaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt")
                , new FileChooser.ExtensionFilter("Database Files", "*.rdb"));

        current_file = fileChooser.showSaveDialog(new Stage());

        if (current_file != null) {
            saveToFile(AllRecords, current_file);
        }
    }

    @FXML
    public void saveRecord() {
        //when save button is pressed
        AllRecords.get(current_index).setValues(StuNumField.getText(), NameField.getText(), PhoneField.getText(), EmailField.getText());
        //cancel editing mode
//        cancelEditingMode();
        //update listview

        RecordsList.getItems().clear();
        updateListView();

        RecordsList.getSelectionModel().clearSelection();
        current_index = -1;
    }

    @FXML
    public void chooseItemInList() {
        System.out.println(current_index);
        current_index = RecordsList.getSelectionModel().getSelectedIndex();
        previewRecord(AllRecords.get(current_index));
    }

    @FXML
    public void nextItemInList() {
        if (isDatabaseOpen) {
            RecordsList.getSelectionModel().select(++current_index);
            //add scrolling if needed
            RecordsList.scrollTo(current_index);
            previewRecord(AllRecords.get(current_index));
        }
    }

    @FXML
    public void prevItemInList() {
        if (isDatabaseOpen) {
            RecordsList.getSelectionModel().select(--current_index);
            //add scrolling if needed
            RecordsList.scrollTo(current_index);
            previewRecord(AllRecords.get(current_index));
        }
    }

    @FXML
    private void addNewRecord() {
        if (isDatabaseOpen) {
            editingMode();
            AllRecords.add(new Record());

            StateLabel.setText("Record has been created. You can edit now");
        }
    }

    @FXML
    public void cancelEditingMode() {
        //on cancel buttonPress
        RecordInfoPane.setDisable(true);
        RecordButtonsBox.setDisable(true);
        RecordButtonsBox.setVisible(false);

        isEditingMode = false;
        //clears screen. Change logic 
        /*StuNumField.clear();
        NameField.clear();
        PhoneField.clear();
        EmailField.clear();*/

        //unselect listview
        /* RecordsList.getSelectionModel().clearSelection();
   		current_index = -1;
   	    */
    }

    @FXML
    public void editingMode() {
        //on editButton press
        //assume previewRecord called somewhere else
        isEditingMode = true;

        RecordInfoPane.setDisable(false);
        RecordButtonsBox.setDisable(false);
        RecordButtonsBox.setVisible(true);
    }

    //************************************
    //***       SERVING METHODS        ***
    //************************************
    private void openDatabase(File file) throws FileNotFoundException {
//        System.out.println("hello " + file.getName());

        String tempStuNum = "", tempName = "", tempPhone = "", tempEmail = "";
        Scanner filescanner = new Scanner(file);
        //saving database info into AllRecords ArrayList
        StateLabel.setText("Database is opening");
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

        updateListView();
        StateLabel.setText("Database is opened");
        isDatabaseOpen = true;
    }


    private void saveToFile(List<Record> content, File file) {
        try {
            PrintWriter writer;
            if (file != null) {
                writer = new PrintWriter(file);
            } else {
                SaveAs();
                return;
            }
            StateLabel.setText("Saving changes to the database");
            for (Record x : content)
                writer.println(x.toString());
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void previewRecord(Record one) {
        //RecordInfoPane.setDisable(false);
        StuNumField.setText(one.getStuNum());
        NameField.setText(one.getName());
        PhoneField.setText(one.getPhone());
        EmailField.setText(one.getEmail());
    }

    private void clearRecordPreview() {
        StuNumField.clear();
        NameField.clear();
        PhoneField.clear();
        EmailField.clear();
    }

    private void updateListView() {
        int i = 1;
        for (Record x : AllRecords)
            RecordsList.getItems().add(i++ + ") " + x.getStuNum());
    }
}