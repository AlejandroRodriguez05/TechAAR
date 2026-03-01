module com.practicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;

    opens com.practicas to javafx.fxml;
    opens com.practicas.controller to javafx.fxml;
    opens com.practicas.model to com.google.gson, javafx.base;

    exports com.practicas;
    exports com.practicas.controller;
    exports com.practicas.model;
    exports com.practicas.service;
    exports com.practicas.util;
}
