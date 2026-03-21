package com.practicas.controller;

import com.practicas.model.Empresa;
import com.practicas.model.Lista;
import com.practicas.service.FavoritoService;
import com.practicas.service.ListaService;
import com.practicas.util.CardFactory;
import com.practicas.util.IconHelper;
import com.practicas.util.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListaDetalleController {

    @FXML private VBox contenedorIconoLista;
    @FXML private Label iconoLista;
    @FXML private Label lblNombreLista;
    @FXML private Label lblEmpresasCount;
    @FXML private VBox listaEmpresas;
    @FXML private VBox estadoVacio;
    @FXML private Button btnEliminarLista;
    @FXML private Label navIconHome;
    @FXML private Label navIconBuscar;
    @FXML private Label navIconListas;
    @FXML private Label navIconPerfil;

    private long listaId;
    private boolean isFavoritos;
    private List<Empresa> empresas = new ArrayList<>();

    @FXML
    public void initialize() {
        // Inicializar iconos de navegación
        navIconHome.setGraphic(IconHelper.get("ic_home.png", 22));
        navIconBuscar.setGraphic(IconHelper.get("ic_search.png", 22));
        navIconListas.setGraphic(IconHelper.get("ic_list.png", 22));
        navIconPerfil.setGraphic(IconHelper.get("ic_profile.png", 22));

        Long id = ViewManager.getParam("listaId");
        ViewManager.clearParam("listaId");
        listaId = id != null ? id : -1;
        isFavoritos = listaId == -1;

        if (isFavoritos) {
            iconoLista.setText("");
            iconoLista.setGraphic(IconHelper.get("ic_heart_full.png", 34));
            lblNombreLista.setText("Favoritos");
            btnEliminarLista.setVisible(false);
            btnEliminarLista.setManaged(false);
        } else {
            iconoLista.setText("");
            iconoLista.setGraphic(IconHelper.get("ic_list.png", 34));
        }

        cargarDatos();
    }

    private void cargarDatos() {
        new Thread(() -> {
            try {
                List<Empresa> emps;
                String nombre;

                if (isFavoritos) {
                    emps = FavoritoService.getMisFavoritos();
                    nombre = "Favoritos";
                } else {
                    Lista lista = ListaService.getById(listaId);
                    emps = lista.getEmpresas();
                    nombre = lista.getNombre();
                }

                final List<Empresa> fEmps = emps != null ? emps : new ArrayList<>();
                final String fNombre = nombre;

                Platform.runLater(() -> {
                    empresas = fEmps;
                    lblNombreLista.setText(fNombre);
                    lblEmpresasCount.setText(empresas.size() + " empresas");
                    renderizarEmpresas();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblEmpresasCount.setText("Error cargando datos");
                    estadoVacio.setVisible(true);
                    estadoVacio.setManaged(true);
                });
            }
        }).start();
    }

    private void renderizarEmpresas() {
        listaEmpresas.getChildren().clear();
        boolean vacia = empresas.isEmpty();
        estadoVacio.setVisible(vacia);
        estadoVacio.setManaged(vacia);

        for (Empresa e : empresas) {
            listaEmpresas.getChildren().add(
                    CardFactory.crearEmpresaCardConEliminar(e, this::abrirDetalle, this::confirmarEliminar));
        }
    }

    private void abrirDetalle(Empresa empresa) {
        ViewManager.navigateTo("empresa_detalle", "empresaId", empresa.getId());
    }

    private void confirmarEliminar(Empresa empresa) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Quitar \"" + empresa.getNombre() + "\" de la lista?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    if (isFavoritos) {
                        FavoritoService.toggle(empresa.getId());
                    } else {
                        ListaService.removeEmpresa(listaId, empresa.getId());
                    }
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
    private void eliminarLista() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar lista");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Estás seguro de eliminar esta lista?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    ListaService.eliminar(listaId);
                    Platform.runLater(() -> ViewManager.navigateTo("listas"));
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo eliminar la lista");
                        alert.showAndWait();
                    });
                }
            }).start();
        }
    }

    @FXML
    private void volverAListas() {
        ViewManager.navigateTo("listas");
    }

    // Navegación
    @FXML private void volverAEmpresas() { ViewManager.navigateTo("empresas"); }
    @FXML private void abrirBusqueda() { ViewManager.navigateTo("busqueda"); }
    @FXML private void abrirPerfil() { ViewManager.navigateTo("perfil"); }
}
