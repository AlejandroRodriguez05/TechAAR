package com.practicas.controller;

import com.practicas.model.DataService;
import com.practicas.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class PerfilController {

    @FXML private Label lblNombre;
    @FXML private Label lblRol;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPais;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Usuario usuario;
    private DataService dataService;

    @FXML
    public void initialize() {
        dataService = DataService.getInstance();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        actualizarVista();
    }

    private void actualizarVista() {
        lblNombre.setText(usuario.getNombreCompleto());
        lblRol.setText(usuario.esProfesor() ? "Profesor" : "Alumno");

        txtEmail.setText(usuario.getEmail());
        txtPais.setText(usuario.getPais());
        txtDni.setText(usuario.getDni());
        txtTelefono.setText(usuario.getTelefono());
    }

    @FXML
    private void activarEdicion() {
        btnEditar.setVisible(false);
        btnEditar.setManaged(false);
        btnGuardar.setVisible(true);
        btnGuardar.setManaged(true);
        btnCancelar.setVisible(true);
        btnCancelar.setManaged(true);

        String estiloEditable = "-fx-background-color: white; -fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 10; -fx-text-fill: #000000; -fx-font-size: 14px;";

        txtEmail.setEditable(true);
        txtEmail.setStyle(estiloEditable);

        txtPais.setEditable(true);
        txtPais.setStyle(estiloEditable);

        txtDni.setEditable(true);
        txtDni.setStyle(estiloEditable);

        txtTelefono.setEditable(true);
        txtTelefono.setStyle(estiloEditable);
    }

    @FXML
    private void cancelarEdicion() {
        txtEmail.setText(usuario.getEmail());
        txtPais.setText(usuario.getPais());
        txtDni.setText(usuario.getDni());
        txtTelefono.setText(usuario.getTelefono());

        desactivarEdicion();
    }

    @FXML
    private void guardarCambios() {
        usuario.setEmail(txtEmail.getText().trim());
        usuario.setPais(txtPais.getText().trim());
        usuario.setDni(txtDni.getText().trim());
        usuario.setTelefono(txtTelefono.getText().trim());

        desactivarEdicion();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guardado");
        alert.setHeaderText(null);
        alert.setContentText("Los cambios se han guardado correctamente.");
        alert.showAndWait();
    }

    private void desactivarEdicion() {
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnGuardar.setVisible(false);
        btnGuardar.setManaged(false);
        btnCancelar.setVisible(false);
        btnCancelar.setManaged(false);

        String estiloNoEditable = "-fx-background-color: #F5F5F5; -fx-border-color: transparent; -fx-background-radius: 5; -fx-padding: 5 10; -fx-text-fill: #333333; -fx-font-size: 14px;";

        txtEmail.setEditable(false);
        txtEmail.setStyle(estiloNoEditable);

        txtPais.setEditable(false);
        txtPais.setStyle(estiloNoEditable);

        txtDni.setEditable(false);
        txtDni.setStyle(estiloNoEditable);

        txtTelefono.setEditable(false);
        txtTelefono.setStyle(estiloNoEditable);
    }

    @FXML
    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cerrar sesion?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                dataService.cerrarSesion();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) lblNombre.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Sistema de Gestion de Practicas - Login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void volverAEmpresas() {
        cambiarVista("/fxml/empresas.fxml", controller -> ((EmpresasController) controller).setUsuario(usuario));
    }

    @FXML
    private void abrirBusqueda() {
        cambiarVista("/fxml/busqueda.fxml", controller -> ((BusquedaController) controller).setUsuario(usuario));
    }

    private void cambiarVista(String fxmlPath, java.util.function.Consumer<Object> setupController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            setupController.accept(loader.getController());
            Stage stage = (Stage) lblNombre.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
