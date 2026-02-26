package com.practicas.view;

import com.practicas.model.DataService;
import com.practicas.model.Empresa;
import com.practicas.model.Usuario;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Vista principal estilo app móvil - diseño exacto de la referencia
 */
public class EmpresasView {

    private Stage stage;
    private Usuario usuario;
    private DataService dataService;
    private VBox listaEmpresas;

    public EmpresasView(Stage stage, Usuario usuario) {
        this.stage = stage;
        this.usuario = usuario;
        this.dataService = DataService.getInstance();
    }

    public Scene crearEscena() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #4facfe 0%, #00f2fe 50%, #87CEEB 100%);");
        
        // Header superior
        root.setTop(crearHeader());
        
        // Contenido central con scroll
        root.setCenter(crearContenido());
        
        // Navegación inferior
        root.setBottom(crearNavegacionInferior());
        
        return new Scene(root, 420, 780);
    }

    private VBox crearHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(20, 20, 10, 20));
        header.setStyle("-fx-background-color: transparent;");
        
        // Fila: Saludo + botón añadir
        HBox filaSaludo = new HBox();
        filaSaludo.setAlignment(Pos.CENTER_LEFT);
        
        VBox infoUsuario = new VBox(2);
        
        String nombre = usuario.getNombreCompleto().split(" ")[0];
        Label lblSaludo = new Label("Hola, " + nombre + " 👋");
        lblSaludo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblSaludo.setTextFill(Color.WHITE);
        
        String rol = usuario.esProfesor() ? "Profesor" : "Alumno";
        int disponibles = (int) dataService.getEmpresas().stream().filter(e -> !e.estaOcupada()).count();
        Label lblInfo = new Label(rol + " • " + disponibles + " empresas disponibles");
        lblInfo.setFont(Font.font("Arial", 13));
        lblInfo.setTextFill(Color.WHITE);
        
        infoUsuario.getChildren().addAll(lblSaludo, lblInfo);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox botones = new HBox(10);
        if (usuario.esProfesor()) {
            Button btnAdd = new Button("+");
            btnAdd.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            btnAdd.setPrefSize(40, 40);
            btnAdd.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #4facfe;" +
                "-fx-background-radius: 20;" +
                "-fx-cursor: hand;"
            );
            btnAdd.setOnAction(e -> mostrarDialogoAnadirEmpresa());
            botones.getChildren().add(btnAdd);
        }
        
        filaSaludo.getChildren().addAll(infoUsuario, spacer, botones);
        
        // Filtros: Nuevas, Top valoradas, Cercanas
        HBox filtros = new HBox(15);
        filtros.setAlignment(Pos.CENTER);
        filtros.setPadding(new Insets(15, 0, 10, 0));
        
        filtros.getChildren().addAll(
            crearBotonFiltro("☆", "Nuevas"),
            crearBotonFiltro("🏆", "Top valoradas"),
            crearBotonFiltro("📍", "Cercanas")
        );
        
        header.getChildren().addAll(filaSaludo, filtros);
        return header;
    }

    private VBox crearBotonFiltro(String icono, String texto) {
        VBox filtro = new VBox(5);
        filtro.setAlignment(Pos.CENTER);
        filtro.setPadding(new Insets(12, 20, 12, 20));
        filtro.setStyle(
            "-fx-background-color: rgba(255,255,255,0.25);" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;"
        );
        
        Label lblIcono = new Label(icono);
        lblIcono.setFont(Font.font("Arial", 18));
        
        Label lblTexto = new Label(texto);
        lblTexto.setFont(Font.font("Arial", 11));
        lblTexto.setTextFill(Color.WHITE);
        
        filtro.getChildren().addAll(lblIcono, lblTexto);
        
        filtro.setOnMouseEntered(e -> filtro.setStyle(
            "-fx-background-color: rgba(255,255,255,0.4);" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;"
        ));
        filtro.setOnMouseExited(e -> filtro.setStyle(
            "-fx-background-color: rgba(255,255,255,0.25);" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;"
        ));
        
        return filtro;
    }

    private VBox crearContenido() {
        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(10, 15, 10, 15));
        contenido.setStyle("-fx-background-color: transparent;");
        
        // Título "Todas las empresas" + badge
        HBox tituloSeccion = new HBox();
        tituloSeccion.setAlignment(Pos.CENTER_LEFT);
        tituloSeccion.setPadding(new Insets(5, 5, 10, 5));
        
        Label lblTodas = new Label("Todas las empresas");
        lblTodas.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblTodas.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label badge = new Label(String.valueOf(dataService.getEmpresas().size()));
        badge.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        badge.setTextFill(Color.web("#4facfe"));
        badge.setPadding(new Insets(4, 12, 4, 12));
        badge.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        tituloSeccion.getChildren().addAll(lblTodas, spacer, badge);
        
        // Lista de empresas
        listaEmpresas = new VBox(12);
        listaEmpresas.setStyle("-fx-background-color: transparent;");
        
        actualizarListaEmpresas();
        
        ScrollPane scroll = new ScrollPane(listaEmpresas);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        
        contenido.getChildren().addAll(tituloSeccion, scroll);
        return contenido;
    }

    private void actualizarListaEmpresas() {
        listaEmpresas.getChildren().clear();
        for (Empresa empresa : dataService.getEmpresas()) {
            listaEmpresas.getChildren().add(crearTarjetaEmpresa(empresa));
        }
    }

    private VBox crearTarjetaEmpresa(Empresa empresa) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #E0E0E0;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"
        );
        
        // === FILA 1: Nombre + Valoración ===
        HBox fila1 = new HBox();
        fila1.setAlignment(Pos.CENTER_LEFT);
        
        VBox infoNombre = new VBox(2);
        
        // Nombre de la empresa - NEGRO Y BOLD FUERTE
        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setStyle(
            "-fx-font-family: 'Arial';" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #000000;"
        );
        
        // Ubicación
        Label lblUbicacion = new Label("📍 " + empresa.getUbicacion());
        lblUbicacion.setStyle(
            "-fx-font-family: 'Arial';" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #555555;"
        );
        
        infoNombre.getChildren().addAll(lblNombre, lblUbicacion);
        
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        // Valoración
        HBox valoracion = new HBox(4);
        valoracion.setAlignment(Pos.CENTER);
        valoracion.setPadding(new Insets(5, 10, 5, 10));
        valoracion.setStyle("-fx-background-color: #FFD54F; -fx-background-radius: 15;");
        
        Label estrella = new Label("⭐");
        Label puntuacion = new Label(String.format("%.1f", 3.5 + Math.random() * 1.5));
        puntuacion.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        puntuacion.setTextFill(Color.web("#5D4037"));
        
        valoracion.getChildren().addAll(estrella, puntuacion);
        
        fila1.getChildren().addAll(infoNombre, spacer1, valoracion);
        
        // === FILA 2: Badge INF/FULL ===
        String badgeText = empresa.estaOcupada() ? "HOT" : "INF";
        String badgeColor = empresa.estaOcupada() ? "#FF5722" : "#4CAF50";
        
        Label lblBadge = new Label(badgeText);
        lblBadge.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        lblBadge.setTextFill(Color.WHITE);
        lblBadge.setPadding(new Insets(3, 8, 3, 8));
        lblBadge.setStyle("-fx-background-color: " + badgeColor + "; -fx-background-radius: 4;");
        
        // === FILA 3: Sección "Contactado por" ===
        VBox seccionContacto = new VBox(6);
        seccionContacto.setPadding(new Insets(10));
        seccionContacto.setStyle(
            "-fx-background-color: #F5F5F5;" +
            "-fx-background-radius: 8;"
        );
        
        Label lblContactado = new Label("✓ Contactado por:");
        lblContactado.setFont(Font.font("System", FontWeight.BOLD, 12));
        lblContactado.setTextFill(Color.rgb(56, 142, 60));
        
        // Tag del sector
        Label tagSector = new Label(empresa.getSector());
        tagSector.setFont(Font.font("System", FontWeight.BOLD, 13));
        tagSector.setTextFill(Color.rgb(21, 101, 192));
        tagSector.setPadding(new Insets(6, 14, 6, 14));
        tagSector.setStyle("-fx-background-color: #E3F2FD; -fx-background-radius: 12;");
        
        seccionContacto.getChildren().addAll(lblContactado, tagSector);
        
        // === FILA 4: Barra de plazas ===
        HBox barraPlazas = new HBox(8);
        barraPlazas.setAlignment(Pos.CENTER_LEFT);
        barraPlazas.setPadding(new Insets(5, 0, 0, 0));
        
        HBox indicadores = new HBox(4);
        for (int i = 0; i < empresa.getPlazasTotales(); i++) {
            Label ind = new Label();
            ind.setPrefSize(25, 6);
            String color = i < empresa.getPlazasOcupadas() ? "#4facfe" : "#E0E0E0";
            ind.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3;");
            indicadores.getChildren().add(ind);
        }
        
        Label lblPlazas = new Label(empresa.getEstadoPlazas());
        lblPlazas.setFont(Font.font("System", FontWeight.BOLD, 12));
        lblPlazas.setTextFill(Color.rgb(100, 100, 100));
        
        barraPlazas.getChildren().addAll(indicadores, lblPlazas);
        
        // Añadir todo a la tarjeta
        tarjeta.getChildren().addAll(fila1, lblBadge, seccionContacto, barraPlazas);
        
        // Efecto para empresas ocupadas (solo alumnos)
        if (usuario.esAlumno() && empresa.estaOcupada()) {
            tarjeta.setStyle(
                "-fx-background-color: #F5F5F5;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #E0E0E0;" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;" +
                "-fx-opacity: 0.85;"
            );
        }
        
        // Click y hover
        tarjeta.setOnMouseClicked(e -> mostrarDetallesEmpresa(empresa));
        tarjeta.setOnMouseEntered(e -> {
            if (!(usuario.esAlumno() && empresa.estaOcupada())) {
                tarjeta.setStyle(
                    "-fx-background-color: #FFFFFF;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #BDBDBD;" +
                    "-fx-border-radius: 12;" +
                    "-fx-border-width: 1;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 15, 0, 0, 5);" +
                    "-fx-cursor: hand;"
                );
            }
        });
        tarjeta.setOnMouseExited(e -> {
            if (usuario.esAlumno() && empresa.estaOcupada()) {
                tarjeta.setStyle(
                    "-fx-background-color: #F5F5F5;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #E0E0E0;" +
                    "-fx-border-radius: 12;" +
                    "-fx-border-width: 1;" +
                    "-fx-opacity: 0.85;"
                );
            } else {
                tarjeta.setStyle(
                    "-fx-background-color: #FFFFFF;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #E0E0E0;" +
                    "-fx-border-radius: 12;" +
                    "-fx-border-width: 1;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"
                );
            }
        });
        
        return tarjeta;
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
        VBox btnBuscar = crearBotonNav("🔍", "Buscar Empre...", true);
        VBox btnListas = crearBotonNav("📝", "Mis Listas", false);
        VBox btnPerfil = crearBotonNav("👤", "Mi Perfil", false);
        
        btnBuscar.setOnMouseClicked(e -> abrirBusqueda());
        btnListas.setOnMouseClicked(e -> mostrarMisListas());
        btnPerfil.setOnMouseClicked(e -> abrirPerfil());
        
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
        
        btn.setOnMouseEntered(e -> lblTexto.setTextFill(Color.web("#4facfe")));
        btn.setOnMouseExited(e -> { if (!activo) lblTexto.setTextFill(Color.GRAY); });
        
        return btn;
    }

    private void mostrarDetallesEmpresa(Empresa empresa) {
        if (usuario.esAlumno() && empresa.estaOcupada()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Empresa no disponible");
            alert.setHeaderText("🔒 " + empresa.getNombre());
            alert.setContentText("Esta empresa no tiene plazas disponibles.\nEstado: " + 
                empresa.getEstadoPlazas() + " (OCUPADA)");
            alert.showAndWait();
            return;
        }
        
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(empresa.getNombre());
        
        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(20));
        contenido.setPrefWidth(380);
        
        // Header
        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Label lblEstado = new Label(empresa.estaOcupada() ? "🔴 OCUPADA" : "🟢 LIBRE");
        lblEstado.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblEstado.setTextFill(empresa.estaOcupada() ? Color.RED : Color.GREEN);
        
        Label lblSector = new Label("📁 " + empresa.getSector());
        Label lblUbicacion = new Label("📍 " + empresa.getUbicacion());
        Label lblPlazas = new Label("👥 Plazas: " + empresa.getEstadoPlazas());
        
        TextArea txtDesc = new TextArea(empresa.getDescripcion());
        txtDesc.setWrapText(true);
        txtDesc.setEditable(false);
        txtDesc.setPrefRowCount(3);
        
        TextArea txtResena = new TextArea(empresa.getResena());
        txtResena.setWrapText(true);
        txtResena.setEditable(false);
        txtResena.setPrefRowCount(3);
        
        contenido.getChildren().addAll(
            lblNombre, lblEstado, lblSector, lblUbicacion, lblPlazas,
            new Separator(),
            new Label("Descripción:"), txtDesc,
            new Label("Reseña:"), txtResena
        );
        
        // Controles profesor
        if (usuario.esProfesor()) {
            HBox controles = new HBox(10);
            controles.setAlignment(Pos.CENTER);
            controles.setPadding(new Insets(10, 0, 0, 0));
            
            Spinner<Integer> spinner = new Spinner<>(0, empresa.getPlazasTotales(), empresa.getPlazasOcupadas());
            spinner.setPrefWidth(70);
            
            Button btnActualizar = new Button("Actualizar Plazas");
            btnActualizar.setStyle("-fx-background-color: #4facfe; -fx-text-fill: white;");
            btnActualizar.setOnAction(e -> {
                dataService.actualizarPlazas(empresa, spinner.getValue());
                actualizarListaEmpresas();
                dialog.close();
            });
            
            Button btnEliminar = new Button("Eliminar");
            btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            btnEliminar.setOnAction(e -> {
                dataService.eliminarEmpresa(empresa);
                actualizarListaEmpresas();
                dialog.close();
            });
            
            controles.getChildren().addAll(new Label("Plazas:"), spinner, btnActualizar, btnEliminar);
            contenido.getChildren().addAll(new Separator(), controles);
        }
        
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void mostrarMisListas() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Mis Listas");
        
        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(20));
        contenido.setPrefSize(350, 400);
        contenido.setStyle("-fx-background-color: linear-gradient(to bottom, #4facfe, #00f2fe);");
        
        Label titulo = new Label("Mis Listas");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);
        
        VBox listas = new VBox(10);
        listas.getChildren().addAll(
            crearItemLista("❤️", "Favoritos", "2 empresas"),
            crearItemLista("📞", "Para contactar", "1 empresas"),
            crearItemLista("📋", "FCT 2025", "3 empresas")
        );
        
        contenido.getChildren().addAll(titulo, listas);
        
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private HBox crearItemLista(String icono, String nombre, String cantidad) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(15));
        item.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        Label lblIcono = new Label(icono);
        lblIcono.setFont(Font.font(18));
        
        VBox info = new VBox(2);
        Label lblNombre = new Label(nombre);
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label lblCant = new Label(cantidad);
        lblCant.setTextFill(Color.GRAY);
        info.getChildren().addAll(lblNombre, lblCant);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label flecha = new Label("›");
        flecha.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        flecha.setTextFill(Color.web("#4facfe"));
        
        item.getChildren().addAll(lblIcono, info, spacer, flecha);
        return item;
    }

    private void mostrarDialogoAnadirEmpresa() {
        Dialog<Empresa> dialog = new Dialog<>();
        dialog.setTitle("Añadir Empresa");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField txtNombre = new TextField();
        TextField txtSector = new TextField();
        TextField txtUbicacion = new TextField();
        TextArea txtDesc = new TextArea();
        txtDesc.setPrefRowCount(2);
        TextArea txtResena = new TextArea();
        txtResena.setPrefRowCount(2);
        Spinner<Integer> spinPlazas = new Spinner<>(1, 10, 4);
        
        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Sector:"), txtSector);
        grid.addRow(2, new Label("Ubicación:"), txtUbicacion);
        grid.addRow(3, new Label("Descripción:"), txtDesc);
        grid.addRow(4, new Label("Reseña:"), txtResena);
        grid.addRow(5, new Label("Plazas:"), spinPlazas);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !txtNombre.getText().isEmpty()) {
                return new Empresa(txtNombre.getText(), txtDesc.getText(), txtResena.getText(),
                    txtSector.getText(), txtUbicacion.getText(), spinPlazas.getValue(), 0);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(emp -> {
            dataService.agregarEmpresa(emp);
            actualizarListaEmpresas();
        });
    }

    private void abrirBusqueda() {
        stage.setScene(new BusquedaView(stage, usuario).crearEscena());
    }

    private void abrirPerfil() {
        stage.setScene(new PerfilView(stage, usuario).crearEscena());
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