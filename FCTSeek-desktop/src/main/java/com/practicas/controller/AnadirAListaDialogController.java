package com.practicas.controller;

import com.practicas.model.Empresa;
import com.practicas.model.Lista;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AnadirAListaDialogController {

    @FXML private Label lblSubtitulo;
    @FXML private VBox contenedorListas;
    @FXML private Label lblSinListas;

    private Empresa empresa;
    private List<Lista> listas = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private boolean guardado = false;

    public void init(Empresa empresa, List<Lista> listasDisponibles) {
        this.empresa = empresa;
        this.listas = listasDisponibles;

        lblSubtitulo.setText("Añadir \"" + empresa.getNombre() + "\" a una lista");

        if (listas.isEmpty()) {
            lblSinListas.setVisible(true);
            lblSinListas.setManaged(true);
            contenedorListas.setVisible(false);
            contenedorListas.setManaged(false);
            return;
        }

        for (Lista lista : listas) {
            boolean yaEsta = lista.getEmpresas().stream()
                    .anyMatch(e -> e.getId() == empresa.getId());

            // CheckBox sin texto para evitar que herede el color blanco global
            CheckBox cb = new CheckBox();
            cb.setSelected(yaEsta);

            // Label independiente con color oscuro forzado
            Label lblNombre = new Label(lista.getNombre());
            lblNombre.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-text-fill: #222222;");
            HBox.setHgrow(lblNombre, Priority.ALWAYS);

            HBox fila = new HBox(12);
            fila.setPadding(new Insets(11, 14, 11, 14));
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 10; -fx-cursor: hand;");
            fila.getChildren().addAll(cb, lblNombre);

            // Clic en toda la fila activa el checkbox
            fila.setOnMouseClicked(e -> cb.setSelected(!cb.isSelected()));
            cb.setOnMouseClicked(javafx.event.Event::consume);

            checkBoxes.add(cb);
            contenedorListas.getChildren().add(fila);
        }
    }

    @FXML
    private void guardar() {
        guardado = true;
        cerrar();
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) contenedorListas.getScene().getWindow();
        stage.close();
    }

    public List<Lista> getListasSeleccionadas() {
        if (!guardado) return null;
        List<Lista> seleccionadas = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                seleccionadas.add(listas.get(i));
            }
        }
        return seleccionadas;
    }

    public List<Lista> getListasOriginales() {
        return listas;
    }

    public Empresa getEmpresa() {
        return empresa;
    }
}
