package com.practicas.controller;

import com.practicas.model.Empresa;
import com.practicas.model.Lista;
import com.practicas.service.FavoritoService;
import com.practicas.service.ListaService;
import com.practicas.util.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.Cursor;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListasController {

    @FXML private Button btnCrearLista;
    @FXML private VBox listaListas;
    @FXML private VBox estadoVacio;

    private List<Lista> listas = new ArrayList<>();
    private List<Empresa> favoritos = new ArrayList<>();

    @FXML
    public void initialize() {
        cargarDatos();
    }

    private void cargarDatos() {
        new Thread(() -> {
            try {
                List<Lista> ls = ListaService.getMisListas();
                List<Empresa> favs = new ArrayList<>();
                try { favs = FavoritoService.getMisFavoritos(); } catch (Exception ignored) {}

                List<Lista> listasCompletas = new ArrayList<>();
                if (ls != null) {
                    for (Lista l : ls) {
                        try {
                            listasCompletas.add(ListaService.getById(l.getId()));
                        } catch (Exception ignored) {
                            listasCompletas.add(l);
                        }
                    }
                }

                final List<Lista> fListas = listasCompletas;
                final List<Empresa> fFavs = favs;

                Platform.runLater(() -> {
                    listas = fListas;
                    favoritos = fFavs;
                    renderizarListas();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    estadoVacio.setVisible(true);
                    estadoVacio.setManaged(true);
                });
            }
        }).start();
    }

    private void renderizarListas() {
        listaListas.getChildren().clear();

        boolean hayContenido = !listas.isEmpty() || !favoritos.isEmpty();
        estadoVacio.setVisible(!hayContenido);
        estadoVacio.setManaged(!hayContenido);

        // Favoritos siempre primero
        listaListas.getChildren().add(crearListaCard("♥", "Favoritos",
                favoritos.size() + " empresas", true, -1));

        // Listas del usuario
        for (Lista lista : listas) {
            if (lista.isEsFavoritos()) continue;
            listaListas.getChildren().add(crearListaCard("▤", lista.getNombre(),
                    lista.getEmpresas().size() + " empresas", false, lista.getId()));
        }
    }

    private HBox crearListaCard(String icono, String nombre, String count, boolean isFav, long listaId) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 14;");
        card.setCursor(Cursor.HAND);

        // Icono
        StackPane iconPane = new StackPane();
        iconPane.setMinSize(52, 52);
        iconPane.setMaxSize(52, 52);
        iconPane.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 14;");
        Label iconLabel = new Label(icono);
        iconLabel.setStyle("-fx-font-size: 24px;");
        iconPane.getChildren().add(iconLabel);

        // Texto
        VBox textBox = new VBox(3);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        Label lblNombre = new Label(nombre);
        lblNombre.setStyle("-fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label lblCount = new Label(count);
        lblCount.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.7);");
        textBox.getChildren().addAll(lblNombre, lblCount);

        // Eliminar (solo listas no favoritas)
        if (!isFav) {
            Label btnDel = new Label("X");
            btnDel.setStyle("-fx-font-size: 16px; -fx-cursor: hand; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
            btnDel.setCursor(Cursor.HAND);
            btnDel.setOnMouseClicked(e -> {
                e.consume();
                eliminarLista(listaId, nombre);
            });
            card.getChildren().addAll(iconPane, textBox, btnDel);
        } else {
            card.getChildren().addAll(iconPane, textBox);
        }

        // Flecha
        Label flecha = new Label("›");
        flecha.setStyle("-fx-font-size: 22px; -fx-text-fill: rgba(255,255,255,0.5);");
        card.getChildren().add(flecha);

        // Click
        card.setOnMouseClicked(e -> {
            ViewManager.navigateTo("lista_detalle", "listaId", listaId);
        });

        return card;
    }

    private void eliminarLista(long id, String nombre) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar lista");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Eliminar \"" + nombre + "\"?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    ListaService.eliminar(id);
                    Platform.runLater(this::cargarDatos);
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo eliminar");
                        alert.showAndWait();
                    });
                }
            }).start();
        }
    }

    @FXML
    private void mostrarDialogoCrearLista() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva lista");
        dialog.setHeaderText(null);
        dialog.setContentText("Nombre de la lista:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombre -> {
            if (nombre.isBlank()) return;
            new Thread(() -> {
                try {
                    ListaService.crear(nombre.trim());
                    Platform.runLater(this::cargarDatos);
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo crear la lista");
                        alert.showAndWait();
                    });
                }
            }).start();
        });
    }

    // Navegación
    @FXML private void volverAEmpresas() { ViewManager.navigateTo("empresas"); }
    @FXML private void abrirBusqueda() { ViewManager.navigateTo("busqueda"); }
    @FXML private void abrirPerfil() { ViewManager.navigateTo("perfil"); }
}
