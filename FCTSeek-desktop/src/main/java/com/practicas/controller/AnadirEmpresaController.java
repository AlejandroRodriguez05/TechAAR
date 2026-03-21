package com.practicas.controller;

import com.practicas.model.Curso;
import com.practicas.model.Departamento;
import com.practicas.model.Empresa;
import com.practicas.service.DepartamentoService;
import com.practicas.service.EmpresaService;
import com.practicas.service.PlazaService;
import com.practicas.util.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    @FXML private VBox contenedorCursos;
    @FXML private VBox contenedorDepartamentos;
    @FXML private Label lblTotalPlazas;
    @FXML private Label lblError;

    private Long empresaId;
    private boolean editando = false;
    private List<Departamento> departamentos = new ArrayList<>();
    private Map<Long, List<Curso>> cursosPorDepto = new HashMap<>();

    private Map<Long, CheckBox> checksDeptos = new HashMap<>();

    private Map<Long, CheckBox> checksDeptoPlaza = new HashMap<>();
    private Map<Long, ToggleGroup> toggleGroups = new HashMap<>();
    private Map<Long, Spinner<Integer>> spinnersGenerales = new HashMap<>();
    private Map<Long, Map<Long, CheckBox>> checksPlazaCurso = new HashMap<>();
    private Map<Long, Map<Long, Spinner<Integer>>> spinnersPlazaCurso = new HashMap<>();

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
                    try { cursosMap.put(d.getId(), DepartamentoService.getCursos(d.getId())); }
                    catch (Exception e) { cursosMap.put(d.getId(), new ArrayList<>()); }
                }
                Empresa empresa = editando ? EmpresaService.getById(empresaId) : null;

                Platform.runLater(() -> {
                    departamentos = deps;
                    cursosPorDepto = cursosMap;
                    construirSeccionDepartamentos();
                    construirSeccionPlazas();
                    if (empresa != null) rellenarFormulario(empresa);
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error cargando datos: " + e.getMessage()));
            }
        }).start();
    }

    private void construirSeccionDepartamentos() {
        contenedorCursos.getChildren().clear();
        checksDeptos.clear();

        for (Departamento depto : departamentos) {
            CheckBox cb = new CheckBox();
            Label lbl = new Label(depto.getCodigo() + " — " + depto.getNombre());
            lbl.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #333333;");

            HBox fila = new HBox(10, cb, lbl);
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setPadding(new Insets(5, 0, 5, 8));
            fila.setStyle("-fx-cursor: hand;");
            fila.setOnMouseClicked((MouseEvent e) -> {
                if (!(e.getTarget() instanceof CheckBox)) {
                    cb.setSelected(!cb.isSelected());
                }
            });

            checksDeptos.put(depto.getId(), cb);
            contenedorCursos.getChildren().add(fila);
        }
    }

    private void construirSeccionPlazas() {
        contenedorDepartamentos.getChildren().clear();
        checksDeptoPlaza.clear();
        toggleGroups.clear();
        spinnersGenerales.clear();
        checksPlazaCurso.clear();
        spinnersPlazaCurso.clear();

        for (Departamento depto : departamentos) {
            VBox deptoContainer = new VBox(8);
            deptoContainer.setPadding(new Insets(0, 0, 10, 0));

            CheckBox chk = new CheckBox();
            Label lblNombre = new Label(depto.getCodigo() + " — " + depto.getNombre());
            lblNombre.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-text-fill: #333333;");
            HBox chkRow = new HBox(10, chk, lblNombre);
            chkRow.setAlignment(Pos.CENTER_LEFT);
            chkRow.setStyle("-fx-cursor: hand;");
            chkRow.setOnMouseClicked((MouseEvent e) -> {
                if (!(e.getTarget() instanceof CheckBox)) {
                    chk.setSelected(!chk.isSelected());
                }
            });
            checksDeptoPlaza.put(depto.getId(), chk);

            VBox panel = new VBox(10);
            panel.setPadding(new Insets(10, 0, 0, 30));
            panel.setVisible(false);
            panel.setManaged(false);
            panel.setStyle("-fx-border-color: #0d9488; -fx-border-width: 0 0 0 2; -fx-padding: 10 0 10 15;");

            ToggleGroup tg = new ToggleGroup();
            RadioButton rbGeneral = new RadioButton();
            rbGeneral.setToggleGroup(tg);
            rbGeneral.setSelected(true);
            Label lblGeneral = new Label("General");
            lblGeneral.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #333333; -fx-cursor: hand;");
            lblGeneral.setOnMouseClicked(e -> rbGeneral.setSelected(true));
            RadioButton rbPorCiclo = new RadioButton();
            rbPorCiclo.setToggleGroup(tg);
            Label lblPorCiclo = new Label("Por ciclo");
            lblPorCiclo.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #333333; -fx-cursor: hand;");
            lblPorCiclo.setOnMouseClicked(e -> rbPorCiclo.setSelected(true));
            HBox toggleBox = new HBox(8, rbGeneral, lblGeneral, new Region() {{ setMinWidth(12); }}, rbPorCiclo, lblPorCiclo);
            toggleBox.setAlignment(Pos.CENTER_LEFT);
            toggleGroups.put(depto.getId(), tg);

            Spinner<Integer> spinnerGen = new Spinner<>(1, 99, 1);
            spinnerGen.setEditable(true);
            spinnerGen.setPrefWidth(90);
            Label lblPlazas = new Label("Plazas:");
            lblPlazas.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #333333;");
            HBox generalBox = new HBox(10, lblPlazas, spinnerGen);
            generalBox.setAlignment(Pos.CENTER_LEFT);
            spinnersGenerales.put(depto.getId(), spinnerGen);

            VBox cursosBox = new VBox(6);
            cursosBox.setVisible(false);
            cursosBox.setManaged(false);
            Map<Long, CheckBox> cursoChecks = new HashMap<>();
            Map<Long, Spinner<Integer>> cursoSpinners = new HashMap<>();

            List<Curso> cursos = cursosPorDepto.getOrDefault(depto.getId(), new ArrayList<>());
            for (Curso curso : cursos) {
                CheckBox cursoChk = new CheckBox();
                Label cursoLbl = new Label(curso.getSiglas() + " — " + curso.getNombre());
                cursoLbl.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #333333;");
                Spinner<Integer> cursoSpinner = new Spinner<>(1, 99, 1);
                cursoSpinner.setPrefWidth(80);
                cursoSpinner.setVisible(false);
                cursoSpinner.setManaged(false);

                cursoChk.selectedProperty().addListener((obs, old, sel) -> {
                    cursoSpinner.setVisible(sel);
                    cursoSpinner.setManaged(sel);
                    actualizarTotalPlazas();
                });
                cursoSpinner.valueProperty().addListener((obs, old, val) -> actualizarTotalPlazas());

                HBox cursoRow = new HBox(10, cursoChk, cursoLbl, cursoSpinner);
                cursoRow.setAlignment(Pos.CENTER_LEFT);
                cursoRow.setStyle("-fx-cursor: hand;");
                cursoRow.setOnMouseClicked((MouseEvent e) -> {
                    if (!(e.getTarget() instanceof CheckBox)) {
                        cursoChk.setSelected(!cursoChk.isSelected());
                    }
                });

                cursosBox.getChildren().add(cursoRow);
                cursoChecks.put(curso.getId(), cursoChk);
                cursoSpinners.put(curso.getId(), cursoSpinner);
            }
            checksPlazaCurso.put(depto.getId(), cursoChecks);
            spinnersPlazaCurso.put(depto.getId(), cursoSpinners);

            tg.selectedToggleProperty().addListener((obs, old, newToggle) -> {
                boolean general = newToggle == rbGeneral;
                generalBox.setVisible(general);
                generalBox.setManaged(general);
                cursosBox.setVisible(!general);
                cursosBox.setManaged(!general);
                actualizarTotalPlazas();
            });

            spinnerGen.valueProperty().addListener((obs, old, val) -> actualizarTotalPlazas());
            panel.getChildren().addAll(toggleBox, generalBox, cursosBox);

            chk.selectedProperty().addListener((obs, old, sel) -> {
                panel.setVisible(sel);
                panel.setManaged(sel);
                actualizarTotalPlazas();
            });

            deptoContainer.getChildren().addAll(chkRow, panel);
            contenedorDepartamentos.getChildren().add(deptoContainer);
        }
    }

    private void rellenarFormulario(Empresa e) {
        txtNombre.setText(e.getNombre() != null ? e.getNombre() : "");
        txtDescripcion.setText(e.getDescripcion() != null ? e.getDescripcion() : "");
        txtDireccion.setText(e.getDireccion() != null ? e.getDireccion() : "");
        txtCiudad.setText(e.getCiudad() != null ? e.getCiudad() : "");
        txtCodigoPostal.setText(e.getCodigoPostal() != null ? e.getCodigoPostal() : "");
        txtTelefono.setText(e.getTelefono() != null ? e.getTelefono() : "");
        txtEmail.setText(e.getEmail() != null ? e.getEmail() : "");
        txtWeb.setText(e.getWeb() != null ? e.getWeb() : "");
        txtPersonaContacto.setText(e.getPersonaContacto() != null ? e.getPersonaContacto() : "");
        txtTelefonoContacto.setText(e.getTelefonoContacto() != null ? e.getTelefonoContacto() : "");
        txtEmailContacto.setText(e.getEmailContacto() != null ? e.getEmailContacto() : "");

        for (Departamento d : e.getDepartamentos()) {
            CheckBox cb = checksDeptos.get(d.getId());
            if (cb != null) cb.setSelected(true);
        }
    }

    private void actualizarTotalPlazas() {
        int total = 0;
        for (Departamento depto : departamentos) {
            CheckBox chk = checksDeptoPlaza.get(depto.getId());
            if (chk == null || !chk.isSelected()) continue;
            ToggleGroup tg = toggleGroups.get(depto.getId());
            if (tg == null || tg.getSelectedToggle() == null) continue;
            boolean general = tg.getSelectedToggle() == tg.getToggles().get(0);
            if (general) {
                Spinner<Integer> s = spinnersGenerales.get(depto.getId());
                if (s != null) total += s.getValue();
            } else {
                Map<Long, CheckBox> cc = checksPlazaCurso.get(depto.getId());
                Map<Long, Spinner<Integer>> cs = spinnersPlazaCurso.get(depto.getId());
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

        List<Long> departamentosIds = checksDeptos.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Map<String, Object> request = new HashMap<>();
        request.put("nombre", nombre);
        request.put("ciudad", ciudad);
        request.put("descripcion", txtDescripcion.getText());
        request.put("direccion", txtDireccion.getText());
        request.put("codigoPostal", txtCodigoPostal.getText());
        request.put("telefono", txtTelefono.getText());
        request.put("email", txtEmail.getText());
        request.put("web", txtWeb.getText());
        request.put("personaContacto", txtPersonaContacto.getText());
        request.put("telefonoContacto", txtTelefonoContacto.getText());
        request.put("emailContacto", txtEmailContacto.getText());
        request.put("activa", true);
        request.put("departamentosIds", departamentosIds);

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        String cursoAcademico = (month >= 9 ? year : year - 1) + "-" + (month >= 9 ? year + 1 : year);

        List<Map<String, Object>> plazasACrear = new ArrayList<>();
        for (Departamento depto : departamentos) {
            CheckBox chk = checksDeptoPlaza.get(depto.getId());
            if (chk == null || !chk.isSelected()) continue;
            ToggleGroup tg = toggleGroups.get(depto.getId());
            if (tg == null || tg.getSelectedToggle() == null) continue;
            boolean general = tg.getSelectedToggle() == tg.getToggles().get(0);
            if (general) {
                Spinner<Integer> s = spinnersGenerales.get(depto.getId());
                if (s != null) {
                    Map<String, Object> plaza = new HashMap<>();
                    plaza.put("departamentoId", depto.getId());
                    plaza.put("cursoId", null);
                    plaza.put("cantidad", s.getValue());
                    plaza.put("cursoAcademico", cursoAcademico);
                    plazasACrear.add(plaza);
                }
            } else {
                Map<Long, CheckBox> cc = checksPlazaCurso.get(depto.getId());
                Map<Long, Spinner<Integer>> cs = spinnersPlazaCurso.get(depto.getId());
                if (cc != null && cs != null) {
                    for (var entry : cc.entrySet()) {
                        if (entry.getValue().isSelected()) {
                            Spinner<Integer> s = cs.get(entry.getKey());
                            if (s != null) {
                                Map<String, Object> plaza = new HashMap<>();
                                plaza.put("departamentoId", depto.getId());
                                plaza.put("cursoId", entry.getKey());
                                plaza.put("cantidad", s.getValue());
                                plaza.put("cursoAcademico", cursoAcademico);
                                plazasACrear.add(plaza);
                            }
                        }
                    }
                }
            }
        }

        new Thread(() -> {
            try {
                long savedId;
                if (editando) {
                    EmpresaService.actualizarConRequest(empresaId, request);
                    savedId = empresaId;
                } else {
                    savedId = EmpresaService.crearConRequest(request);
                }

                final long fSavedId = savedId;
                for (Map<String, Object> plaza : plazasACrear) {
                    plaza.put("empresaId", fSavedId);
                    PlazaService.crearConRequest(plaza);
                }

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Éxito");
                    alert.setHeaderText(null);
                    alert.setContentText(editando ? "Empresa actualizada" : "Empresa creada correctamente");
                    alert.showAndWait();
                    ViewManager.navigateTo("empresa_detalle", "empresaId", fSavedId);
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