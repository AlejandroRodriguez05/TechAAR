package com.practicas;

import com.practicas.util.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        ViewManager.init(stage);
        ViewManager.navigateTo("login");
        stage.setTitle("FCT-Seek");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
