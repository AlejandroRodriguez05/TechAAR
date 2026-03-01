package com.practicas.controller;

import com.practicas.model.Departamento;
import com.practicas.model.Empresa;
import com.practicas.service.DepartamentoService;
import com.practicas.service.EmpresaService;
import com.practicas.util.CardFactory;
import com.practicas.util.ViewManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BusquedaController {

    @FXML private TextField txtBusqueda;
    @FXML private ComboBox<Departamento> cmbDepartamento;
    @FXML private Label lblResultados;
    @FXML private VBox listaResultados;

    private List<Empresa> todasEmpresas = new ArrayList<>();
    private List<Departamento> departamentos = new ArrayList<>();
    private Timer debounceTimer;

    @FXML
    public void initialize() {
        // Cargar datos iniciales
        new Thread(() -> {
            try {
                List<Empresa> emps = EmpresaService.getAll();
                List<Departamento> deps = DepartamentoService.getAll();
                Platform.runLater(() -> {
                    todasEmpresas = emps != null ? emps : new ArrayList<>();
                    departamentos = deps != null ? deps : new ArrayList<>();
                    configurarComboBox();
                    filtrar();
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblResultados.setText("Error cargando datos"));
            }
        }).start();

        // Debounce en búsqueda por texto
        txtBusqueda.textProperty().addListener((obs, old, newVal) -> {
            if (debounceTimer != null) debounceTimer.cancel();
            debounceTimer = new Timer();
            debounceTimer.schedule(new TimerTask() {
                @Override public void run() { Platform.runLater(() -> filtrar()); }
            }, 300);
        });
    }

    private void configurarComboBox() {
        // Opción "Todos" + departamentos
        List<Departamento> opciones = new ArrayList<>();
        Departamento todos = new Departamento();
        todos.setId(0);
        todos.setNombre("Todos");
        todos.setCodigo("Todos");
        opciones.add(todos);
        opciones.addAll(departamentos);

        cmbDepartamento.setItems(FXCollections.observableArrayList(opciones));
        cmbDepartamento.setConverter(new StringConverter<>() {
            @Override public String toString(Departamento d) { return d == null ? "Todos" : d.getCodigo(); }
            @Override public Departamento fromString(String s) { return null; }
        });
        cmbDepartamento.getSelectionModel().selectFirst();
    }

    @FXML
    private void filtrarPorDepartamento() {
        filtrar();
    }

    @FXML
    private void limpiarBusqueda() {
        txtBusqueda.clear();
    }

    private void filtrar() {
        String query = txtBusqueda.getText() != null ? txtBusqueda.getText().trim().toLowerCase() : "";
        Departamento deptoSel = cmbDepartamento.getValue();
        long deptoId = deptoSel != null ? deptoSel.getId() : 0;

        List<Empresa> filtradas = new ArrayList<>();
        for (Empresa e : todasEmpresas) {
            // Filtro texto
            if (!query.isEmpty()) {
                boolean match = (e.getNombre() != null && e.getNombre().toLowerCase().contains(query))
                        || (e.getCiudad() != null && e.getCiudad().toLowerCase().contains(query))
                        || (e.getDescripcion() != null && e.getDescripcion().toLowerCase().contains(query));
                if (!match) continue;
            }
            // Filtro departamento
            if (deptoId > 0) {
                boolean tieneDepto = e.getDepartamentos().stream().anyMatch(d -> d.getId() == deptoId);
                if (!tieneDepto) continue;
            }
            filtradas.add(e);
        }

        lblResultados.setText(filtradas.size() + " empresa" + (filtradas.size() != 1 ? "s" : "") + " encontrada" + (filtradas.size() != 1 ? "s" : ""));

        listaResultados.getChildren().clear();
        for (Empresa e : filtradas) {
            listaResultados.getChildren().add(
                    CardFactory.crearEmpresaCard(e, this::abrirDetalle));
        }
    }

    private void abrirDetalle(Empresa empresa) {
        ViewManager.navigateTo("empresa_detalle", "empresaId", empresa.getId());
    }

    // Navegación
    @FXML private void volverAEmpresas() { ViewManager.navigateTo("empresas"); }
    @FXML private void abrirListas() { ViewManager.navigateTo("listas"); }
    @FXML private void abrirPerfil() { ViewManager.navigateTo("perfil"); }
}
