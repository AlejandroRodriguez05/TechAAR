package com.practicas.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona la navegación entre vistas FXML.
 */
public class ViewManager {

    private static Stage primaryStage;
    private static final Map<String, Object> params = new HashMap<>();

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getStage() {
        return primaryStage;
    }

    /**
     * Navega a una vista FXML por nombre (sin extensión).
     * Ejemplo: navigateTo("login") carga /fxml/login.fxml
     */
    public static void navigateTo(String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ViewManager.class.getResource("/fxml/" + viewName + ".fxml"));
            Parent root = loader.load();

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            // Adaptar tamaño al contenido preferido del FXML
            primaryStage.sizeToScene();
        } catch (IOException e) {
            System.err.println("Error cargando vista '" + viewName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navega pasando un parámetro con clave.
     */
    public static void navigateTo(String viewName, String key, Object value) {
        params.put(key, value);
        navigateTo(viewName);
    }

    /**
     * Recupera un parámetro pasado en la navegación.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getParam(String key) {
        return (T) params.get(key);
    }

    /**
     * Limpia un parámetro tras usarlo.
     */
    public static void clearParam(String key) {
        params.remove(key);
    }
}
