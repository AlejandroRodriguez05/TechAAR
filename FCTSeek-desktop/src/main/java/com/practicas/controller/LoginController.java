package com.practicas.controller;

import com.practicas.service.AuthService;
import com.practicas.util.ApiClient.ApiException;
import com.practicas.util.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;
    @FXML private HBox loadingIndicator;

    @FXML
    public void initialize() {
        // Enter en password → login
        txtPassword.setOnAction(e -> iniciarSesion());
    }

    @FXML
    private void iniciarSesion() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, completa todos los campos");
            return;
        }

        setLoading(true);
        ocultarError();

        new Thread(() -> {
            try {
                AuthService.login(email, password);
                Platform.runLater(() -> ViewManager.navigateTo("empresas"));
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    mostrarError(e.getMessage());
                    setLoading(false);
                });
            }
        }).start();
    }

    private void setLoading(boolean loading) {
        btnLogin.setDisable(loading);
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void ocultarError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    @FXML
    private void recuperarPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recuperar contraseña");
        alert.setHeaderText(null);
        alert.setContentText("Contacta con el administrador del centro para recuperar tu contraseña.");
        alert.showAndWait();
    }
}
