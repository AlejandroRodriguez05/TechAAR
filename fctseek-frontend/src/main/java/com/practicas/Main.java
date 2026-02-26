package com.practicas;

import com.practicas.view.LoginView;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación de Gestión de Prácticas
 * 
 * Sistema con dos roles:
 * - ALUMNO: Solo puede ver empresas con plazas libres (modo lectura)
 * - PROFESOR: Puede ver todo, añadir/eliminar empresas y gestionar plazas
 * 
 * @author Sistema de Prácticas
 * @version 1.0
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Configurar ventana principal
        primaryStage.setTitle("Sistema de Gestión de Prácticas - Login");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        
        // Crear y mostrar la vista de login
        LoginView loginView = new LoginView(primaryStage);
        primaryStage.setScene(loginView.crearEscena());
        
        // Maximizar ventana
        primaryStage.setMaximized(true);
        
        // Mostrar
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}