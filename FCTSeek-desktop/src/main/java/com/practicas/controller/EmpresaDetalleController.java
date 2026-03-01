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

    private Empresa empresa;
    private long empresaIdCargada;
    private List<Comentario> comentarios = new ArrayList<>();
    private List<Plaza> plazas = new ArrayList<>();
    private boolean esFavorito = false;
    private String tabComentarios = "general";

    @FXML
    public void initialize() {
        Long empresaId = ViewManager.getParam("empresaId");
        System.out.println("[DEBUG] EmpresaDetalle.initialize() - empresaId param: " + empresaId);
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
                try { coms = ComentarioService.getByEmpresa(empresaId); } catch (Exception ignored) {}
                try { plz = PlazaService.getByEmpresa(empresaId); } catch (Exception ignored) {}

                boolean fav = false;
                try { fav = FavoritoService.isFavorito(empresaId); } catch (Exception ignored) {}

                final Empresa fEmp = emp;
                final List<Comentario> fComs = coms;
                final List<Plaza> fPlz = plz;
                final boolean fFav = fav;

                Platform.runLater(() -> {
                    empresa = fEmp;
                    comentarios = fComs != null ? fComs : new ArrayList<>();
                    plazas = fPlz != null ? fPlz : new ArrayList<>();
                    esFavorito = fFav;
                    rellenarDatos();
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblNombreEmpresa.setText("Error cargando empresa"));
            }
        }).start();
    }

    private void rellenarDatos() {
        if (empresa == null) return;

        lblNombreEmpresa.setText(empresa.getNombre());

        // Valoración
        lblEstrellas.setText(empresa.getEstrellasTexto());
        lblValoracion.setText(empresa.getValoracionMedia() != null
                ? String.format("%.1f", empresa.getValoracionMedia()) : "-");
        lblNumValoraciones.setText("(" + (empresa.getTotalValoraciones() != null
                ? empresa.getTotalValoraciones() : 0) + " valoraciones)");

        // Chips departamentos
        contenedorChipsDpto.getChildren().clear();
        for (Departamento d : empresa.getDepartamentos()) {
            Label chip = new Label(d.getCodigo() != null ? d.getCodigo() : d.getNombre());
            chip.setStyle("-fx-background-color: rgba(255,255,255,0.25); -fx-background-radius: 10; "
                    + "-fx-padding: 5 12; -fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: white;");
            contenedorChipsDpto.getChildren().add(chip);
        }

        // Plazas
        if (plazas.isEmpty()) {
            tarjetaPlazas.setVisible(false);
            tarjetaPlazas.setManaged(false);
        } else {
            tarjetaPlazas.setVisible(true);
            tarjetaPlazas.setManaged(true);
            contenedorPlazas.getChildren().clear();
            for (Plaza p : plazas) {
                contenedorPlazas.getChildren().add(crearPlazaCard(p));
            }
        }

        // Contacto
        lblDireccion.setText(formatDireccion());
        lblTelefono.setText(empresa.getTelefono() != null ? empresa.getTelefono() : "-");
        lblEmail.setText(empresa.getEmail() != null ? empresa.getEmail() : "-");
        lblPersonaContacto.setText(formatContacto());
        lblDescripcion.setText(empresa.getDescripcion() != null ? empresa.getDescripcion() : "Sin descripción");

        // Ciclos
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

        // Favorito
        actualizarEstiloFavorito();

        // Comentarios
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

    // ─── Plazas card ────────────────────────────────────────────────────

    private VBox crearPlazaCard(Plaza p) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label depto = new Label(p.getDepartamentoNombre());
        depto.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = new Label(p.getLibres() > 0 ? p.getLibres() + " libres" : "Completo");
        badge.setStyle(p.getLibres() > 0
                ? "-fx-background-color: #d1fae5; -fx-text-fill: #065f46; -fx-background-radius: 12; -fx-padding: 3 10; -fx-font-size: 12px; -fx-font-weight: bold;"
                : "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-background-radius: 12; -fx-padding: 3 10; -fx-font-size: 12px; -fx-font-weight: bold;");
        header.getChildren().addAll(depto, spacer, badge);

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);
        stats.getChildren().addAll(
                crearStatItem(String.valueOf(p.getPlazasOfertadas()), "Ofertadas", "#333"),
                crearStatItem(String.valueOf(p.getReservadas()), "Reservadas", "#333"),
                crearStatItem(String.valueOf(p.getLibres()), "Libres", p.getLibres() > 0 ? "#10b981" : "#ef4444")
        );

        card.getChildren().addAll(header, stats);

        if (Session.get().esProfesor() && p.getLibres() > 0) {
            Button btnReservar = new Button("+ Reservar plazas");
            btnReservar.setStyle("-fx-background-color: transparent; -fx-text-fill: #0891b2; -fx-font-family: Arial; -fx-font-size: 13px; -fx-cursor: hand;");
            btnReservar.setOnAction(e -> abrirDialogoReserva(p.getDepartamentoId()));
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

    // ─── Comentarios ────────────────────────────────────────────────────

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

        // Solo mostrar tab profesores si es profesor
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

    // ─── Acciones ───────────────────────────────────────────────────────

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
        System.out.println("[DEBUG] toggleFavorito() llamado - empresa: " + (empresa != null ? empresa.getNombre() : "NULL"));
        if (empresa == null) {
            mostrarAlerta("Espera", "Los datos aún se están cargando, inténtalo de nuevo en un momento.");
            return;
        }
        long id = empresa.getId();
        new Thread(() -> {
            try {
                System.out.println("[DEBUG] Llamando FavoritoService.toggle(" + id + ")");
                boolean nuevoEstado = FavoritoService.toggle(id);
                System.out.println("[DEBUG] Toggle resultado: " + nuevoEstado);
                Platform.runLater(() -> {
                    esFavorito = nuevoEstado;
                    actualizarEstiloFavorito();
                    mostrarAlerta("Favoritos", esFavorito ? "Añadido a favoritos" : "Eliminado de favoritos");
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    mostrarAlerta("Error", "No se pudo cambiar el favorito: " + e.getMessage());
                });
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
                    dialog.setHeaderText("¿Por qué departamento?");
                    dialog.setContentText("Departamento:");

                    Optional<Departamento> result = dialog.showAndWait();
                    result.ifPresent(depto -> {
                        new Thread(() -> {
                            try {
                                PlazaService.crear(empresa.getId(), depto.getId(), 1, true);
                                Platform.runLater(() -> {
                                    mostrarAlerta("Contactado", "Empresa marcada como contactada por " + depto.getCodigo());
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

    private void abrirDialogoReserva(long departamentoId) {
        new Thread(() -> {
            try {
                List<Curso> cursos = DepartamentoService.getCursos(departamentoId);
                Platform.runLater(() -> {
                    if (cursos == null || cursos.isEmpty()) {
                        mostrarAlerta("Sin ciclos", "No hay ciclos disponibles para este departamento");
                        return;
                    }
                    ChoiceDialog<Curso> dialog = new ChoiceDialog<>(cursos.get(0), cursos);
                    dialog.setTitle("Reservar plazas");
                    dialog.setHeaderText("Selecciona el ciclo formativo");
                    dialog.setContentText("Ciclo:");

                    Optional<Curso> result = dialog.showAndWait();
                    result.ifPresent(curso -> {
                        TextInputDialog cantDialog = new TextInputDialog("1");
                        cantDialog.setTitle("Cantidad");
                        cantDialog.setHeaderText("¿Cuántas plazas reservar para " + curso.getSiglas() + "?");
                        cantDialog.setContentText("Cantidad:");
                        Optional<String> cantResult = cantDialog.showAndWait();
                        cantResult.ifPresent(cant -> {
                            int cantidad;
                            try { cantidad = Integer.parseInt(cant); } catch (NumberFormatException e) { cantidad = 1; }
                            final int fCant = cantidad;
                            new Thread(() -> {
                                try {
                                    ReservaService.crear(empresa.getId(), departamentoId, fCant, curso.getId(), curso.getSiglas(), null);
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

    // Navegación
    @FXML private void irAInicio() { ViewManager.navigateTo("empresas"); }
    @FXML private void irABusqueda() { ViewManager.navigateTo("busqueda"); }
    @FXML private void irAListas() { ViewManager.navigateTo("listas"); }
    @FXML private void irAPerfil() { ViewManager.navigateTo("perfil"); }
}