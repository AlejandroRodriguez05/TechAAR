package com.practicas.controller;

import com.practicas.model.Curso;
import com.practicas.model.Departamento;
import com.practicas.model.Empresa;
import com.practicas.service.DepartamentoService;
import com.practicas.service.EmpresaService;
import com.practicas.util.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnadirEmpresaController {

    @FXML private Label lblTitulo;
    @FXML private Button btnGuardar;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtWeb;
    @FXML private TextField txtPersonaContacto;
    @FXML private TextField txtTelefonoContacto;
    @FXML private TextField txtEmailContacto;
    @FXML private VBox contenedorDepartamentos;
    @FXML private Label lblTotalPlazas;
    @FXML private Label lblError;

    private Long empresaId;
    private boolean editando = false;
    private List<Departamento> departamentos = new ArrayList<>();
    private Map<Long, List<Curso>> cursosPorDepto = new HashMap<>();
    private Map<Long, CheckBox> checksDepartamento = new HashMap<>();
    private Map<Long, Spinner<Integer>> spinnersGenerales = new HashMap<>();
    private Map<Long, Map<Long, CheckBox>> checksCursos = new HashMap<>();
    private Map<Long, Map<Long, Spinner<Integer>>> spinnersCursos = new HashMap<>();
    private Map<Long, ToggleGroup> toggleGroups = new HashMap<>();

    @FXML
    public void initialize() {
        empresaId = ViewManager.getParam("empresaId");
        ViewManager.clearParam("empresaId");
        editando = empresaId != null;

        lblTitulo.setText(editando ? "Editar Empresa" : "Nueva Empresa");
        ocultarError();

        cargarDepartamentos();
    }

    private void cargarDepartamentos() {
        new Thread(() -> {
            try {
                List<Departamento> deps = DepartamentoService.getAll();
                Map<Long, List<Curso>> cursosMap = new HashMap<>();
                for (Departamento d : deps) {
                    try {
                        cursosMap.put(d.getId(), DepartamentoService.getCursos(d.getId()));
                    } catch (Exception e) {
                        cursosMap.put(d.getId(), new ArrayList<>());
                    }
                }

                Empresa empresa = editando ? EmpresaService.getById(empresaId) : null;

                Platform.runLater(() -> {
                    departamentos = deps;
                    cursosPorDepto = cursosMap;
                    construirDepartamentosUI();
                    if (empresa != null) rellenarFormulario(empresa);
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error cargando datos: " + e.getMessage()));
            }
        }).start();
    }

    private void construirDepartamentosUI() {
        contenedorDepartamentos.getChildren().clear();

        for (Departamento depto : departamentos) {
            VBox deptoContainer = new VBox(8);
            deptoContainer.setPadding(new Insets(0, 0, 10, 0));

            // Checkbox departamento
            CheckBox chk = new CheckBox(depto.getCodigo() + " - " + depto.getNombre());
            chk.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-text-fill: #333;");
            checksDepartamento.put(depto.getId(), chk);

            // Panel expandible
            VBox panel = new VBox(10);
            panel.setPadding(new Insets(10, 0, 0, 30));
            panel.setVisible(false);
            panel.setManaged(false);
            panel.setStyle("-fx-border-color: #0d9488; -fx-border-width: 0 0 0 2; -fx-padding: 10 0 10 15;");

            // Toggle General / Por ciclo
            ToggleGroup tg = new ToggleGroup();
            RadioButton rbGeneral = new RadioButton("General");
            rbGeneral.setToggleGroup(tg);
            rbGeneral.setSelected(true);
            rbGeneral.setStyle("-fx-font-family: Arial; -fx-font-size: 13px;");
            RadioButton rbPorCiclo = new RadioButton("Por ciclo");
            rbPorCiclo.setToggleGroup(tg);
            rbPorCiclo.setStyle("-fx-font-family: Arial; -fx-font-size: 13px;");
            HBox toggleBox = new HBox(15, rbGeneral, rbPorCiclo);
            toggleGroups.put(depto.getId(), tg);

            // Spinner general
            Spinner<Integer> spinnerGen = new Spinner<>(1, 50, 1);
            spinnerGen.setEditable(true);
            spinnerGen.setPrefWidth(100);
            HBox generalBox = new HBox(10, new Label("Plazas:"), spinnerGen);
            generalBox.setAlignment(Pos.CENTER_LEFT);
            spinnersGenerales.put(depto.getId(), spinnerGen);

            // Cursos
            VBox cursosBox = new VBox(6);
            cursosBox.setVisible(false);
            cursosBox.setManaged(false);
            Map<Long, CheckBox> cursosChecks = new HashMap<>();
            Map<Long, Spinner<Integer>> cursosSpinners = new HashMap<>();

            List<Curso> cursos = cursosPorDepto.getOrDefault(depto.getId(), new ArrayList<>());
            for (Curso curso : cursos) {
                CheckBox cursoChk = new CheckBox(curso.getSiglas());
                cursoChk.setStyle("-fx-font-family: Arial; -fx-font-size: 13px;");
                Spinner<Integer> cursoSpinner = new Spinner<>(1, 50, 1);
                cursoSpinner.setPrefWidth(80);
                cursoSpinner.setVisible(false);
                cursoSpinner.setManaged(false);

                cursoChk.selectedProperty().addListener((obs, old, sel) -> {
                    cursoSpinner.setVisible(sel);
                    cursoSpinner.setManaged(sel);
                    actualizarTotalPlazas();
                });
                cursoSpinner.valueProperty().addListener((obs, old, val) -> actualizarTotalPlazas());

                HBox cursoRow = new HBox(10, cursoChk, cursoSpinner);
                cursoRow.setAlignment(Pos.CENTER_LEFT);
                cursosBox.getChildren().add(cursoRow);
                cursosChecks.put(curso.getId(), cursoChk);
                cursosSpinners.put(curso.getId(), cursoSpinner);
            }
            checksCursos.put(depto.getId(), cursosChecks);
            spinnersCursos.put(depto.getId(), cursosSpinners);

            // Toggle cambio
            tg.selectedToggleProperty().addListener((obs, old, newToggle) -> {
                boolean general = newToggle == rbGeneral;
                generalBox.setVisible(general);
                generalBox.setManaged(general);
                cursosBox.setVisible(!general);
                cursosBox.setManaged(!general);
                actualizarTotalPlazas();
            });

            panel.getChildren().addAll(toggleBox, generalBox, cursosBox);

            // Checkbox toggle panel
            chk.selectedProperty().addListener((obs, old, sel) -> {
                panel.setVisible(sel);
                panel.setManaged(sel);
                actualizarTotalPlazas();
            });

            spinnerGen.valueProperty().addListener((obs, old, val) -> actualizarTotalPlazas());

            deptoContainer.getChildren().addAll(chk, panel);
            contenedorDepartamentos.getChildren().add(deptoContainer);
        }
    }

    private void rellenarFormulario(Empresa e) {
        txtNombre.setText(e.getNombre());
        txtDescripcion.setText(e.getDescripcion());
        txtDireccion.setText(e.getDireccion());
        txtCiudad.setText(e.getCiudad());
        txtCodigoPostal.setText(e.getCodigoPostal());
        txtTelefono.setText(e.getTelefono());
        txtEmail.setText(e.getEmail());
        txtWeb.setText(e.getWeb());
        txtPersonaContacto.setText(e.getPersonaContacto());
        txtTelefonoContacto.setText(e.getTelefonoContacto());
        txtEmailContacto.setText(e.getEmailContacto());

        // Marcar departamentos
        for (Departamento d : e.getDepartamentos()) {
            CheckBox chk = checksDepartamento.get(d.getId());
            if (chk != null) chk.setSelected(true);
        }
    }

    private void actualizarTotalPlazas() {
        int total = 0;
        for (Departamento depto : departamentos) {
            CheckBox chk = checksDepartamento.get(depto.getId());
            if (chk == null || !chk.isSelected()) continue;

            ToggleGroup tg = toggleGroups.get(depto.getId());
            boolean general = tg.getSelectedToggle() != null
                    && ((RadioButton) tg.getSelectedToggle()).getText().equals("General");

            if (general) {
                Spinner<Integer> s = spinnersGenerales.get(depto.getId());
                if (s != null) total += s.getValue();
            } else {
                Map<Long, CheckBox> cc = checksCursos.get(depto.getId());
                Map<Long, Spinner<Integer>> cs = spinnersCursos.get(depto.getId());
                if (cc != null && cs != null) {
                    for (var entry : cc.entrySet()) {
                        if (entry.getValue().isSelected()) {
                            Spinner<Integer> s = cs.get(entry.getKey());
                            if (s != null) total += s.getValue();
                        }
                    }
                }
            }
        }
        lblTotalPlazas.setText("Total plazas: " + total);
    }

    @FXML
    private void guardarEmpresa() {
        String nombre = txtNombre.getText().trim();
        String ciudad = txtCiudad.getText().trim();

        if (nombre.isEmpty()) { mostrarError("El nombre es obligatorio"); return; }
        if (ciudad.isEmpty()) { mostrarError("La ciudad es obligatoria"); return; }

        ocultarError();
        btnGuardar.setDisable(true);

        Empresa empresa = new Empresa();
        empresa.setNombre(nombre);
        empresa.setDescripcion(txtDescripcion.getText());
        empresa.setDireccion(txtDireccion.getText());
        empresa.setCiudad(ciudad);
        empresa.setCodigoPostal(txtCodigoPostal.getText());
        empresa.setTelefono(txtTelefono.getText());
        empresa.setEmail(txtEmail.getText());
        empresa.setWeb(txtWeb.getText());
        empresa.setPersonaContacto(txtPersonaContacto.getText());
        empresa.setTelefonoContacto(txtTelefonoContacto.getText());
        empresa.setEmailContacto(txtEmailContacto.getText());
        empresa.setActiva(true);

        // Departamentos seleccionados
        List<Departamento> selectedDeps = new ArrayList<>();
        for (Departamento d : departamentos) {
            CheckBox chk = checksDepartamento.get(d.getId());
            if (chk != null && chk.isSelected()) selectedDeps.add(d);
        }
        empresa.setDepartamentos(selectedDeps);

        new Thread(() -> {
            try {
                if (editando) {
                    EmpresaService.actualizar(empresaId, empresa);
                } else {
                    EmpresaService.crear(empresa);
                }
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Éxito");
                    alert.setHeaderText(null);
                    alert.setContentText(editando ? "Empresa actualizada" : "Empresa añadida correctamente");
                    alert.showAndWait();
                    ViewManager.navigateTo("empresas");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarError(e.getMessage());
                    btnGuardar.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void cancelar() {
        ViewManager.navigateTo("empresas");
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void ocultarError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }
}
