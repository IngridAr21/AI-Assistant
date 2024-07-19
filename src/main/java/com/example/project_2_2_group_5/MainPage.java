package com.example.project_2_2_group_5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPage extends Application {
    private static boolean filesCleared = false;

    @Override
    //Launch Application Method
    public void start(Stage stage) throws IOException {
        //Make sure all csv files are cleared before starting the application
        if(!filesCleared){
            CsvIO.clearFile("src/main/python/summary.csv");
            CsvIO.clearFile("src/main/python/todo.csv");
            CsvIO.clearFile("src/main/python/textfile.csv");
            filesCleared = true;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainPage.class.getResource("fxml-files/main-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Main Page!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}