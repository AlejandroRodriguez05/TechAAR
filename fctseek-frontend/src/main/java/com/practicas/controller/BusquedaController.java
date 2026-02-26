package com.practicas.controller;

import com.practicas.model.DataService;
import com.practicas.model.Empresa;
import com.practicas.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        VBox tarjeta = new VBox(8);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);");

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
        lblEstado.setStyle("-fx-background-color: " + (empresa.estaOcupada() ? "#FF5722" : "#4CAF50") + "; -fx-background-radius: 4;");

        fila1.getChildren().addAll(lblNombre, spacer, lblEstado);

        HBox fila2 = new HBox(15);
        fila2.setAlignment(Pos.CENTER_LEFT);

        Label lblUbicacion = new Label(empresa.getUbicacion());
        lblUbicacion.setFont(Font.font("Arial", 12));
        lblUbicacion.setTextFill(Color.GRAY);

        Label lblSector = new Label(empresa.getSector());
        lblSector.setFont(Font.font("Arial", 12));
        lblSector.setTextFill(Color.GRAY);

        fila2.getChildren().addAll(lblUbicacion, lblSector);

        Label lblPlazas = new Label("Plazas: " + empresa.getEstadoPlazas());
        lblPlazas.setFont(Font.font("Arial", 12));
        lblPlazas.setTextFill(Color.web("#666666"));

        tarjeta.getChildren().addAll(fila1, fila2, lblPlazas);

        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #BDBDBD; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 15, 0, 0, 5); -fx-cursor: hand;"));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"));
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

        Label lblEstado = new Label(empresa.estaOcupada() ? "OCUPADA" : "DISPONIBLE");
        lblEstado.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblEstado.setTextFill(empresa.estaOcupada() ? Color.RED : Color.GREEN);

        Label lblSector = new Label("Sector: " + empresa.getSector());
        Label lblUbicacion = new Label("Ubicacion: " + empresa.getUbicacion());
        Label lblPlazas = new Label("Plazas: " + empresa.getEstadoPlazas());

        TextArea txtDesc = new TextArea(empresa.getDescripcion());
        txtDesc.setWrapText(true);
        txtDesc.setEditable(false);
        txtDesc.setPrefRowCount(3);

        contenido.getChildren().addAll(lblNombre, lblEstado, lblSector, lblUbicacion, lblPlazas, new Separator(), new Label("Descripcion:"), txtDesc);

        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
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
