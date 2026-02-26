package com.practicas.controller;

import com.practicas.model.DataService;
import com.practicas.model.TipoUsuario;
import com.practicas.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private Button btnAlumno;
    @FXML private Button btnProfesor;
    @FXML private VBox loginForm;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private DataService dataService;
    private TipoUsuario tipoSeleccionado;

    @FXML
    public void initialize() {
        dataService = DataService.getInstance();
    }

    @FXML
    private void seleccionarAlumno() {
        tipoSeleccionado = TipoUsuario.ALUMNO;
        btnAlumno.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 10;");
        btnProfesor.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10;");
        mostrarFormulario();
    }

    @FXML
    private void seleccionarProfesor() {
        tipoSeleccionado = TipoUsuario.PROFESOR;
        btnProfesor.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white; -fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 10;");
        btnAlumno.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10;");
        mostrarFormulario();
    }

    private void mostrarFormulario() {
        loginForm.setVisible(true);
        loginForm.setManaged(true);
        txtUsername.clear();
        txtPassword.clear();
        lblError.setText("");
        txtUsername.requestFocus();
    }

    @FXML
    private void ocultarFormulario() {
        loginForm.setVisible(false);
        loginForm.setManaged(false);
        tipoSeleccionado = null;
        btnAlumno.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10;");
        btnProfesor.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10;");
    }

    @FXML
    private void intentarLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor, completa todos los campos");
            return;
        }

        Usuario usuario = dataService.autenticar(username, password, tipoSeleccionado);

        if (usuario != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/empresas.fxml"));
                Parent root = loader.load();
                
                EmpresasController controller = loader.getController();
                controller.setUsuario(usuario);
                
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Gestion de Practicas - " + usuario.getNombreCompleto());
                stage.setMaximized(true);
            } catch (IOException e) {
                e.printStackTrace();
                lblError.setText("Error al cargar la vista");
            }
        } else {
            lblError.setText("Credenciales incorrectas o tipo de usuario no coincide");
            txtPassword.clear();
        }
    }
}
