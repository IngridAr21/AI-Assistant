package com.example.project_2_2_group_5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class TextFileController implements Initializable {
    @FXML
    private TextArea textField;
    @FXML
    private ImageView returnButton;
    @FXML
    private Button DoneButton;

    //Initialization method when this page is called
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setToDoField();
    }

    //Change the text in text box to what's being read in csv file
    private void setToDoField(){
        textField.setEditable(true);
        textField.setText(CsvIO.readFile("src/main/python/textfile.csv"));

    }

    //Method to return to previous screen via return button
    @FXML
    private void handleReturnButton() throws IOException {

        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml-files/main-page.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    //Button Logic to edit and save text field
    @FXML
    private void handleDoneButton(){
        if (DoneButton.getText().equals("Done")) {
            if (textField.getText().isEmpty()) {
                displayWarning("Please write something in the text field.");
                return;
            } else {
                CsvIO.writeToFile(textField.getText(), "src/main/python/textfile.csv");
            }
        } else {
            textField.clear();
            CsvIO.clearFile( "src/main/python/textfile.csv");
            CsvIO.clearFile( "src/main/python/summary.csv");
            CsvIO.clearFile( "src/main/python/todo.csv");
        }

        if (DoneButton.getText().equals("Done")) {
            DoneButton.setText("Delete");
            DoneButton.setStyle("-fx-background-color: #f94449;");
            textField.setEditable(false);
        } else {
            textField.clear();
            DoneButton.setText("Done");
            DoneButton.setStyle("-fx-background-color: #6488ea;");
            textField.setEditable(true);
        }

    }



    //Displays Warning when Text Field is empty
    private void displayWarning(String warningText){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(warningText);
        alert.showAndWait();

    }

}
