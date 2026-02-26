module com.practicas {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens com.practicas to javafx.fxml;
    opens com.practicas.controller to javafx.fxml;
    opens com.practicas.model to javafx.fxml;
    
    exports com.practicas;
    exports com.practicas.controller;
    exports com.practicas.model;
}
