package com.example.project_2_2_group_5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    @FXML
    private ImageView exitButton;
    @FXML
    private Button creditsButton;
    @FXML
    private ImageView returnButton;
    @FXML
    private ImageView guideButton;
    @FXML
    private Button inputButton;
    @FXML
    private ChoiceBox<String> summaryTypes = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> todoTypes = new ChoiceBox<>();
    @FXML
    private Button summaryButton;
    @FXML
    private Button todoListButton;
    @FXML
    private Button textFileButton;
    @FXML
    private TextArea textField;
    private Process pythonProcess;
    private final String[] summaries = {"Extractive", "Abstractive", "Rule Based"};
    private final String[] todolists = {"Simple Rule Based", "Complex Rule Based"};
    private static File file;


    //Initialization method when this page is called
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        summaryTypes.setValue("Choose Summary!");
        summaryTypes.getItems().addAll(summaries);
        todoTypes.setValue("Choose To-do List!");
        todoTypes.getItems().addAll(todolists);
    }

    //Exit Button to quit application
    @FXML
    private void handleExitButton(MouseEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    //Credits Button to display contributors scene
    @FXML
    private void handleCreditsButton(MouseEvent event) throws IOException {
        Stage stage = (Stage) creditsButton.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(MainPage.class.getResource("fxml-files/credits-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Credits Page!");
        stage.setScene(scene);
        stage.show();
    }

    //Guide Button to display guide scene
    @FXML
    private void handleGuideButton(MouseEvent event) throws IOException {
        Stage stage = (Stage) guideButton.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(MainPage.class.getResource("fxml-files/guide-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        stage.setTitle("Guide Page!");
        stage.setScene(scene);
        stage.show();
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

    //Button where user can input a file
    @FXML
    private void handleInputButton() throws IOException {
        // Opens file explorer where user can browse his files
        CsvIO.clearFile("src/main/python/textfile.csv");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Audio File");

        // Types of files that are allowed
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4 files (*.mp4)", "*.mp4"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("WAV files (*.wav)", "*.wav"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("FLAC files (*.flac)", "*.flac"));

        // Get the file which was inputted
        Stage stage = (Stage) inputButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            // Endpoint URL of your Flask API
            String apiUrl = "http://localhost:5000/speech";

            // Create HTTP connection
            HttpURLConnection con = null;
            try {
                URL url = new URL(apiUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                // Send data
                String jsonInputString = String.format("{\"file_path\": \"%s\", \"mode\": \"%s\"}",
                        file.getAbsolutePath().replace("\\", "\\\\"), "2");

                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Read response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Response from API: " + response.toString());
                }
            } finally {
                //Disconnect connection if it is still online and file is not null
                if (con != null) {
                    con.disconnect();
                }
            }
        }
    }

    //Opens Summary Page
    @FXML
    private void handleSummaryButton() throws IOException {
        CsvIO.clearFile("src/main/python/summary.csv");

//        if(file==null){
//            displayWarning("File not chosen");
//            return;
//        }
//
        //Check if file is empty
        if(CsvIO.isFileEmpty("src/main/python/textfile.csv")){
            displayWarning("No Text File/ No Dialogue");
            return;
        }
        else if(summaryTypes.getValue().equals("Abstractive")){
            // Endpoint URL of your Flask API
            String apiUrl = "http://localhost:5000/summary";

            // Create HTTP connection
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Send data
            String jsonInputString = "{}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response from API: " + response.toString());
            }

            // Close connection
            con.disconnect();
        } else if(summaryTypes.getValue().equals("Extractive")) {

            String apiUrl = "http://localhost:5000/extractive";

            // Create HTTP connection
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Send data
            String jsonInputString = "{}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response from API: " + response.toString());
            }

            // Close connection
        } else if (summaryTypes.getValue().equals("Rule Based")) {
            String text = CsvIO.readFile("src/main/python/textfile.csv");
            RuleBased.summarize(text);
        } else{
            displayWarning("Summary type not chosen");
            return;
        }


        Stage stage = (Stage) summaryButton.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(MainPage.class.getResource("fxml-files/summary-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.setTitle("Summary Page!");
        stage.setScene(scene);
        stage.show();

    }

    //Opens To-Do Page
    @FXML
    private void handleTodoButton() throws IOException {
        CsvIO.clearFile("src/main/python/todo.csv");

        //Check if file is empty
        if(CsvIO.isFileEmpty("src/main/python/summary.csv")){
            displayWarning("No Summary/ Create a Summary first");
            return;
        }

        if(todoTypes.getValue().equals("Choose Todo")){
            displayWarning("Todo type not chosen");
            return;
        }

        if(todoTypes.getValue().equals("Complex Rule Based")){
            // Endpoint URL of your Flask API
            String apiUrl = "http://localhost:5000/todo";

            // Create HTTP connection
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Send data
            String jsonInputString = "{}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response from API: " + response.toString());
            }

            // Close connection
            con.disconnect();
        } else if (todoTypes.getValue().equals("Simple Rule Based")) {
            // Endpoint URL of your Flask API
            String apiUrl = "http://localhost:5000/rulebased";

            // Create HTTP connection
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Send data
            String jsonInputString = "{}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response from API: " + response.toString());
            }

            // Close connection
            con.disconnect();
        }


        Stage stage = (Stage) todoListButton.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(MainPage.class.getResource("fxml-files/todo-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.setTitle("To-Do Page!");
        stage.setScene(scene);
        stage.show();

    }

    //Opens Text File Page
    @FXML
    private void handleTextFieldButton() throws IOException {

        Stage stage = (Stage) textFileButton.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(MainPage.class.getResource("fxml-files/textfile-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.setTitle("Text Field Page!");
        stage.setScene(scene);
        stage.show();

    }

    //Display Warning Screen Method
    private void displayWarning(String warningText){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(warningText);
        alert.showAndWait();

    }

    //Method to get current file that was inputted
    public static File getFile(){
        if(file==null){
            return null;
        }
        return file;
    }

}