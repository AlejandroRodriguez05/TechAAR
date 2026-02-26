package com.practicas.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.practicas.model.DataService;
import com.practicas.model.Empresa;
import com.practicas.model.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class EmpresasController {

    @FXML private Label lblSaludo;
    @FXML private Label lblInfo;
    @FXML private Label lblBadge;
    @FXML private Label lblTituloSeccion;
    @FXML private Button btnAdd;
    @FXML private VBox listaEmpresas;
    @FXML private VBox filtroNuevas;
    @FXML private VBox filtroTop;
    @FXML private VBox filtroCercanas;

    private Usuario usuario;
    private DataService dataService;
    private String filtroActual = "todas";
    
    // Mapa de valoraciones por empresa (simulado)
    private Map<String, Double> valoraciones = new HashMap<>();
    // Mapa de fechas de creación (simulado - más reciente = índice menor)
    private Map<String, Integer> ordenCreacion = new HashMap<>();
    // Mapa de distancias (simulado)
    private Map<String, Double> distancias = new HashMap<>();

    @FXML
    public void initialize() {
        dataService = DataService.getInstance();
        inicializarDatosSimulados();
    }

    private void inicializarDatosSimulados() {
        // Simular valoraciones aleatorias para cada empresa
        Random rand = new Random(42); // Seed fija para consistencia
        List<Empresa> empresas = dataService.getEmpresas();
        
        for (int i = 0; i < empresas.size(); i++) {
            String nombre = empresas.get(i).getNombre();
            valoraciones.put(nombre, 3.5 + rand.nextDouble() * 1.5);
            ordenCreacion.put(nombre, i);
            distancias.put(nombre, 0.5 + rand.nextDouble() * 20); // 0.5 a 20.5 km
        }
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        actualizarVista();
    }

    private void actualizarVista() {
        String nombre = usuario.getNombreCompleto().split(" ")[0];
        lblSaludo.setText("Hola, " + nombre);
        
        String rol = usuario.esProfesor() ? "Profesor" : "Alumno";
        int disponibles = (int) dataService.getEmpresas().stream().filter(e -> !e.estaOcupada()).count();
        lblInfo.setText(rol + " - " + disponibles + " empresas disponibles");
        
        btnAdd.setVisible(usuario.esProfesor());
        
        cargarEmpresas();
    }

    private void cargarEmpresas() {
        listaEmpresas.getChildren().clear();
        
        List<Empresa> empresas = dataService.getEmpresas();
        
        // Aplicar filtro según selección
        switch (filtroActual) {
            case "nuevas":
                empresas = empresas.stream()
                    .sorted(Comparator.comparingInt(e -> ordenCreacion.getOrDefault(e.getNombre(), 999)))
                    .collect(Collectors.toList());
                lblTituloSeccion.setText("Empresas nuevas");
                break;
            case "top":
                empresas = empresas.stream()
                    .sorted((e1, e2) -> Double.compare(
                        valoraciones.getOrDefault(e2.getNombre(), 0.0),
                        valoraciones.getOrDefault(e1.getNombre(), 0.0)))
                    .collect(Collectors.toList());
                lblTituloSeccion.setText("Top valoradas");
                break;
            case "cercanas":
                empresas = empresas.stream()
                    .sorted(Comparator.comparingDouble(e -> distancias.getOrDefault(e.getNombre(), 999.0)))
                    .collect(Collectors.toList());
                lblTituloSeccion.setText("Empresas cercanas");
                break;
            default:
                lblTituloSeccion.setText("Todas las empresas");
                break;
        }
        
        lblBadge.setText(String.valueOf(empresas.size()));
        
        for (Empresa empresa : empresas) {
            listaEmpresas.getChildren().add(crearTarjetaEmpresa(empresa));
        }
    }

    @FXML
    private void filtrarNuevas() {
        filtroActual = filtroActual.equals("nuevas") ? "todas" : "nuevas";
        actualizarEstilosFiltros();
        cargarEmpresas();
    }

    @FXML
    private void filtrarTopValoradas() {
        filtroActual = filtroActual.equals("top") ? "todas" : "top";
        actualizarEstilosFiltros();
        cargarEmpresas();
    }

    @FXML
    private void filtrarCercanas() {
        filtroActual = filtroActual.equals("cercanas") ? "todas" : "cercanas";
        actualizarEstilosFiltros();
        cargarEmpresas();
    }

    private void actualizarEstilosFiltros() {
        String estiloActivo = "-fx-background-color: white; -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 12 20;";
        String estiloInactivo = "-fx-background-color: rgba(255,255,255,0.25); -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 12 20;";
        
        filtroNuevas.setStyle(filtroActual.equals("nuevas") ? estiloActivo : estiloInactivo);
        filtroTop.setStyle(filtroActual.equals("top") ? estiloActivo : estiloInactivo);
        filtroCercanas.setStyle(filtroActual.equals("cercanas") ? estiloActivo : estiloInactivo);
    }

    private VBox crearTarjetaEmpresa(Empresa empresa) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);");

        // Fila 1: Nombre + Valoracion
        HBox fila1 = new HBox();
        fila1.setAlignment(Pos.CENTER_LEFT);

        VBox infoNombre = new VBox(2);
        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setStyle("-fx-font-family: Arial; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        
        Label lblUbicacion = new Label(empresa.getUbicacion());
        lblUbicacion.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #555555;");
        
        // Mostrar distancia si estamos en filtro cercanas
        if (filtroActual.equals("cercanas")) {
            double dist = distancias.getOrDefault(empresa.getNombre(), 0.0);
            lblUbicacion.setText(empresa.getUbicacion() + " - " + String.format("%.1f", dist) + " km");
        }
        
        infoNombre.getChildren().addAll(lblNombre, lblUbicacion);

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        // Valoración
        HBox valoracion = new HBox(4);
        valoracion.setAlignment(Pos.CENTER);
        valoracion.setPadding(new Insets(5, 10, 5, 10));
        valoracion.setStyle("-fx-background-color: #FFD54F; -fx-background-radius: 15;");
        
        double valor = valoraciones.getOrDefault(empresa.getNombre(), 4.0);
        Label puntuacion = new Label(String.format("%.1f", valor));
        puntuacion.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        puntuacion.setTextFill(Color.web("#5D4037"));
        valoracion.getChildren().add(puntuacion);

        fila1.getChildren().addAll(infoNombre, spacer1, valoracion);

        // Badge
        String badgeText = empresa.estaOcupada() ? "HOT" : "INF";
        String badgeColor = empresa.estaOcupada() ? "#FF5722" : "#4CAF50";
        Label lblBadgeEmpresa = new Label(badgeText);
        lblBadgeEmpresa.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        lblBadgeEmpresa.setTextFill(Color.WHITE);
        lblBadgeEmpresa.setPadding(new Insets(3, 8, 3, 8));
        lblBadgeEmpresa.setStyle("-fx-background-color: " + badgeColor + "; -fx-background-radius: 4;");

        // Seccion contacto
        VBox seccionContacto = new VBox(6);
        seccionContacto.setPadding(new Insets(10));
        seccionContacto.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 8;");
        Label lblContactado = new Label("Contactado por:");
        lblContactado.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblContactado.setTextFill(Color.rgb(56, 142, 60));
        Label tagSector = new Label(empresa.getSector());
        tagSector.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        tagSector.setTextFill(Color.rgb(21, 101, 192));
        tagSector.setPadding(new Insets(6, 14, 6, 14));
        tagSector.setStyle("-fx-background-color: #E3F2FD; -fx-background-radius: 12;");
        seccionContacto.getChildren().addAll(lblContactado, tagSector);

        // Barra de plazas
        HBox barraPlazas = new HBox(8);
        barraPlazas.setAlignment(Pos.CENTER_LEFT);
        HBox indicadores = new HBox(4);
        for (int i = 0; i < empresa.getPlazasTotales(); i++) {
            Region ind = new Region();
            ind.setPrefSize(25, 6);
            String color = i < empresa.getPlazasOcupadas() ? "#4facfe" : "#E0E0E0";
            ind.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3;");
            indicadores.getChildren().add(ind);
        }
        Label lblPlazas = new Label(empresa.getEstadoPlazas());
        lblPlazas.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblPlazas.setTextFill(Color.rgb(100, 100, 100));
        barraPlazas.getChildren().addAll(indicadores, lblPlazas);

        tarjeta.getChildren().addAll(fila1, lblBadgeEmpresa, seccionContacto, barraPlazas);

        // Hover
        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #BDBDBD; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 15, 0, 0, 5); -fx-cursor: hand;"));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"));
        tarjeta.setOnMouseClicked(e -> mostrarDetallesEmpresa(empresa));

        return tarjeta;
    }

    private void mostrarDetallesEmpresa(Empresa empresa) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(empresa.getNombre());

        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(20));
        contenido.setPrefWidth(380);

        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label lblEstado = new Label(empresa.estaOcupada() ? "OCUPADA" : "LIBRE");
        lblEstado.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblEstado.setTextFill(empresa.estaOcupada() ? Color.RED : Color.GREEN);

        double valor = valoraciones.getOrDefault(empresa.getNombre(), 4.0);
        Label lblValoracion = new Label("Valoracion: " + String.format("%.1f", valor) + " / 5.0");
        lblValoracion.setFont(Font.font("Arial", 12));
        
        double dist = distancias.getOrDefault(empresa.getNombre(), 0.0);
        Label lblDistancia = new Label("Distancia: " + String.format("%.1f", dist) + " km");
        lblDistancia.setFont(Font.font("Arial", 12));

        Label lblSector = new Label("Sector: " + empresa.getSector());
        Label lblUbicacion = new Label("Ubicacion: " + empresa.getUbicacion());
        Label lblPlazas = new Label("Plazas: " + empresa.getEstadoPlazas());

        TextArea txtDesc = new TextArea(empresa.getDescripcion());
        txtDesc.setWrapText(true);
        txtDesc.setEditable(false);
        txtDesc.setPrefRowCount(3);

        contenido.getChildren().addAll(lblNombre, lblEstado, lblValoracion, lblDistancia, lblSector, lblUbicacion, lblPlazas, new Separator(), new Label("Descripcion:"), txtDesc);

        if (usuario.esProfesor()) {
            HBox controles = new HBox(10);
            controles.setAlignment(Pos.CENTER);
            Spinner<Integer> spinner = new Spinner<>(0, empresa.getPlazasTotales(), empresa.getPlazasOcupadas());
            spinner.setPrefWidth(70);
            Button btnActualizar = new Button("Actualizar Plazas");
            btnActualizar.setStyle("-fx-background-color: #4facfe; -fx-text-fill: white;");
            btnActualizar.setOnAction(e -> {
                dataService.actualizarPlazas(empresa, spinner.getValue());
                cargarEmpresas();
                dialog.close();
            });
            Button btnEliminar = new Button("Eliminar");
            btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            btnEliminar.setOnAction(e -> {
                dataService.eliminarEmpresa(empresa);
                cargarEmpresas();
                dialog.close();
            });
            controles.getChildren().addAll(new Label("Plazas:"), spinner, btnActualizar, btnEliminar);
            contenido.getChildren().addAll(new Separator(), controles);
        }

        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    private void mostrarDialogoAnadir() {
        Dialog<Empresa> dialog = new Dialog<>();
        dialog.setTitle("Anadir Empresa");

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
        grid.addRow(2, new Label("Ubicacion:"), txtUbicacion);
        grid.addRow(3, new Label("Descripcion:"), txtDesc);
        grid.addRow(4, new Label("Resena:"), txtResena);
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
            // Añadir datos simulados para la nueva empresa
            Random rand = new Random();
            valoraciones.put(emp.getNombre(), 3.5 + rand.nextDouble() * 1.5);
            ordenCreacion.put(emp.getNombre(), -1); // Nueva empresa, más reciente
            distancias.put(emp.getNombre(), 0.5 + rand.nextDouble() * 10);
            cargarEmpresas();
        });
    }

    @FXML
    private void mostrarMisListas() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Mis Listas");

        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(25));
        contenido.setPrefSize(400, 350);
        contenido.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        Label titulo = new Label("Mis Listas");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.WHITE);

        contenido.getChildren().addAll(titulo,
                crearItemLista("❤️", "Favoritos", "2 empresas", "#FF6B6B", "#FFEBEE"),
                crearItemLista("📞", "Para contactar", "1 empresa", "#4ECDC4", "#E0F7FA"),
                crearItemLista("📋", "FCT 2025", "3 empresas", "#45B7D1", "#E3F2FD"),
                crearItemLista("⭐", "Destacadas", "5 empresas", "#F7DC6F", "#FFFDE7"));

        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setStyle("-fx-background-color: transparent;");
        dialog.showAndWait();
    }

    private HBox crearItemLista(String emoji, String nombre, String cantidad, String colorIcono, String colorFondo) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(15));
        item.setStyle("-fx-background-color: " + colorFondo + "; -fx-background-radius: 15; -fx-cursor: hand;");

        // Emoji
        Label lblEmoji = new Label(emoji);
        lblEmoji.setFont(Font.font(24));
        lblEmoji.setMinWidth(40);
        lblEmoji.setAlignment(Pos.CENTER);

        // Info
        VBox info = new VBox(2);
        Label lblNombre = new Label(nombre);
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblNombre.setTextFill(Color.web("#333333"));
        Label lblCant = new Label(cantidad);
        lblCant.setFont(Font.font("Arial", 12));
        lblCant.setTextFill(Color.web("#666666"));
        info.getChildren().addAll(lblNombre, lblCant);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Flecha
        Label flecha = new Label("›");
        flecha.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        flecha.setTextFill(Color.web(colorIcono));

        item.getChildren().addAll(lblEmoji, info, spacer, flecha);

        // Hover effect
        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"));
        item.setOnMouseExited(e -> item.setStyle("-fx-background-color: " + colorFondo + "; -fx-background-radius: 15; -fx-cursor: hand;"));

        return item;
    }

    @FXML
    private void abrirBusqueda() {
        cambiarVista("/fxml/busqueda.fxml", controller -> ((BusquedaController) controller).setUsuario(usuario));
    }

    @FXML
    private void abrirPerfil() {
        cambiarVista("/fxml/perfil.fxml", controller -> ((PerfilController) controller).setUsuario(usuario));
    }

    private void cambiarVista(String fxmlPath, java.util.function.Consumer<Object> setupController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            setupController.accept(loader.getController());
            Stage stage = (Stage) listaEmpresas.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}