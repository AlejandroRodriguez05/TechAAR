package com.practicas.controller;

import com.practicas.model.*;
import com.practicas.service.*;
import com.practicas.util.Session;
import com.practicas.util.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmpresaDetalleController {

    @FXML private Label lblNombreEmpresa;
    @FXML private Label lblEstrellas;
    @FXML private Label lblValoracion;
    @FXML private Label lblNumValoraciones;
    @FXML private HBox contenedorChipsDpto;
    @FXML private VBox tarjetaPlazas;
    @FXML private VBox contenedorPlazas;
    @FXML private VBox listaContactados;
    @FXML private Label lblDireccion;
    @FXML private Label lblTelefono;
    @FXML private Label lblEmail;
    @FXML private Label lblPersonaContacto;
    @FXML private Label lblDescripcion;
    @FXML private VBox listaCiclos;
    @FXML private Button btnTabGeneral;
    @FXML private Button btnTabProfesores;
    @FXML private VBox listaComentarios;
    @FXML private Label iconFavorito;
    @FXML private Label lblFavoritoTexto;
    @FXML private VBox boxFavorito;
    @FXML private Button btnEditarInfo;
    @FXML private Button btnMarcarContactado;
    @FXML private Region sepEditar;
    @FXML private Button btnAnadirALista;

    private Empresa empresa;
    private long empresaIdCargada;
    private List<Comentario> comentarios = new ArrayList<>();
    private List<Plaza> plazas = new ArrayList<>();
    private List<Reserva> reservas = new ArrayList<>();
    private boolean esFavorito = false;
    private String tabComentarios = "general";

    @FXML
    public void initialize() {
        Long empresaId = ViewManager.getParam("empresaId");
        if (empresaId == null) return;
        ViewManager.clearParam("empresaId");

        boolean prof = Session.get().esProfesor();
        btnEditarInfo.setVisible(prof);
        btnEditarInfo.setManaged(prof);
        sepEditar.setVisible(prof);
        sepEditar.setManaged(prof);
        btnMarcarContactado.setVisible(prof);
        btnMarcarContactado.setManaged(prof);

        empresaIdCargada = empresaId;
        cargarDatos(empresaId);
    }

    private void cargarDatos(long empresaId) {
        new Thread(() -> {
            try {
                Empresa emp = EmpresaService.getById(empresaId);
                List<Comentario> coms = new ArrayList<>();
                List<Plaza> plz = new ArrayList<>();
                List<Reserva> rvs = new ArrayList<>();
                List<EmpresaContactada> contactados = new ArrayList<>();

                try { coms = ComentarioService.getByEmpresa(empresaId); } catch (Exception ignored) {}
                try { plz = PlazaService.getByEmpresa(empresaId); } catch (Exception ignored) {}
                try { rvs = ReservaService.getByEmpresa(empresaId); } catch (Exception ignored) {}
                try { contactados = EmpresaContactadaService.getByEmpresa(empresaId); } catch (Exception ignored) {}

                boolean fav = false;
                try { fav = FavoritoService.isFavorito(empresaId); } catch (Exception ignored) {}

                final Empresa fEmp = emp;
                final List<Comentario> fComs = coms;
                final List<Plaza> fPlz = plz;
                final List<Reserva> fRvs = rvs;
                final List<EmpresaContactada> fContactados = contactados;
                final boolean fFav = fav;

                Platform.runLater(() -> {
                    empresa = fEmp;
                    comentarios = fComs != null ? fComs : new ArrayList<>();
                    plazas = fPlz != null ? fPlz : new ArrayList<>();
                    reservas = fRvs != null ? fRvs : new ArrayList<>();
                    esFavorito = fFav;
                    rellenarContactados(fContactados != null ? fContactados : new ArrayList<>());
                    rellenarDatos();
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblNombreEmpresa.setText("Error cargando empresa"));
            }
        }).start();
    }

    private void rellenarContactados(List<EmpresaContactada> contactados) {
        listaContactados.getChildren().clear();
        if (contactados.isEmpty()) {
            Label vacio = new Label("Ningún departamento ha contactado esta empresa aún.");
            vacio.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #888888;");
            listaContactados.getChildren().add(vacio);
            return;
        }
        for (int i = 0; i < contactados.size(); i++) {
            EmpresaContactada c = contactados.get(i);
            HBox fila = new HBox(10);
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setPadding(new Insets(6, 0, 6, 0));

            Label icono = new Label("👤");
            icono.setStyle("-fx-font-size: 16px;");

            VBox textos = new VBox(2);
            Label profesor = new Label(c.getProfesorNombre() != null ? c.getProfesorNombre() : "Profesor");
            profesor.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
            Label depto = new Label((c.getDepartamentoNombre() != null ? c.getDepartamentoNombre() : "")
                    + (c.getFecha() != null ? " · " + c.getFecha() : ""));
            depto.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #888888;");
            textos.getChildren().addAll(profesor, depto);
            fila.getChildren().addAll(icono, textos);
            listaContactados.getChildren().add(fila);

            if (i < contactados.size() - 1) {
                Separator sep = new Separator();
                sep.setStyle("-fx-background-color: #eeeeee;");
                listaContactados.getChildren().add(sep);
            }
        }
    }

    private void rellenarDatos() {
        if (empresa == null) return;

        lblNombreEmpresa.setText(empresa.getNombre());
        lblEstrellas.setText(empresa.getEstrellasTexto());
        lblValoracion.setText(empresa.getValoracionMedia() != null
                ? String.format("%.1f", empresa.getValoracionMedia()) : "-");
        lblNumValoraciones.setText("(" + (empresa.getTotalValoraciones() != null
                ? empresa.getTotalValoraciones() : 0) + " valoraciones)");

        contenedorChipsDpto.getChildren().clear();
        for (Departamento d : empresa.getDepartamentos()) {
            Label chip = new Label(d.getCodigo() != null ? d.getCodigo() : d.getNombre());
            chip.setStyle("-fx-background-color: rgba(255,255,255,0.25); -fx-background-radius: 10; "
                    + "-fx-padding: 5 12; -fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: white;");
            contenedorChipsDpto.getChildren().add(chip);
        }

        List<Plaza> plazasReales = plazas.stream()
                .filter(p -> p.getCantidad() > 0)
                .collect(Collectors.toList());
        if (plazasReales.isEmpty()) {
            tarjetaPlazas.setVisible(false);
            tarjetaPlazas.setManaged(false);
        } else {
            tarjetaPlazas.setVisible(true);
            tarjetaPlazas.setManaged(true);
            contenedorPlazas.getChildren().clear();
            for (Plaza p : plazasReales) {
                contenedorPlazas.getChildren().add(crearPlazaCard(p));
            }
        }

        lblDireccion.setText(formatDireccion());
        lblTelefono.setText(empresa.getTelefono() != null ? empresa.getTelefono() : "-");
        lblEmail.setText(empresa.getEmail() != null ? empresa.getEmail() : "-");
        lblPersonaContacto.setText(formatContacto());
        lblDescripcion.setText(empresa.getDescripcion() != null ? empresa.getDescripcion() : "Sin descripción");

        listaCiclos.getChildren().clear();
        for (Curso c : empresa.getCursos()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 0, 8, 0));
            Label siglas = new Label(c.getSiglas());
            siglas.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0d9488;");
            siglas.setMinWidth(55);
            Label nombre = new Label(c.getNombre());
            nombre.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-text-fill: #555;");
            row.getChildren().addAll(siglas, nombre);
            listaCiclos.getChildren().add(row);
        }

        actualizarEstiloFavorito();
        actualizarComentarios();
    }

    private String formatDireccion() {
        String dir = empresa.getDireccion() != null ? empresa.getDireccion() : "";
        String ciu = empresa.getCiudad() != null ? empresa.getCiudad() : "";
        if (!dir.isEmpty() && !ciu.isEmpty()) return dir + ", " + ciu;
        return !dir.isEmpty() ? dir : ciu;
    }

    private String formatContacto() {
        String pc = empresa.getPersonaContacto();
        String tc = empresa.getTelefonoContacto();
        if (pc == null || pc.isBlank()) return "-";
        return "Contacto: " + pc + (tc != null && !tc.isBlank() ? " (" + tc + ")" : "");
    }

    private VBox crearPlazaCard(Plaza p) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox headerTextos = new VBox(2);
        Label depto = new Label(p.getDepartamentoNombre());
        depto.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        String tipoStr = p.isEsGeneral()
                ? "General · " + p.getCantidad() + " plaza" + (p.getCantidad() != 1 ? "s" : "")
                : (p.getCursoSiglas() != null ? p.getCursoSiglas() : "Ciclo específico")
                        + " · " + p.getCantidad() + " plaza" + (p.getCantidad() != 1 ? "s" : "");
        Label tipo = new Label(tipoStr);
        tipo.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #0891b2;");
        headerTextos.getChildren().addAll(depto, tipo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(p.getPlazasDisponibles() > 0 ? p.getPlazasDisponibles() + " libres" : "Completo");
        badge.setStyle(p.getPlazasDisponibles() > 0
                ? "-fx-background-color: #d1fae5; -fx-text-fill: #065f46; -fx-background-radius: 12; -fx-padding: 3 10; -fx-font-size: 12px; -fx-font-weight: bold;"
                : "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-background-radius: 12; -fx-padding: 3 10; -fx-font-size: 12px; -fx-font-weight: bold;");

        header.getChildren().addAll(headerTextos, spacer, badge);

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(8, 0, 8, 0));
        stats.getChildren().addAll(
                crearStatItem(String.valueOf(p.getCantidad()), "Ofertadas", "#333"),
                crearStatItem(String.valueOf(p.getPlazasReservadas()), "Reservadas", "#333"),
                crearStatItem(String.valueOf(p.getPlazasDisponibles()), "Libres",
                        p.getPlazasDisponibles() > 0 ? "#10b981" : "#ef4444")
        );

        card.getChildren().addAll(header, stats);

        List<Reserva> reservasPlaza = reservas.stream()
                .filter(r -> r.getPlazaId() == p.getId())
                .collect(Collectors.toList());

        if (!reservasPlaza.isEmpty()) {
            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: #e2e8f0;");

            Label lblReservas = new Label("Reservas realizadas:");
            lblReservas.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #555;");

            VBox listaRvs = new VBox(6);
            for (Reserva r : reservasPlaza) {
                HBox filaR = new HBox(8);
                filaR.setAlignment(Pos.CENTER_LEFT);

                Label ico = new Label("👤");
                ico.setStyle("-fx-font-size: 14px;");

                String detalle = r.getCantidad() + " plaza" + (r.getCantidad() != 1 ? "s" : "");
                if (r.getCursoSiglas() != null) detalle += " · " + r.getCursoSiglas();
                if (r.getClase() != null && !r.getClase().isBlank()) detalle += " (" + r.getClase() + ")";

                VBox textos = new VBox(1);
                Label nombre = new Label(r.getProfesorNombre() != null ? r.getProfesorNombre() : "Profesor");
                nombre.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
                Label info = new Label(detalle);
                info.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #888;");
                textos.getChildren().addAll(nombre, info);

                filaR.getChildren().addAll(ico, textos);
                listaRvs.getChildren().add(filaR);
            }

            card.getChildren().addAll(sep, lblReservas, listaRvs);
        }

        if (Session.get().esProfesor() && p.getPlazasDisponibles() > 0) {
            Button btnReservar = new Button("⊕  Reservar plazas para mis alumnos");
            btnReservar.setStyle("-fx-background-color: transparent; -fx-text-fill: #0891b2; "
                    + "-fx-font-family: Arial; -fx-font-size: 13px; -fx-font-weight: bold; "
                    + "-fx-border-color: #0891b2; -fx-border-radius: 8; -fx-background-radius: 8; "
                    + "-fx-padding: 8 0; -fx-cursor: hand;");
            btnReservar.setMaxWidth(Double.MAX_VALUE);
            btnReservar.setOnAction(e -> abrirDialogoReserva(p));
            card.getChildren().add(btnReservar);
        }

        return card;
    }

    private VBox crearStatItem(String num, String label, String color) {
        VBox item = new VBox(2);
        item.setAlignment(Pos.CENTER);
        Label numLabel = new Label(num);
        numLabel.setStyle("-fx-font-family: Arial; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #888;");
        item.getChildren().addAll(numLabel, textLabel);
        return item;
    }

    @FXML
    private void mostrarComentariosGenerales() {
        tabComentarios = "general";
        btnTabGeneral.setStyle("-fx-background-color: #0d9488; -fx-text-fill: white; -fx-font-family: Arial; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnTabProfesores.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-font-family: Arial; -fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand;");
        actualizarComentarios();
    }

    @FXML
    private void mostrarComentariosProfesores() {
        tabComentarios = "profesores";
        btnTabProfesores.setStyle("-fx-background-color: #0d9488; -fx-text-fill: white; -fx-font-family: Arial; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnTabGeneral.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-font-family: Arial; -fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand;");
        actualizarComentarios();
    }

    private void actualizarComentarios() {
        List<Comentario> generales = comentarios.stream().filter(c -> !c.isEsPrivado()).toList();
        List<Comentario> privados = comentarios.stream().filter(Comentario::isEsPrivado).toList();

        btnTabGeneral.setText("\uD83C\uDF10 General (" + generales.size() + ")");
        btnTabProfesores.setText("\uD83C\uDF93 Profesores (" + privados.size() + ")");

        boolean prof = Session.get().esProfesor();
        btnTabProfesores.setVisible(prof);
        btnTabProfesores.setManaged(prof);

        List<Comentario> mostrar = tabComentarios.equals("general") ? generales : privados;
        listaComentarios.getChildren().clear();

        if (mostrar.isEmpty()) {
            Label empty = new Label("No hay comentarios aún");
            empty.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-text-fill: #999;");
            listaComentarios.getChildren().add(empty);
        } else {
            for (Comentario c : mostrar) {
                listaComentarios.getChildren().add(crearComentarioCard(c));
            }
        }
    }

    private VBox crearComentarioCard(Comentario c) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10;");

        HBox header = new HBox(10);
        Label autor = new Label(c.getUsuarioNombre() != null ? c.getUsuarioNombre() : "Anónimo");
        autor.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label fecha = new Label(c.getFecha() != null ? c.getFecha() : "");
        fecha.setStyle("-fx-font-family: Arial; -fx-font-size: 11px; -fx-text-fill: #999;");
        header.getChildren().addAll(autor, sp, fecha);

        Label texto = new Label(c.getTexto());
        texto.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #555;");
        texto.setWrapText(true);

        card.getChildren().addAll(header, texto);
        return card;
    }

    @FXML
    private void escribirComentario() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Escribir comentario");
        dialog.setHeaderText("Comentario para " + empresa.getNombre());

        TextArea txtArea = new TextArea();
        txtArea.setPromptText("Escribe tu comentario...");
        txtArea.setPrefRowCount(4);
        txtArea.setWrapText(true);

        CheckBox chkPrivado = new CheckBox("Comentario privado (solo profesores)");
        chkPrivado.setVisible(Session.get().esProfesor());

        VBox content = new VBox(10, txtArea, chkPrivado);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType btnEnviar = new ButtonType("Enviar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEnviar, ButtonType.CANCEL);
        dialog.setResultConverter(bt -> bt == btnEnviar ? txtArea.getText() : null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(texto -> {
            if (texto.isBlank()) return;
            new Thread(() -> {
                try {
                    ComentarioService.crear(empresa.getId(), texto, chkPrivado.isSelected());
                    List<Comentario> coms = ComentarioService.getByEmpresa(empresa.getId());
                    Platform.runLater(() -> {
                        comentarios = coms != null ? coms : new ArrayList<>();
                        actualizarComentarios();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> mostrarAlerta("Error", "No se pudo enviar el comentario"));
                }
            }).start();
        });
    }

    private void actualizarEstiloFavorito() {
        iconFavorito.setText(esFavorito ? "❤️" : "\uD83E\uDD0D");
        lblFavoritoTexto.setText(esFavorito ? "Añadido a favoritos" : "Favorito");
        if (esFavorito) {
            boxFavorito.setStyle("-fx-background-color: #d1fae5; -fx-background-radius: 14; -fx-cursor: hand; -fx-border-color: #10b981; -fx-border-radius: 14; -fx-border-width: 2;");
            lblFavoritoTexto.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #065f46; -fx-font-weight: bold;");
        } else {
            boxFavorito.setStyle("-fx-background-color: #F8F8F8; -fx-background-radius: 14; -fx-cursor: hand;");
            lblFavoritoTexto.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #555555;");
        }
    }

    @FXML
    private void toggleFavorito() {
        if (empresa == null) {
            mostrarAlerta("Espera", "Los datos aún se están cargando, inténtalo de nuevo en un momento.");
            return;
        }
        long id = empresa.getId();
        boolean nuevoEstado = !esFavorito;
        new Thread(() -> {
            try {
                FavoritoService.toggle(id);
                Platform.runLater(() -> {
                    esFavorito = nuevoEstado;
                    actualizarEstiloFavorito();
                    mostrarAlerta("Favoritos", esFavorito ? "Añadido a favoritos" : "Eliminado de favoritos");
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlerta("Error", "No se pudo cambiar el favorito: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void abrirValorar() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(5, 1, 2, 3, 4, 5);
        dialog.setTitle("Valorar empresa");
        dialog.setHeaderText("¿Cuántas estrellas le das a " + empresa.getNombre() + "?");
        dialog.setContentText("Estrellas:");

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(puntuacion -> {
            new Thread(() -> {
                try {
                    ValoracionService.crear(empresa.getId(), puntuacion);
                    Empresa emp = EmpresaService.getById(empresa.getId());
                    Platform.runLater(() -> {
                        empresa = emp;
                        rellenarDatos();
                        mostrarAlerta("¡Gracias!", "Has valorado con " + puntuacion + " estrellas");
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> mostrarAlerta("Error", "No se pudo enviar la valoración"));
                }
            }).start();
        });
    }

    @FXML
    private void editarEmpresa() {
        ViewManager.navigateTo("anadir_empresa", "empresaId", empresa.getId());
    }

    @FXML
    private void marcarContactado() {
        new Thread(() -> {
            try {
                List<Departamento> deps = DepartamentoService.getAll();
                Platform.runLater(() -> {
                    ChoiceDialog<Departamento> dialog = new ChoiceDialog<>(
                            deps.isEmpty() ? null : deps.get(0), deps);
                    dialog.setTitle("Marcar contactado");
                    dialog.setHeaderText("¿Por qué departamento contactasteis la empresa?");
                    dialog.setContentText("Departamento:");

                    Optional<Departamento> result = dialog.showAndWait();
                    result.ifPresent(depto -> {
                        new Thread(() -> {
                            try {
                                EmpresaContactadaService.crear(empresa.getId(), depto.getId());
                                Platform.runLater(() -> {
                                    mostrarAlerta("Contactado", "Empresa marcada como contactada por " + depto.getNombre());
                                    cargarDatos(empresa.getId());
                                });
                            } catch (Exception e2) {
                                Platform.runLater(() -> mostrarAlerta("Error", e2.getMessage()));
                            }
                        }).start();
                    });
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlerta("Error", e.getMessage()));
            }
        }).start();
    }

    private void abrirDialogoReserva(Plaza plaza) {
        new Thread(() -> {
            try {
                List<Curso> cursos;
                if (plaza.isEsGeneral()) {
                    cursos = DepartamentoService.getCursos(plaza.getDepartamentoId());
                } else {
                    Curso c = new Curso();
                    c.setId(plaza.getCursoId());
                    c.setSiglas(plaza.getCursoSiglas() != null ? plaza.getCursoSiglas() : "");
                    c.setNombre(plaza.getCursoNombre() != null ? plaza.getCursoNombre() : plaza.getCursoSiglas());
                    cursos = List.of(c);
                }

                final List<Curso> fCursos = (cursos != null) ? cursos : new ArrayList<>();
                Platform.runLater(() -> {
                    if (fCursos.isEmpty()) {
                        mostrarAlerta("Sin ciclos", "No hay ciclos disponibles para este departamento");
                        return;
                    }

                    ChoiceDialog<Curso> dialogCurso = new ChoiceDialog<>(fCursos.get(0), fCursos);
                    dialogCurso.setTitle("Reservar plazas");
                    dialogCurso.setHeaderText("Selecciona el ciclo formativo para " + plaza.getDepartamentoNombre());
                    dialogCurso.setContentText("Ciclo:");

                    Optional<Curso> resCurso = dialogCurso.showAndWait();
                    resCurso.ifPresent(curso -> {
                        TextInputDialog cantDialog = new TextInputDialog("1");
                        cantDialog.setTitle("Cantidad");
                        cantDialog.setHeaderText("¿Cuántas plazas reservar para " + curso.getSiglas() + "?");
                        cantDialog.setContentText("Cantidad:");
                        Optional<String> resCant = cantDialog.showAndWait();

                        resCant.ifPresent(cantStr -> {
                            TextInputDialog claseDialog = new TextInputDialog("");
                            claseDialog.setTitle("Clase (opcional)");
                            claseDialog.setHeaderText("Indica la clase o grupo (deja vacío si no aplica)");
                            claseDialog.setContentText("Clase:");
                            Optional<String> resClase = claseDialog.showAndWait();

                            int cantidad;
                            try { cantidad = Math.max(1, Integer.parseInt(cantStr.trim())); }
                            catch (NumberFormatException ex) { cantidad = 1; }
                            final int fCant = cantidad;
                            final String fClase = resClase.orElse("").trim();

                            new Thread(() -> {
                                try {
                                    ReservaService.crear(plaza.getId(), curso.getId(), fCant, fClase.isEmpty() ? null : fClase);
                                    Platform.runLater(() -> {
                                        mostrarAlerta("Reservado", fCant + " plaza(s) reservada(s) para " + curso.getSiglas());
                                        cargarDatos(empresa.getId());
                                    });
                                } catch (Exception e) {
                                    Platform.runLater(() -> mostrarAlerta("Error", e.getMessage()));
                                }
                            }).start();
                        });
                    });
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlerta("Error", e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void volver() {
        ViewManager.navigateTo("empresas");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML private void irAInicio() { ViewManager.navigateTo("empresas"); }
    @FXML private void irABusqueda() { ViewManager.navigateTo("busqueda"); }
    @FXML private void irAListas() { ViewManager.navigateTo("listas"); }
    @FXML private void irAPerfil() { ViewManager.navigateTo("perfil"); }

    @FXML
    private void abrirDialogoAnadirALista() {
        if (empresa == null) {
            mostrarAlerta("Espera", "Los datos aún se están cargando, inténtalo de nuevo.");
            return;
        }
        new Thread(() -> {
            try {
                List<Lista> listas = ListaService.getMisListas().stream()
                        .filter(l -> !l.isEsFavoritos())
                        .collect(Collectors.toList());

                List<Lista> listasCompletas = new ArrayList<>();
                for (Lista l : listas) {
                    listasCompletas.add(ListaService.getById(l.getId()));
                }
                Platform.runLater(() -> abrirVentanaDialogo(listasCompletas));
            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlerta("Error", "No se pudieron cargar las listas: " + e.getMessage()));
            }
        }).start();
    }

    private void abrirVentanaDialogo(List<Lista> listas) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/anadir_a_lista_dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            AnadirAListaDialogController dialogCtrl = loader.getController();
            dialogCtrl.init(empresa, listas);

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.initOwner(ViewManager.getStage());
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.setTitle("Añadir a lista");
            dialogStage.setResizable(false);
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.showAndWait();

            List<Lista> seleccionadas = dialogCtrl.getListasSeleccionadas();
            if (seleccionadas == null) return;
            guardarCambiosEnListas(dialogCtrl.getListasOriginales(), seleccionadas);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el diálogo: " + e.getMessage());
        }
    }

    private void guardarCambiosEnListas(List<Lista> listasOriginales, List<Lista> seleccionadas) {
        new Thread(() -> {
            try {
                for (Lista lista : listasOriginales) {
                    boolean debeEstar = seleccionadas.stream().anyMatch(s -> s.getId() == lista.getId());
                    boolean estabaAntes = lista.getEmpresas().stream().anyMatch(e -> e.getId() == empresa.getId());
                    if (debeEstar && !estabaAntes) {
                        ListaService.addEmpresa(lista.getId(), empresa.getId());
                    } else if (!debeEstar && estabaAntes) {
                        ListaService.removeEmpresa(lista.getId(), empresa.getId());
                    }
                }
                Platform.runLater(() -> mostrarAlerta("Listas", "Cambios guardados correctamente."));
            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlerta("Error", "No se pudo actualizar las listas: " + e.getMessage()));
            }
        }).start();
    }
}