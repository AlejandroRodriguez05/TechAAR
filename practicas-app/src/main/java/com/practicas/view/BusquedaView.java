package com.practicas.view;

import java.util.List;
import java.util.stream.Collectors;

import com.practicas.model.DataService;
import com.practicas.model.Empresa;
import com.practicas.model.Usuario;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
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
 * Vista de búsqueda de empresas
 */
public class BusquedaView {

    private Stage stage;
    private Usuario usuario;
    private DataService dataService;
    private VBox listaResultados;
    private TextField txtBusqueda;
    private String filtroSector = "Todos";

    public BusquedaView(Stage stage, Usuario usuario) {
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
        header.setPadding(new Insets(20, 20, 15, 20));
        header.setStyle("-fx-background-color: transparent;");
        
        // Título
        Label lblTitulo = new Label("🔍 Buscar Empresas");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.WHITE);
        
        // Campo de búsqueda
        HBox barraBusqueda = new HBox(10);
        barraBusqueda.setAlignment(Pos.CENTER_LEFT);
        barraBusqueda.setPadding(new Insets(12, 15, 12, 15));
        barraBusqueda.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"
        );
        
        Label iconoBuscar = new Label("🔍");
        iconoBuscar.setFont(Font.font(16));
        
        txtBusqueda = new TextField();
        txtBusqueda.setPromptText("Buscar por nombre, sector o ubicación...");
        txtBusqueda.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-width: 0;" +
            "-fx-font-size: 14px;"
        );
        txtBusqueda.setPrefWidth(280);
        txtBusqueda.textProperty().addListener((obs, old, nuevo) -> buscar(nuevo));
        
        Button btnLimpiar = new Button("✕");
        btnLimpiar.setStyle(
            "-fx-background-color: #E0E0E0;" +
            "-fx-background-radius: 12;" +
            "-fx-text-fill: #666666;" +
            "-fx-cursor: hand;"
        );
        btnLimpiar.setOnAction(e -> {
            txtBusqueda.clear();
            buscar("");
        });
        
        barraBusqueda.getChildren().addAll(iconoBuscar, txtBusqueda, btnLimpiar);
        HBox.setHgrow(txtBusqueda, Priority.ALWAYS);
        
        // Filtros por sector
        HBox filtros = new HBox(10);
        filtros.setAlignment(Pos.CENTER_LEFT);
        filtros.setPadding(new Insets(10, 0, 5, 0));
        
        filtros.getChildren().addAll(
            crearChipFiltro("Todos"),
            crearChipFiltro("Tecnología"),
            crearChipFiltro("Consultoría IT"),
            crearChipFiltro("Cloud Computing")
        );
        
        header.getChildren().addAll(lblTitulo, barraBusqueda, filtros);
        return header;
    }

    private Label crearChipFiltro(String sector) {
        Label chip = new Label(sector);
        chip.setFont(Font.font("Arial", 12));
        chip.setPadding(new Insets(6, 12, 6, 12));
        
        boolean activo = sector.equals(filtroSector);
        actualizarEstiloChip(chip, activo);
        
        chip.setOnMouseClicked(e -> {
            filtroSector = sector;
            // Actualizar todos los chips
            HBox parent = (HBox) chip.getParent();
            for (var node : parent.getChildren()) {
                if (node instanceof Label) {
                    Label c = (Label) node;
                    actualizarEstiloChip(c, c.getText().equals(filtroSector));
                }
            }
            buscar(txtBusqueda.getText());
        });
        
        return chip;
    }

    private void actualizarEstiloChip(Label chip, boolean activo) {
        if (activo) {
            chip.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 15;" +
                "-fx-text-fill: #4facfe;" +
                "-fx-cursor: hand;"
            );
        } else {
            chip.setStyle(
                "-fx-background-color: rgba(255,255,255,0.3);" +
                "-fx-background-radius: 15;" +
                "-fx-text-fill: white;" +
                "-fx-cursor: hand;"
            );
        }
    }

    private VBox crearContenido() {
        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(10, 15, 10, 15));
        contenido.setStyle("-fx-background-color: transparent;");
        
        listaResultados = new VBox(12);
        listaResultados.setStyle("-fx-background-color: transparent;");
        
        // Mostrar todas las empresas inicialmente
        buscar("");
        
        ScrollPane scroll = new ScrollPane(listaResultados);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        
        contenido.getChildren().add(scroll);
        return contenido;
    }

    private void buscar(String termino) {
        listaResultados.getChildren().clear();
        
        String terminoLower = termino.toLowerCase().trim();
        
        List<Empresa> resultados = dataService.getEmpresas().stream()
            .filter(e -> {
                // Filtro por texto
                boolean coincideTexto = terminoLower.isEmpty() ||
                    e.getNombre().toLowerCase().contains(terminoLower) ||
                    e.getSector().toLowerCase().contains(terminoLower) ||
                    e.getUbicacion().toLowerCase().contains(terminoLower);
                
                // Filtro por sector
                boolean coincideSector = filtroSector.equals("Todos") ||
                    e.getSector().equalsIgnoreCase(filtroSector);
                
                return coincideTexto && coincideSector;
            })
            .collect(Collectors.toList());
        
        // Mostrar contador de resultados
        Label lblResultados = new Label(resultados.size() + " empresas encontradas");
        lblResultados.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblResultados.setTextFill(Color.WHITE);
        lblResultados.setPadding(new Insets(0, 0, 10, 5));
        listaResultados.getChildren().add(lblResultados);
        
        if (resultados.isEmpty()) {
            VBox vacio = new VBox(10);
            vacio.setAlignment(Pos.CENTER);
            vacio.setPadding(new Insets(40));
            
            Label iconoVacio = new Label("🔍");
            iconoVacio.setFont(Font.font(50));
            
            Label lblVacio = new Label("No se encontraron empresas");
            lblVacio.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            lblVacio.setTextFill(Color.WHITE);
            
            Label lblSugerencia = new Label("Intenta con otros términos de búsqueda");
            lblSugerencia.setFont(Font.font("Arial", 13));
            lblSugerencia.setTextFill(Color.rgb(255, 255, 255, 0.8));
            
            vacio.getChildren().addAll(iconoVacio, lblVacio, lblSugerencia);
            listaResultados.getChildren().add(vacio);
        } else {
            for (Empresa empresa : resultados) {
                listaResultados.getChildren().add(crearTarjetaResultado(empresa));
            }
        }
    }

    private VBox crearTarjetaResultado(Empresa empresa) {
        VBox tarjeta = new VBox(8);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #E0E0E0;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"
        );
        
        // Fila 1: Nombre + Estado
        HBox fila1 = new HBox();
        fila1.setAlignment(Pos.CENTER_LEFT);
        
        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblNombre.setTextFill(Color.BLACK);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label lblEstado = new Label(empresa.estaOcupada() ? "OCUPADA" : "DISPONIBLE");
        lblEstado.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        lblEstado.setTextFill(Color.WHITE);
        lblEstado.setPadding(new Insets(4, 8, 4, 8));
        lblEstado.setStyle(
            "-fx-background-color: " + (empresa.estaOcupada() ? "#FF5722" : "#4CAF50") + ";" +
            "-fx-background-radius: 4;"
        );
        
        fila1.getChildren().addAll(lblNombre, spacer, lblEstado);
        
        // Fila 2: Ubicación y sector
        HBox fila2 = new HBox(15);
        fila2.setAlignment(Pos.CENTER_LEFT);
        
        Label lblUbicacion = new Label("📍 " + empresa.getUbicacion());
        lblUbicacion.setFont(Font.font("Arial", 12));
        lblUbicacion.setTextFill(Color.GRAY);
        
        Label lblSector = new Label("📁 " + empresa.getSector());
        lblSector.setFont(Font.font("Arial", 12));
        lblSector.setTextFill(Color.GRAY);
        
        fila2.getChildren().addAll(lblUbicacion, lblSector);
        
        // Fila 3: Plazas
        Label lblPlazas = new Label("👥 Plazas: " + empresa.getEstadoPlazas());
        lblPlazas.setFont(Font.font("Arial", 12));
        lblPlazas.setTextFill(Color.web("#666666"));
        
        tarjeta.getChildren().addAll(fila1, fila2, lblPlazas);
        
        // Hover effect
        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #BDBDBD;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 15, 0, 0, 5);" +
            "-fx-cursor: hand;"
        ));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #E0E0E0;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"
        ));
        
        tarjeta.setOnMouseClicked(e -> mostrarDetalles(empresa));
        
        return tarjeta;
    }

    private void mostrarDetalles(Empresa empresa) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(empresa.getNombre());
        
        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(20));
        contenido.setPrefWidth(380);
        
        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Label lblEstado = new Label(empresa.estaOcupada() ? "🔴 OCUPADA" : "🟢 DISPONIBLE");
        lblEstado.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblEstado.setTextFill(empresa.estaOcupada() ? Color.RED : Color.GREEN);
        
        Label lblSector = new Label("📁 " + empresa.getSector());
        Label lblUbicacion = new Label("📍 " + empresa.getUbicacion());
        Label lblPlazas = new Label("👥 Plazas: " + empresa.getEstadoPlazas());
        
        TextArea txtDesc = new TextArea(empresa.getDescripcion());
        txtDesc.setWrapText(true);
        txtDesc.setEditable(false);
        txtDesc.setPrefRowCount(3);
        
        contenido.getChildren().addAll(
            lblNombre, lblEstado, lblSector, lblUbicacion, lblPlazas,
            new Separator(),
            new Label("Descripción:"), txtDesc
        );
        
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
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
        
        // Navegación
        btnFCT.setOnMouseClicked(e -> volverAEmpresas());
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
        return btn;
    }

    private void volverAEmpresas() {
        stage.setScene(new EmpresasView(stage, usuario).crearEscena());
    }

    private void abrirPerfil() {
        stage.setScene(new PerfilView(stage, usuario).crearEscena());
    }
}