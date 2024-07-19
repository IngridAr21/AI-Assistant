package com.example.project_2_2_group_5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ToDoPageController implements Initializable {
    @FXML
    private TextArea todoField;
    @FXML
    private ImageView returnButton;

    //Initialization method when this page is called
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setToDoField();
    }

    //Change the text in text box to what's being read in csv file
    private void setToDoField(){
        todoField.setText(CsvIO.readFile("src/main/python/todo.csv"));
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
}
