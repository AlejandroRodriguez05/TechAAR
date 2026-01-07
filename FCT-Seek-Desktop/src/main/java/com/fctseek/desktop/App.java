package com.fctseek.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        // Cargar la pantalla de login
        scene = new Scene(loadFXML("view/login"), 500, 650);
        
        // Configurar la ventana principal
        stage.setTitle("FCT-Seek Desktop");
        stage.setScene(scene);
        stage.setResizable(false); // No permitir redimensionar el login
        stage.show();
    }

    /**
     * Cambiar a otra vista (pantalla)
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        
        // Ajustar tamaño de ventana segun la pantalla
        if (fxml.contains("dashboard") || fxml.contains("empresas")) {
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
        }
    }

    /**
     * Cargar un archivo FXML
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}