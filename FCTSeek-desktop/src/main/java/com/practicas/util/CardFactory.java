package com.practicas.util;

import com.practicas.model.Empresa;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

/**
 * Fábrica de tarjetas de empresa reutilizables en las listas.
 */
public class CardFactory {

    /**
     * Crea una tarjeta de empresa estilizada.
     */
    public static HBox crearEmpresaCard(Empresa empresa, Consumer<Empresa> onClick) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(18));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        card.setCursor(Cursor.HAND);
        card.setPrefHeight(Region.USE_COMPUTED_SIZE);
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);

        // Icono inicial
        StackPane iconPane = new StackPane();
        iconPane.setMinSize(52, 52);
        iconPane.setMaxSize(52, 52);
        iconPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #4facfe, #00f2fe); "
                + "-fx-background-radius: 14;");
        Label iconLabel = new Label(empresa.getNombre() != null && !empresa.getNombre().isEmpty()
                ? empresa.getNombre().substring(0, 1).toUpperCase() : "?");
        iconLabel.setStyle("-fx-font-family: Arial; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        iconPane.getChildren().add(iconLabel);

        // Contenido
        VBox content = new VBox(4);
        content.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(content, Priority.ALWAYS);

        Label nombre = new Label(empresa.getNombre());
        nombre.setStyle("-fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        nombre.setWrapText(true);

        // Departamentos
        Label dptos = new Label(empresa.getDepartamentosTexto());
        dptos.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #888;");

        // Ciudad
        String ciudadTexto = empresa.getCiudad() != null ? "📍 " + empresa.getCiudad() : "";
        Label ciudad = new Label(ciudadTexto);
        ciudad.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #999;");

        content.getChildren().addAll(nombre, dptos, ciudad);

        // Valoración
        VBox valoracionBox = new VBox(3);
        valoracionBox.setAlignment(Pos.CENTER_RIGHT);
        valoracionBox.setMinWidth(80);

        Label estrellas = new Label(empresa.getEstrellasTexto());
        estrellas.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFD700;");

        String valText = empresa.getValoracionMedia() != null
                ? String.format("%.1f", empresa.getValoracionMedia()) : "-";
        Label valLabel = new Label(valText);
        valLabel.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label numVal = new Label(empresa.getTotalValoraciones() != null
                ? "(" + empresa.getTotalValoraciones() + ")" : "");
        numVal.setStyle("-fx-font-family: Arial; -fx-font-size: 11px; -fx-text-fill: #999;");

        valoracionBox.getChildren().addAll(estrellas, valLabel, numVal);

        // Flecha
        Label flecha = new Label("›");
        flecha.setStyle("-fx-font-size: 24px; -fx-text-fill: #ccc;");

        card.getChildren().addAll(iconPane, content, valoracionBox, flecha);

        // Click
        if (onClick != null) {
            card.setOnMouseClicked(e -> onClick.accept(empresa));
        }

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle().replace(
                "rgba(0,0,0,0.08)", "rgba(0,0,0,0.15)")));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace(
                "rgba(0,0,0,0.15)", "rgba(0,0,0,0.08)")));

        return card;
    }

    /**
     * Crea una tarjeta de empresa con botón de eliminar (para listas).
     */
    public static HBox crearEmpresaCardConEliminar(Empresa empresa,
                                                     Consumer<Empresa> onClick,
                                                     Consumer<Empresa> onDelete) {
        HBox card = crearEmpresaCard(empresa, onClick);

        Label btnDelete = new Label("✕");
        btnDelete.setStyle("-fx-font-size: 16px; -fx-text-fill: #FF5252; -fx-cursor: hand;");
        btnDelete.setCursor(Cursor.HAND);
        btnDelete.setOnMouseClicked(e -> {
            e.consume();
            if (onDelete != null) onDelete.accept(empresa);
        });

        card.getChildren().add(btnDelete);
        return card;
    }
}
