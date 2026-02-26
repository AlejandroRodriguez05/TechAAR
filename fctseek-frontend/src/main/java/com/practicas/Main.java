package com.practicas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicacion de Gestion de Practicas
 * 
 * Sistema con dos roles:
 * - ALUMNO: Solo puede ver empresas con plazas libres (modo lectura)
 * - PROFESOR: Puede ver todo, anadir/eliminar empresas y gestionar plazas
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Sistema de Gestion de Practicas - Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
