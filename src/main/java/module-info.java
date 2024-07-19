module com.example.project_22_group5 {
    requires javafx.controls;
    requires javafx.fxml;
            
            requires com.dlsc.formsfx;
                        
    opens com.example.project_2_2_group_5 to javafx.fxml;
    exports com.example.project_2_2_group_5;
}