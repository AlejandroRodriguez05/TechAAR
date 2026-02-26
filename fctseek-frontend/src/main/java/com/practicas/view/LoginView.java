package com.practicas.view;

import com.practicas.model.DataService;
import com.practicas.model.TipoUsuario;
import com.practicas.model.Usuario;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Vista de inicio de sesión con selección de rol (Alumno/Profesor)
 */
public class LoginView {
    
    private Stage stage;
    private DataService dataService;
    private TipoUsuario tipoSeleccionado;
    
    // Componentes de la UI
    private Button btnAlumno;
    private Button btnProfesor;
    private TextField txtUsername;
    private PasswordField txtPassword;
    private Label lblError;
    private VBox loginForm;

    public LoginView(Stage stage) {
        this.stage = stage;
        this.dataService = DataService.getInstance();
    }

    public Scene crearEscena() {
        // Contenedor principal
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #4facfe 0%, #00f2fe 100%);");

        // Título
        Label titulo = new Label("Sistema de Gestión de Prácticas");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.WHITE);

        Label subtitulo = new Label("Selecciona tu tipo de usuario");
        subtitulo.setFont(Font.font("Arial", 16));
        subtitulo.setTextFill(Color.WHITE);

        // Contenedor de botones de selección
        HBox botonesRol = new HBox(30);
        botonesRol.setAlignment(Pos.CENTER);

        btnAlumno = crearBotonRol("👨‍🎓 ALUMNO", "#4CAF50");
        btnProfesor = crearBotonRol("👨‍🏫 PROFESOR", "#2196F3");

        btnAlumno.setOnAction(e -> seleccionarRol(TipoUsuario.ALUMNO));
        btnProfesor.setOnAction(e -> seleccionarRol(TipoUsuario.PROFESOR));

        botonesRol.getChildren().addAll(btnAlumno, btnProfesor);

        // Formulario de login (inicialmente oculto)
        loginForm = crearFormularioLogin();
        loginForm.setVisible(false);
        loginForm.setManaged(false);

        root.getChildren().addAll(titulo, subtitulo, botonesRol, loginForm);

        return new Scene(root, 1200, 800);
    }

    private Button crearBotonRol(String texto, String color) {
        Button btn = new Button(texto);
        btn.setPrefSize(180, 80);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        // Efectos hover
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: derive(" + color + ", -20%);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        ));
        
        return btn;
    }

    private VBox crearFormularioLogin() {
        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));
        form.setMaxWidth(350);
        form.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);"
        );

        Label lblTitulo = new Label("Iniciar Sesión");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.web("#333"));

        // Campo usuario
        Label lblUser = new Label("Usuario:");
        lblUser.setFont(Font.font("Arial", 14));
        txtUsername = new TextField();
        txtUsername.setPromptText("Ingresa tu usuario");
        txtUsername.setPrefHeight(40);
        txtUsername.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #ccc;");

        // Campo contraseña
        Label lblPass = new Label("Contraseña:");
        lblPass.setFont(Font.font("Arial", 14));
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Ingresa tu contraseña");
        txtPassword.setPrefHeight(40);
        txtPassword.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #ccc;");

        // Label de error
        lblError = new Label();
        lblError.setTextFill(Color.RED);
        lblError.setFont(Font.font("Arial", 12));

        // Botones
        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);

        Button btnLogin = new Button("Entrar");
        btnLogin.setPrefSize(120, 40);
        btnLogin.setStyle(
            "-fx-background-color: #4facfe;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        btnLogin.setOnAction(e -> intentarLogin());

        Button btnVolver = new Button("Volver");
        btnVolver.setPrefSize(120, 40);
        btnVolver.setStyle(
            "-fx-background-color: #95a5a6;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        btnVolver.setOnAction(e -> ocultarFormulario());

        botones.getChildren().addAll(btnLogin, btnVolver);

        // Info de usuarios de prueba
        Label lblInfo = new Label("Usuarios de prueba:");
        lblInfo.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblInfo.setTextFill(Color.GRAY);
        
        Label lblInfoAlumno = new Label("Alumno: alumno1 / 1234");
        lblInfoAlumno.setFont(Font.font("Arial", 10));
        lblInfoAlumno.setTextFill(Color.GRAY);
        
        Label lblInfoProfe = new Label("Profesor: profesor1 / admin");
        lblInfoProfe.setFont(Font.font("Arial", 10));
        lblInfoProfe.setTextFill(Color.GRAY);

        form.getChildren().addAll(
            lblTitulo, 
            lblUser, txtUsername, 
            lblPass, txtPassword, 
            lblError,
            botones,
            new Separator(),
            lblInfo, lblInfoAlumno, lblInfoProfe
        );

        // Enter para login
        txtPassword.setOnAction(e -> intentarLogin());

        return form;
    }

    private void seleccionarRol(TipoUsuario tipo) {
        this.tipoSeleccionado = tipo;
        
        // Resaltar botón seleccionado
        if (tipo == TipoUsuario.ALUMNO) {
            btnAlumno.setStyle(
                "-fx-background-color: #2E7D32;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;"
            );
            btnProfesor.setStyle(
                "-fx-background-color: #2196F3;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;"
            );
        } else {
            btnProfesor.setStyle(
                "-fx-background-color: #1565C0;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;"
            );
            btnAlumno.setStyle(
                "-fx-background-color: #4CAF50;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;"
            );
        }

        // Mostrar formulario
        loginForm.setVisible(true);
        loginForm.setManaged(true);
        txtUsername.clear();
        txtPassword.clear();
        lblError.setText("");
        txtUsername.requestFocus();
    }

    private void ocultarFormulario() {
        loginForm.setVisible(false);
        loginForm.setManaged(false);
        tipoSeleccionado = null;
        
        // Resetear estilos de botones
        btnAlumno.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;"
        );
        btnProfesor.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;"
        );
    }

    private void intentarLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor, completa todos los campos");
            return;
        }

        Usuario usuario = dataService.autenticar(username, password, tipoSeleccionado);

        if (usuario != null) {
            // Login exitoso - ir a la vista principal
            EmpresasView empresasView = new EmpresasView(stage, usuario);
            stage.setScene(empresasView.crearEscena());
            stage.setTitle("Gestión de Prácticas - " + usuario.getNombreCompleto());
        } else {
            lblError.setText("Credenciales incorrectas o tipo de usuario no coincide");
            txtPassword.clear();
        }
    }
}