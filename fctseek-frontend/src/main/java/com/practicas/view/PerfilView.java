package com.practicas.view;

import com.practicas.model.DataService;
import com.practicas.model.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Vista de perfil del usuario
 */
public class PerfilView {

    private Stage stage;
    private Usuario usuario;
    private DataService dataService;

    public PerfilView(Stage stage, Usuario usuario) {
        this.stage = stage;
        this.usuario = usuario;
        this.dataService = DataService.getInstance();
    }

    public Scene crearEscena() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #4facfe 0%, #00f2fe 50%, #87CEEB 100%);");
        
        root.setTop(crearHeader());
        root.setCenter(crearContenido());
        root.setBottom(crearNavegacionInferior());
        
        return new Scene(root, 420, 780);
    }

    private VBox crearHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(30, 20, 20, 20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: transparent;");
        
        // Avatar
        Label avatar = new Label("P");
        avatar.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        avatar.setTextFill(Color.web("#4facfe"));
        avatar.setPadding(new Insets(25, 35, 25, 35));
        avatar.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 50;"
        );
        
        // Nombre
        Label lblNombre = new Label(usuario.getNombreCompleto());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblNombre.setTextFill(Color.WHITE);
        
        // Rol
        String rol = usuario.esProfesor() ? "Profesor" : "Alumno";
        Label lblRol = new Label(rol);
        lblRol.setFont(Font.font("Arial", 14));
        lblRol.setTextFill(Color.WHITE);
        lblRol.setPadding(new Insets(5, 15, 5, 15));
        lblRol.setStyle(
            "-fx-background-color: rgba(255,255,255,0.3);" +
            "-fx-background-radius: 15;"
        );
        
        header.getChildren().addAll(avatar, lblNombre, lblRol);
        return header;
    }

    private VBox crearContenido() {
        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: transparent;");
        
        // Tarjeta de información
        VBox tarjeta = new VBox(0);
        tarjeta.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"
        );
        
        // Título
        Label lblTitulo = new Label("Información Personal");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblTitulo.setPadding(new Insets(15, 20, 10, 20));
        
        tarjeta.getChildren().add(lblTitulo);
        tarjeta.getChildren().add(crearSeparador());
        
        // Campos de información
        tarjeta.getChildren().add(crearCampoInfo("Gmail", usuario.getEmail()));
        tarjeta.getChildren().add(crearSeparador());
        
        tarjeta.getChildren().add(crearCampoInfo("País", usuario.getPais()));
        tarjeta.getChildren().add(crearSeparador());
        
        tarjeta.getChildren().add(crearCampoInfo("DNI", usuario.getDni()));
        tarjeta.getChildren().add(crearSeparador());
        
        tarjeta.getChildren().add(crearCampoInfo("Teléfono", usuario.getTelefono()));
        
        contenido.getChildren().add(tarjeta);
        
        // Botón cerrar sesión
        Button btnCerrarSesion = new Button("Cerrar Sesión");
        btnCerrarSesion.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btnCerrarSesion.setPrefWidth(200);
        btnCerrarSesion.setPadding(new Insets(12, 30, 12, 30));
        btnCerrarSesion.setStyle(
            "-fx-background-color: #FF5252;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;"
        );
        btnCerrarSesion.setOnMouseEntered(e -> btnCerrarSesion.setStyle(
            "-fx-background-color: #FF1744;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;"
        ));
        btnCerrarSesion.setOnMouseExited(e -> btnCerrarSesion.setStyle(
            "-fx-background-color: #FF5252;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;"
        ));
        btnCerrarSesion.setOnAction(e -> cerrarSesion());
        
        VBox contenedorBoton = new VBox(btnCerrarSesion);
        contenedorBoton.setAlignment(Pos.CENTER);
        contenedorBoton.setPadding(new Insets(30, 0, 0, 0));
        
        contenido.getChildren().add(contenedorBoton);
        
        // Scroll
        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        VBox wrapper = new VBox(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        wrapper.setStyle("-fx-background-color: transparent;");
        
        return wrapper;
    }

    private HBox crearCampoInfo(String etiqueta, String valor) {
        HBox campo = new HBox(15);
        campo.setAlignment(Pos.CENTER_LEFT);
        campo.setPadding(new Insets(15, 20, 15, 20));
        
        VBox info = new VBox(2);
        
        Label lblEtiqueta = new Label(etiqueta);
        lblEtiqueta.setFont(Font.font("Arial", 12));
        lblEtiqueta.setTextFill(Color.GRAY);
        
        Label lblValor = new Label(valor != null && !valor.isEmpty() ? valor : "No especificado");
        lblValor.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
        lblValor.setTextFill(Color.BLACK);
        
        info.getChildren().addAll(lblEtiqueta, lblValor);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        campo.getChildren().addAll(info, spacer);
        
        // Hover
        campo.setOnMouseEntered(e -> campo.setStyle("-fx-background-color: #F5F5F5; -fx-cursor: hand;"));
        campo.setOnMouseExited(e -> campo.setStyle("-fx-background-color: transparent;"));
        
        return campo;
    }

    private Region crearSeparador() {
        Region separador = new Region();
        separador.setPrefHeight(1);
        separador.setStyle("-fx-background-color: #EEEEEE;");
        return separador;
    }

    private HBox crearNavegacionInferior() {
        HBox nav = new HBox();
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(12, 10, 18, 10));
        nav.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20 20 0 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, -2);"
        );
        
        VBox btnFCT = crearBotonNav("📋", "FCT-Seek", false);
        VBox btnBuscar = crearBotonNav("🔍", "Buscar Empre...", false);
        VBox btnListas = crearBotonNav("📝", "Mis Listas", false);
        VBox btnPerfil = crearBotonNav("👤", "Mi Perfil", true);
        
        // Navegación
        btnFCT.setOnMouseClicked(e -> volverAEmpresas());
        btnBuscar.setOnMouseClicked(e -> abrirBusqueda());
        
        HBox.setHgrow(btnFCT, Priority.ALWAYS);
        HBox.setHgrow(btnBuscar, Priority.ALWAYS);
        HBox.setHgrow(btnListas, Priority.ALWAYS);
        HBox.setHgrow(btnPerfil, Priority.ALWAYS);
        
        nav.getChildren().addAll(btnFCT, btnBuscar, btnListas, btnPerfil);
        return nav;
    }

    private VBox crearBotonNav(String icono, String texto, boolean activo) {
        VBox btn = new VBox(3);
        btn.setAlignment(Pos.CENTER);
        btn.setPadding(new Insets(5));
        btn.setStyle("-fx-cursor: hand;");
        
        Label lblIcono = new Label(icono);
        lblIcono.setFont(Font.font("Arial", 20));
        
        Label lblTexto = new Label(texto);
        lblTexto.setFont(Font.font("Arial", 10));
        lblTexto.setTextFill(activo ? Color.web("#4facfe") : Color.GRAY);
        
        btn.getChildren().addAll(lblIcono, lblTexto);
        return btn;
    }

    private void volverAEmpresas() {
        stage.setScene(new EmpresasView(stage, usuario).crearEscena());
    }

    private void abrirBusqueda() {
        stage.setScene(new BusquedaView(stage, usuario).crearEscena());
    }

    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Cerrar sesión?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                dataService.cerrarSesion();
                stage.setScene(new LoginView(stage).crearEscena());
            }
        });
    }
}