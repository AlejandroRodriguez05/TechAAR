package com.practicas.view;

import com.practicas.model.DataService;
import com.practicas.model.Usuario;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
    
    // Campos editables
    private TextField txtEmail;
    private TextField txtPais;
    private TextField txtDni;
    private TextField txtTelefono;
    
    // Botones
    private Button btnEditar;
    private Button btnGuardar;
    private Button btnCancelar;
    
    private boolean modoEdicion = false;

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
        
        return new Scene(root, 1200, 800);
    }

    private VBox crearHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20, 20, 15, 20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: transparent;");
        
        // Nombre
        Label lblNombre = new Label(usuario.getNombreCompleto());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 28));
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
        
        header.getChildren().addAll(lblNombre, lblRol);
        return header;
    }

    private VBox crearContenido() {
        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(20, 50, 20, 50));
        contenido.setAlignment(Pos.TOP_CENTER);
        contenido.setStyle("-fx-background-color: transparent;");
        
        // Tarjeta de información
        VBox tarjeta = new VBox(0);
        tarjeta.setMaxWidth(600);
        tarjeta.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"
        );
        
        // Título con botón editar
        HBox headerTarjeta = new HBox();
        headerTarjeta.setAlignment(Pos.CENTER_LEFT);
        headerTarjeta.setPadding(new Insets(15, 20, 15, 20));
        
        Label lblTitulo = new Label("Informacion Personal");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblTitulo.setTextFill(Color.BLACK);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Botón Editar
        btnEditar = new Button("Editar");
        btnEditar.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        btnEditar.setPadding(new Insets(8, 20, 8, 20));
        btnEditar.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;"
        );
        btnEditar.setOnAction(e -> activarModoEdicion());
        
        // Botón Guardar (oculto inicialmente)
        btnGuardar = new Button("Guardar");
        btnGuardar.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        btnGuardar.setPadding(new Insets(8, 20, 8, 20));
        btnGuardar.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;"
        );
        btnGuardar.setOnAction(e -> guardarCambios());
        btnGuardar.setVisible(false);
        btnGuardar.setManaged(false);
        
        // Botón Cancelar (oculto inicialmente)
        btnCancelar = new Button("Cancelar");
        btnCancelar.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        btnCancelar.setPadding(new Insets(8, 20, 8, 20));
        btnCancelar.setStyle(
            "-fx-background-color: #9E9E9E;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;"
        );
        btnCancelar.setOnAction(e -> cancelarEdicion());
        btnCancelar.setVisible(false);
        btnCancelar.setManaged(false);
        
        HBox botonesEdicion = new HBox(10);
        botonesEdicion.getChildren().addAll(btnEditar, btnGuardar, btnCancelar);
        
        headerTarjeta.getChildren().addAll(lblTitulo, spacer, botonesEdicion);
        
        tarjeta.getChildren().add(headerTarjeta);
        tarjeta.getChildren().add(crearSeparador());
        
        // Campos
        txtEmail = crearCampoTexto(usuario.getEmail());
        tarjeta.getChildren().add(crearFilaCampo("Gmail", txtEmail));
        tarjeta.getChildren().add(crearSeparador());
        
        txtPais = crearCampoTexto(usuario.getPais());
        tarjeta.getChildren().add(crearFilaCampo("Pais", txtPais));
        tarjeta.getChildren().add(crearSeparador());
        
        txtDni = crearCampoTexto(usuario.getDni());
        tarjeta.getChildren().add(crearFilaCampo("DNI", txtDni));
        tarjeta.getChildren().add(crearSeparador());
        
        txtTelefono = crearCampoTexto(usuario.getTelefono());
        tarjeta.getChildren().add(crearFilaCampo("Telefono", txtTelefono));
        
        contenido.getChildren().add(tarjeta);
        
        // Botón cerrar sesión
        Button btnCerrarSesion = new Button("Cerrar Sesion");
        btnCerrarSesion.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btnCerrarSesion.setPrefWidth(200);
        btnCerrarSesion.setPadding(new Insets(12, 30, 12, 30));
        btnCerrarSesion.setStyle(
            "-fx-background-color: #FF5252;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;"
        );
        btnCerrarSesion.setOnAction(e -> cerrarSesion());
        
        VBox contenedorBoton = new VBox(btnCerrarSesion);
        contenedorBoton.setAlignment(Pos.CENTER);
        contenedorBoton.setPadding(new Insets(20, 0, 0, 0));
        
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

    private TextField crearCampoTexto(String valor) {
        TextField txt = new TextField(valor != null ? valor : "");
        txt.setFont(Font.font("Arial", 14));
        txt.setPrefWidth(350);
        txt.setPrefHeight(35);
        txt.setEditable(false);
        txt.setStyle(
            "-fx-background-color: #F5F5F5;" +
            "-fx-border-color: transparent;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 5 10;" +
            "-fx-text-fill: #333333;"
        );
        return txt;
    }

    private HBox crearFilaCampo(String etiqueta, TextField textField) {
        HBox fila = new HBox(20);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(15, 20, 15, 20));
        
        Label lbl = new Label(etiqueta);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lbl.setTextFill(Color.BLACK);
        lbl.setPrefWidth(100);
        
        fila.getChildren().addAll(lbl, textField);
        
        return fila;
    }

    private void activarModoEdicion() {
        modoEdicion = true;
        
        // Mostrar botones guardar/cancelar, ocultar editar
        btnEditar.setVisible(false);
        btnEditar.setManaged(false);
        btnGuardar.setVisible(true);
        btnGuardar.setManaged(true);
        btnCancelar.setVisible(true);
        btnCancelar.setManaged(true);
        
        // Habilitar campos
        String estiloEditable = 
            "-fx-background-color: white;" +
            "-fx-border-color: #4CAF50;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 5 10;" +
            "-fx-text-fill: #000000;";
        
        txtEmail.setEditable(true);
        txtEmail.setStyle(estiloEditable);
        
        txtPais.setEditable(true);
        txtPais.setStyle(estiloEditable);
        
        txtDni.setEditable(true);
        txtDni.setStyle(estiloEditable);
        
        txtTelefono.setEditable(true);
        txtTelefono.setStyle(estiloEditable);
    }

    private void cancelarEdicion() {
        modoEdicion = false;
        
        // Restaurar valores originales
        txtEmail.setText(usuario.getEmail());
        txtPais.setText(usuario.getPais());
        txtDni.setText(usuario.getDni());
        txtTelefono.setText(usuario.getTelefono());
        
        desactivarModoEdicion();
    }

    private void guardarCambios() {
        usuario.setEmail(txtEmail.getText().trim());
        usuario.setPais(txtPais.getText().trim());
        usuario.setDni(txtDni.getText().trim());
        usuario.setTelefono(txtTelefono.getText().trim());
        
        desactivarModoEdicion();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guardado");
        alert.setHeaderText(null);
        alert.setContentText("Los cambios se han guardado correctamente.");
        alert.showAndWait();
    }

    private void desactivarModoEdicion() {
        modoEdicion = false;
        
        // Mostrar botón editar, ocultar guardar/cancelar
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnGuardar.setVisible(false);
        btnGuardar.setManaged(false);
        btnCancelar.setVisible(false);
        btnCancelar.setManaged(false);
        
        // Deshabilitar campos
        String estiloNoEditable = 
            "-fx-background-color: #F5F5F5;" +
            "-fx-border-color: transparent;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 5 10;" +
            "-fx-text-fill: #333333;";
        
        txtEmail.setEditable(false);
        txtEmail.setStyle(estiloNoEditable);
        
        txtPais.setEditable(false);
        txtPais.setStyle(estiloNoEditable);
        
        txtDni.setEditable(false);
        txtDni.setStyle(estiloNoEditable);
        
        txtTelefono.setEditable(false);
        txtTelefono.setStyle(estiloNoEditable);
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
        
        VBox btnFCT = crearBotonNav("FCT-Seek", false);
        VBox btnBuscar = crearBotonNav("Buscar", false);
        VBox btnListas = crearBotonNav("Mis Listas", false);
        VBox btnPerfil = crearBotonNav("Mi Perfil", true);
        
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

    private VBox crearBotonNav(String texto, boolean activo) {
        VBox btn = new VBox(3);
        btn.setAlignment(Pos.CENTER);
        btn.setPadding(new Insets(5));
        btn.setStyle("-fx-cursor: hand;");
        
        Label lblTexto = new Label(texto);
        lblTexto.setFont(Font.font("Arial", 12));
        lblTexto.setTextFill(activo ? Color.web("#4facfe") : Color.GRAY);
        
        btn.getChildren().add(lblTexto);
        return btn;
    }

    private void volverAEmpresas() {
        stage.setScene(new EmpresasView(stage, usuario).crearEscena());
    }

    private void abrirBusqueda() {
        stage.setScene(new BusquedaView(stage, usuario).crearEscena());
    }

    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cerrar sesion?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                dataService.cerrarSesion();
                stage.setScene(new LoginView(stage).crearEscena());
            }
        });
    }
}