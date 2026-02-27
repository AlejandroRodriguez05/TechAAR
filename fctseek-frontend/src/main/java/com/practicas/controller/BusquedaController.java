package com.practicas.controller;

import java.io.IOException;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class BusquedaController {

    @FXML private TextField txtBusqueda;
    @FXML private Label lblResultados;
    @FXML private VBox listaResultados;
    @FXML private Label chipTodos;
    @FXML private Label chipTecnologia;
    @FXML private Label chipConsultoria;
    @FXML private Label chipCloud;

    private Usuario usuario;
    private DataService dataService;
    private String filtroSector = "Todos";

    @FXML
    public void initialize() {
        dataService = DataService.getInstance();
        txtBusqueda.textProperty().addListener((obs, old, nuevo) -> buscar());
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        buscar();
    }

    @FXML
    private void limpiarBusqueda() {
        txtBusqueda.clear();
        buscar();
    }

    @FXML
    private void filtrarTodos() {
        filtroSector = "Todos";
        actualizarChips();
        buscar();
    }

    @FXML
    private void filtrarTecnologia() {
        filtroSector = "Tecnologia";
        actualizarChips();
        buscar();
    }

    @FXML
    private void filtrarConsultoria() {
        filtroSector = "Consultoria IT";
        actualizarChips();
        buscar();
    }

    @FXML
    private void filtrarCloud() {
        filtroSector = "Cloud Computing";
        actualizarChips();
        buscar();
    }

    private void actualizarChips() {
        String activo = "-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-padding: 6 12; -fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #4facfe; -fx-cursor: hand;";
        String inactivo = "-fx-background-color: rgba(255,255,255,0.3); -fx-background-radius: 15; -fx-padding: 6 12; -fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: white; -fx-cursor: hand;";

        chipTodos.setStyle(filtroSector.equals("Todos") ? activo : inactivo);
        chipTecnologia.setStyle(filtroSector.equals("Tecnologia") ? activo : inactivo);
        chipConsultoria.setStyle(filtroSector.equals("Consultoria IT") ? activo : inactivo);
        chipCloud.setStyle(filtroSector.equals("Cloud Computing") ? activo : inactivo);
    }

    private void buscar() {
        listaResultados.getChildren().clear();

        String termino = txtBusqueda.getText().toLowerCase().trim();

        List<Empresa> resultados = dataService.getEmpresas().stream()
                .filter(e -> {
                    boolean coincideTexto = termino.isEmpty() ||
                            e.getNombre().toLowerCase().contains(termino) ||
                            e.getSector().toLowerCase().contains(termino) ||
                            e.getUbicacion().toLowerCase().contains(termino);

                    boolean coincideSector = filtroSector.equals("Todos") ||
                            e.getSector().equalsIgnoreCase(filtroSector);

                    return coincideTexto && coincideSector;
                })
                .collect(Collectors.toList());

        lblResultados.setText(resultados.size() + " empresas encontradas");

        if (resultados.isEmpty()) {
            VBox vacio = new VBox(10);
            vacio.setAlignment(Pos.CENTER);
            vacio.setPadding(new Insets(40));

            Label lblVacio = new Label("No se encontraron empresas");
            lblVacio.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            lblVacio.setTextFill(Color.WHITE);

            Label lblSugerencia = new Label("Intenta con otros terminos de busqueda");
            lblSugerencia.setFont(Font.font("Arial", 13));
            lblSugerencia.setTextFill(Color.rgb(255, 255, 255, 0.8));

            vacio.getChildren().addAll(lblVacio, lblSugerencia);
            listaResultados.getChildren().add(vacio);
        } else {
            for (Empresa empresa : resultados) {
                listaResultados.getChildren().add(crearTarjetaResultado(empresa));
            }
        }
    }

    private VBox crearTarjetaResultado(Empresa empresa) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setMinHeight(80);
        tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 12; -fx-border-width: 1;");

        // Fila 1: Nombre + Estado
        HBox fila1 = new HBox(10);
        fila1.setAlignment(Pos.CENTER_LEFT);

        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setStyle("-fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblEstado = new Label(empresa.estaOcupada() ? "OCUPADA" : "DISPONIBLE");
        lblEstado.setStyle("-fx-font-family: Arial; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + (empresa.estaOcupada() ? "#FF5722" : "#4CAF50") + "; -fx-background-radius: 4; -fx-padding: 4 8;");

        fila1.getChildren().addAll(lblNombre, spacer, lblEstado);

        // Fila 2: Ubicacion + Sector
        HBox fila2 = new HBox(20);
        fila2.setAlignment(Pos.CENTER_LEFT);

        Label lblUbicacion = new Label("Ubicacion: " + empresa.getUbicacion());
        lblUbicacion.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #666666;");

        Label lblSector = new Label("Sector: " + empresa.getSector());
        lblSector.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #666666;");

        fila2.getChildren().addAll(lblUbicacion, lblSector);

        // Fila 3: Plazas
        Label lblPlazas = new Label("Plazas: " + empresa.getEstadoPlazas());
        lblPlazas.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #333333; -fx-font-weight: bold;");

        tarjeta.getChildren().addAll(fila1, fila2, lblPlazas);

        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 12; -fx-border-color: #4facfe; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;"));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 12; -fx-border-width: 1;"));
        tarjeta.setOnMouseClicked(e -> mostrarDetalles(empresa));

        return tarjeta;
    }

    private void mostrarDetalles(Empresa empresa) {
        Stage ventana = new Stage();
        ventana.setTitle(empresa.getNombre());
        ventana.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: white;");

        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setStyle("-fx-font-family: Arial; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label lblEstado = new Label(empresa.estaOcupada() ? "OCUPADA" : "DISPONIBLE");
        lblEstado.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + (empresa.estaOcupada() ? "#FF5722" : "#4CAF50") + ";");

        Label lblSector = new Label("Sector: " + empresa.getSector());
        lblSector.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #666666;");
        
        Label lblUbicacion = new Label("Ubicacion: " + empresa.getUbicacion());
        lblUbicacion.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #666666;");
        
        Label lblPlazas = new Label("Plazas: " + empresa.getEstadoPlazas());
        lblPlazas.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #666666; -fx-font-weight: bold;");

        Separator sep = new Separator();
        
        Label lblDescTitulo = new Label("Descripcion:");
        lblDescTitulo.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        TextArea txtDesc = new TextArea(empresa.getDescripcion() != null ? empresa.getDescripcion() : "Sin descripcion");
        txtDesc.setWrapText(true);
        txtDesc.setEditable(false);
        txtDesc.setPrefRowCount(3);
        txtDesc.setPrefWidth(330);

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color: #4facfe; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnCerrar.setOnAction(e -> ventana.close());
        
        HBox boxCerrar = new HBox(btnCerrar);
        boxCerrar.setAlignment(Pos.CENTER);
        boxCerrar.setPadding(new Insets(15, 0, 0, 0));

        contenido.getChildren().addAll(lblNombre, lblEstado, lblSector, lblUbicacion, lblPlazas, sep, lblDescTitulo, txtDesc, boxCerrar);

        Scene scene = new Scene(contenido, 380, 380);
        ventana.setScene(scene);
        ventana.show();
    }

    @FXML
    private void volverAEmpresas() {
        cambiarVista("/fxml/empresas.fxml", controller -> ((EmpresasController) controller).setUsuario(usuario));
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
            Stage stage = (Stage) listaResultados.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}