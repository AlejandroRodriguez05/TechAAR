module com.fctseek.desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.fctseek.desktop to javafx.fxml;
    opens com.fctseek.desktop.controller to javafx.fxml;
    opens com.fctseek.desktop.model to javafx.fxml, com.google.gson;
    exports com.fctseek.desktop;
}
