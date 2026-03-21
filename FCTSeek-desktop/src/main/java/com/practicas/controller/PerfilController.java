package com.practicas.controller;

import com.practicas.model.Usuario;
import com.practicas.service.AuthService;
import com.practicas.util.IconHelper;
import com.practicas.util.Session;
import com.practicas.util.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.util.Optional;

public class PerfilController {

    @FXML private Label lblIniciales;
    @FXML private Label lblNombreCompleto;
    @FXML private Label lblRol;
    @FXML private Label lblEmail;
    @FXML private Label lblNif;
    @FXML private Label lblCentro;
    @FXML private Label navIconHome;
    @FXML private Label navIconBuscar;
    @FXML private Label navIconListas;
    @FXML private Label navIconPerfil;

    @FXML
    public void initialize() {
        // Inicializar iconos de navegación
        navIconHome.setGraphic(IconHelper.get("ic_home.png", 22));
        navIconBuscar.setGraphic(IconHelper.get("ic_search.png", 22));
        navIconListas.setGraphic(IconHelper.get("ic_list.png", 22));
        navIconPerfil.setGraphic(IconHelper.get("ic_profile.png", 22));

        Usuario user = Session.get().getUsuario();
        if (user == null) return;

        lblIniciales.setText(user.getIniciales());
        lblNombreCompleto.setText(user.getNombreCompleto());
        lblRol.setText(user.getRol() != null ? user.getRol() : "USUARIO");
        lblEmail.setText(user.getEmail() != null ? user.getEmail() : "Sin email");
        lblNif.setText(user.getNif() != null ? user.getNif() : "Sin NIF");
        lblCentro.setText("CIFP Villa de Agüimes");
    }

    @FXML
    private void cerrarSesion() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar sesión");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Estás seguro de que quieres cerrar sesión?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            AuthService.logout();
            ViewManager.navigateTo("login");
        }
    }

    // Navegación
    @FXML private void volverAEmpresas() { ViewManager.navigateTo("empresas"); }
    @FXML private void abrirBusqueda() { ViewManager.navigateTo("busqueda"); }
    @FXML private void abrirListas() { ViewManager.navigateTo("listas"); }
}
