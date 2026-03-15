package com.practicas.controller;

import com.practicas.model.Empresa;
import com.practicas.model.Usuario;
import com.practicas.service.EmpresaService;
import com.practicas.util.CardFactory;
import com.practicas.util.Session;
import com.practicas.util.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EmpresasController {

    @FXML private Label lblSaludo;
    @FXML private Label lblInfo;
    @FXML private Button btnAdd;
    @FXML private Label filtroNuevas;
    @FXML private Label filtroTop;
    @FXML private Label filtroCercanas;
    @FXML private Label lblTituloSeccion;
    @FXML private Label lblBadge;
    @FXML private ScrollPane scrollEmpresas;
    @FXML private VBox listaEmpresas;

    private List<Empresa> todasEmpresas = new ArrayList<>();
    private String filtroActual = "todas";

    @FXML
    public void initialize() {
        Usuario user = Session.get().getUsuario();

        // Saludo
        lblSaludo.setText("Hola, " + (user != null ? user.getNombre() : "Usuario") + " \uD83D\uDC4B");
        lblInfo.setText((user != null && user.esProfesor() ? "Profesor" : "Alumno") + " • Cargando...");

        // Botón añadir solo para profesores
        boolean prof = Session.get().esProfesor();
        btnAdd.setVisible(prof);
        btnAdd.setManaged(prof);

        cargarEmpresas();
    }

    private void cargarEmpresas() {
        new Thread(() -> {
            try {
                List<Empresa> empresas = EmpresaService.getAll();
                Platform.runLater(() -> {
                    todasEmpresas = empresas != null ? empresas : new ArrayList<>();
                    lblInfo.setText((Session.get().esProfesor() ? "Profesor" : "Alumno")
                            + " • " + todasEmpresas.size() + " empresas");
                    aplicarFiltro();
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblInfo.setText("Error al cargar empresas"));
            }
        }).start();
    }

    private void aplicarFiltro() {
        List<Empresa> filtradas;
        switch (filtroActual) {
            case "nuevas" -> {
                filtradas = new ArrayList<>(todasEmpresas);
                filtradas.sort(Comparator.comparingLong(Empresa::getId).reversed());
                lblTituloSeccion.setText("Más recientes");
            }
            case "top" -> {
                filtradas = new ArrayList<>(todasEmpresas.stream()
                        .filter(e -> e.getValoracionMedia() != null && e.getValoracionMedia() > 0)
                        .sorted(Comparator.comparingDouble((Empresa e) ->
                                e.getValoracionMedia() != null ? e.getValoracionMedia() : 0).reversed())
                        .toList());
                lblTituloSeccion.setText("Mejor valoradas");
            }
            default -> {
                filtradas = new ArrayList<>(todasEmpresas);
                lblTituloSeccion.setText("Todas las empresas");
            }
        }

        lblBadge.setText(String.valueOf(filtradas.size()));
        listaEmpresas.getChildren().clear();
        for (Empresa e : filtradas) {
            listaEmpresas.getChildren().add(
                    CardFactory.crearEmpresaCard(e, this::abrirDetalle));
        }
        scrollEmpresas.setVvalue(0);
    }

    @FXML
    private void filtrarNuevas() {
        filtroActual = filtroActual.equals("nuevas") ? "todas" : "nuevas";
        resetFiltroStyles();
        if (filtroActual.equals("nuevas")) resaltarFiltro(filtroNuevas);
        aplicarFiltro();
    }

    @FXML
    private void filtrarTopValoradas() {
        filtroActual = filtroActual.equals("top") ? "todas" : "top";
        resetFiltroStyles();
        if (filtroActual.equals("top")) resaltarFiltro(filtroTop);
        aplicarFiltro();
    }

    @FXML
    private void filtrarCercanas() {
        // Placeholder
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Próximamente");
        alert.setHeaderText(null);
        alert.setContentText("La función de empresas cercanas estará disponible en futuras versiones.");
        alert.showAndWait();
    }

    private void resetFiltroStyles() {
        String normal = "-fx-background-color: rgba(255,255,255,0.25); -fx-background-radius: 20; " +
                "-fx-padding: 9 18; -fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: white; -fx-cursor: hand;";
        filtroNuevas.setStyle(normal);
        filtroTop.setStyle(normal);
        filtroCercanas.setStyle(normal);
    }

    private void resaltarFiltro(Label filtro) {
        filtro.setStyle(filtro.getStyle().replace("rgba(255,255,255,0.2)", "rgba(255,255,255,0.5)"));
    }

    @FXML
    private void mostrarDialogoAnadir() {
        ViewManager.navigateTo("anadir_empresa");
    }

    private void abrirDetalle(Empresa empresa) {
        ViewManager.navigateTo("empresa_detalle", "empresaId", empresa.getId());
    }

    // ─── Navegación ───
    @FXML private void abrirBusqueda() { ViewManager.navigateTo("busqueda"); }
    @FXML private void mostrarMisListas() { ViewManager.navigateTo("listas"); }
    @FXML private void abrirPerfil() { ViewManager.navigateTo("perfil"); }
}
