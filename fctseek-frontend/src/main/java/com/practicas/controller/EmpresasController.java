package com.practicas.controller;

import com.practicas.model.DataService;
import com.practicas.model.Empresa;
import com.practicas.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EmpresasController {

    @FXML private Label lblSaludo;
    @FXML private Label lblInfo;
    @FXML private Label lblBadge;
    @FXML private Label lblTituloSeccion;
    @FXML private Button btnAdd;
    @FXML private VBox listaEmpresas;
    @FXML private VBox filtroNuevas;
    @FXML private VBox filtroTop;
    @FXML private VBox filtroCercanas;

    private Usuario usuario;
    private DataService dataService;
    private String filtroActual = "todas";
    
    // Mapa de valoraciones por empresa (simulado)
    private Map<String, Double> valoraciones = new HashMap<>();
    // Mapa de fechas de creación (simulado - más reciente = índice menor)
    private Map<String, Integer> ordenCreacion = new HashMap<>();
    // Mapa de distancias (simulado)
    private Map<String, Double> distancias = new HashMap<>();

    @FXML
    public void initialize() {
        dataService = DataService.getInstance();
        inicializarDatosSimulados();
    }

    private void inicializarDatosSimulados() {
        // Simular valoraciones aleatorias para cada empresa
        Random rand = new Random(42); // Seed fija para consistencia
        List<Empresa> empresas = dataService.getEmpresas();
        
        for (int i = 0; i < empresas.size(); i++) {
            String nombre = empresas.get(i).getNombre();
            valoraciones.put(nombre, 3.5 + rand.nextDouble() * 1.5);
            ordenCreacion.put(nombre, i);
            distancias.put(nombre, 0.5 + rand.nextDouble() * 20); // 0.5 a 20.5 km
        }
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        actualizarVista();
    }

    private void actualizarVista() {
        String nombre = usuario.getNombreCompleto().split(" ")[0];
        lblSaludo.setText("Hola, " + nombre);
        
        String rol = usuario.esProfesor() ? "Profesor" : "Alumno";
        int disponibles = (int) dataService.getEmpresas().stream().filter(e -> !e.estaOcupada()).count();
        lblInfo.setText(rol + " - " + disponibles + " empresas disponibles");
        
        btnAdd.setVisible(usuario.esProfesor());
        
        cargarEmpresas();
    }

    private void cargarEmpresas() {
        listaEmpresas.getChildren().clear();
        
        List<Empresa> empresas = dataService.getEmpresas();
        
        // Aplicar filtro según selección
        switch (filtroActual) {
            case "nuevas":
                empresas = empresas.stream()
                    .sorted(Comparator.comparingInt(e -> ordenCreacion.getOrDefault(e.getNombre(), 999)))
                    .collect(Collectors.toList());
                lblTituloSeccion.setText("Empresas nuevas");
                break;
            case "top":
                empresas = empresas.stream()
                    .sorted((e1, e2) -> Double.compare(
                        valoraciones.getOrDefault(e2.getNombre(), 0.0),
                        valoraciones.getOrDefault(e1.getNombre(), 0.0)))
                    .collect(Collectors.toList());
                lblTituloSeccion.setText("Top valoradas");
                break;
            case "cercanas":
                empresas = empresas.stream()
                    .sorted(Comparator.comparingDouble(e -> distancias.getOrDefault(e.getNombre(), 999.0)))
                    .collect(Collectors.toList());
                lblTituloSeccion.setText("Empresas cercanas");
                break;
            default:
                lblTituloSeccion.setText("Todas las empresas");
                break;
        }
        
        lblBadge.setText(String.valueOf(empresas.size()));
        
        for (Empresa empresa : empresas) {
            listaEmpresas.getChildren().add(crearTarjetaEmpresa(empresa));
        }
    }

    @FXML
    private void filtrarNuevas() {
        filtroActual = filtroActual.equals("nuevas") ? "todas" : "nuevas";
        actualizarEstilosFiltros();
        cargarEmpresas();
    }

    @FXML
    private void filtrarTopValoradas() {
        filtroActual = filtroActual.equals("top") ? "todas" : "top";
        actualizarEstilosFiltros();
        cargarEmpresas();
    }

    @FXML
    private void filtrarCercanas() {
        filtroActual = filtroActual.equals("cercanas") ? "todas" : "cercanas";
        actualizarEstilosFiltros();
        cargarEmpresas();
    }

    private void actualizarEstilosFiltros() {
        String estiloActivo = "-fx-background-color: #E0E0E0; -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 12 20;";
        String estiloInactivo = "-fx-background-color: rgba(255,255,255,0.25); -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 12 20;";
        
        filtroNuevas.setStyle(filtroActual.equals("nuevas") ? estiloActivo : estiloInactivo);
        filtroTop.setStyle(filtroActual.equals("top") ? estiloActivo : estiloInactivo);
        filtroCercanas.setStyle(filtroActual.equals("cercanas") ? estiloActivo : estiloInactivo);
    }

    private VBox crearTarjetaEmpresa(Empresa empresa) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);");

        // Fila 1: Nombre + Valoracion
        HBox fila1 = new HBox();
        fila1.setAlignment(Pos.CENTER_LEFT);

        VBox infoNombre = new VBox(2);
        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setStyle("-fx-font-family: Arial; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        
        Label lblUbicacion = new Label(empresa.getUbicacion());
        lblUbicacion.setStyle("-fx-font-family: Arial; -fx-font-size: 13px; -fx-text-fill: #555555;");
        
        // Mostrar distancia si estamos en filtro cercanas
        if (filtroActual.equals("cercanas")) {
            double dist = distancias.getOrDefault(empresa.getNombre(), 0.0);
            lblUbicacion.setText(empresa.getUbicacion() + " - " + String.format("%.1f", dist) + " km");
        }
        
        infoNombre.getChildren().addAll(lblNombre, lblUbicacion);

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        // Valoración
        HBox valoracion = new HBox(4);
        valoracion.setAlignment(Pos.CENTER);
        valoracion.setPadding(new Insets(5, 10, 5, 10));
        valoracion.setStyle("-fx-background-color: #FFD54F; -fx-background-radius: 15;");
        
        double valor = valoraciones.getOrDefault(empresa.getNombre(), 4.0);
        Label puntuacion = new Label(String.format("%.1f", valor));
        puntuacion.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        puntuacion.setTextFill(Color.web("#5D4037"));
        valoracion.getChildren().add(puntuacion);

        fila1.getChildren().addAll(infoNombre, spacer1, valoracion);

        // Badge
        String badgeText = empresa.estaOcupada() ? "HOT" : "INF";
        String badgeColor = empresa.estaOcupada() ? "#FF5722" : "#4CAF50";
        Label lblBadgeEmpresa = new Label(badgeText);
        lblBadgeEmpresa.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        lblBadgeEmpresa.setTextFill(Color.WHITE);
        lblBadgeEmpresa.setPadding(new Insets(3, 8, 3, 8));
        lblBadgeEmpresa.setStyle("-fx-background-color: " + badgeColor + "; -fx-background-radius: 4;");

        // Seccion contacto
        VBox seccionContacto = new VBox(6);
        seccionContacto.setPadding(new Insets(10));
        seccionContacto.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 8;");
        Label lblContactado = new Label("Contactado por:");
        lblContactado.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblContactado.setTextFill(Color.rgb(56, 142, 60));
        Label tagSector = new Label(empresa.getSector());
        tagSector.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        tagSector.setTextFill(Color.rgb(21, 101, 192));
        tagSector.setPadding(new Insets(6, 14, 6, 14));
        tagSector.setStyle("-fx-background-color: #E3F2FD; -fx-background-radius: 12;");
        seccionContacto.getChildren().addAll(lblContactado, tagSector);

        // Barra de plazas
        HBox barraPlazas = new HBox(8);
        barraPlazas.setAlignment(Pos.CENTER_LEFT);
        HBox indicadores = new HBox(4);
        for (int i = 0; i < empresa.getPlazasTotales(); i++) {
            Region ind = new Region();
            ind.setPrefSize(25, 6);
            String color = i < empresa.getPlazasOcupadas() ? "#4facfe" : "#E0E0E0";
            ind.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3;");
            indicadores.getChildren().add(ind);
        }
        Label lblPlazas = new Label(empresa.getEstadoPlazas());
        lblPlazas.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblPlazas.setTextFill(Color.rgb(100, 100, 100));
        barraPlazas.getChildren().addAll(indicadores, lblPlazas);

        tarjeta.getChildren().addAll(fila1, lblBadgeEmpresa, seccionContacto, barraPlazas);

        // Hover
        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #BDBDBD; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 15, 0, 0, 5); -fx-cursor: hand;"));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"));
        tarjeta.setOnMouseClicked(e -> mostrarDetallesEmpresa(empresa));

        return tarjeta;
    }

    private void mostrarDetallesEmpresa(Empresa empresa) {
        Stage ventana = new Stage();
        ventana.setTitle(empresa.getNombre());
        ventana.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: white;");

        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setStyle("-fx-font-family: Arial; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label lblEstado = new Label(empresa.estaOcupada() ? "OCUPADA" : "DISPONIBLE");
        lblEstado.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + (empresa.estaOcupada() ? "#FF5722" : "#4CAF50") + ";");

        double valor = valoraciones.getOrDefault(empresa.getNombre(), 4.0);
        Label lblValoracion = new Label("Valoracion: " + String.format("%.1f", valor) + " / 5.0");
        lblValoracion.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #666666;");
        
        double dist = distancias.getOrDefault(empresa.getNombre(), 0.0);
        Label lblDistancia = new Label("Distancia: " + String.format("%.1f", dist) + " km");
        lblDistancia.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #666666;");

        Label lblSector = new Label("Sector: " + empresa.getSector());
        lblSector.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #666666;");
        
        Label lblUbicacion = new Label("Ubicacion: " + empresa.getUbicacion());
        lblUbicacion.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #666666;");
        
        Label lblPlazas = new Label("Plazas: " + empresa.getEstadoPlazas());
        lblPlazas.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-text-fill: #666666; -fx-font-weight: bold;");

        Separator sep1 = new Separator();
        
        Label lblDescTitulo = new Label("Descripcion:");
        lblDescTitulo.setStyle("-fx-font-family: Arial; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        TextArea txtDesc = new TextArea(empresa.getDescripcion() != null ? empresa.getDescripcion() : "Sin descripcion");
        txtDesc.setWrapText(true);
        txtDesc.setEditable(false);
        txtDesc.setPrefRowCount(3);
        txtDesc.setPrefWidth(350);

        contenido.getChildren().addAll(lblNombre, lblEstado, lblValoracion, lblDistancia, lblSector, lblUbicacion, lblPlazas, sep1, lblDescTitulo, txtDesc);

        if (usuario.esProfesor()) {
            Separator sep2 = new Separator();
            
            HBox controles = new HBox(10);
            controles.setAlignment(Pos.CENTER);
            controles.setPadding(new Insets(10, 0, 0, 0));
            
            Label lblPlazasCtrl = new Label("Plazas:");
            lblPlazasCtrl.setStyle("-fx-font-family: Arial; -fx-font-size: 12px;");
            
            Spinner<Integer> spinner = new Spinner<>(0, empresa.getPlazasTotales(), empresa.getPlazasOcupadas());
            spinner.setPrefWidth(70);
            
            Button btnActualizar = new Button("Actualizar");
            btnActualizar.setStyle("-fx-background-color: #4facfe; -fx-text-fill: white; -fx-font-weight: bold;");
            btnActualizar.setOnAction(e -> {
                dataService.actualizarPlazas(empresa, spinner.getValue());
                cargarEmpresas();
                ventana.close();
            });
            
            Button btnEditar = new Button("Editar");
            btnEditar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
            btnEditar.setOnAction(e -> {
                ventana.close();
                mostrarDialogoEditar(empresa);
            });
            
            Button btnEliminar = new Button("Eliminar");
            btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
            btnEliminar.setOnAction(e -> {
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("Confirmar eliminacion");
                confirmacion.setHeaderText("Eliminar " + empresa.getNombre() + "?");
                confirmacion.setContentText("Esta accion no se puede deshacer.");
                confirmacion.showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.OK) {
                        dataService.eliminarEmpresa(empresa);
                        cargarEmpresas();
                        ventana.close();
                    }
                });
            });
            
            controles.getChildren().addAll(lblPlazasCtrl, spinner, btnActualizar, btnEditar, btnEliminar);
            contenido.getChildren().addAll(sep2, controles);
        }

        // Boton cerrar
        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnCerrar.setOnAction(e -> ventana.close());
        
        HBox boxCerrar = new HBox(btnCerrar);
        boxCerrar.setAlignment(Pos.CENTER_RIGHT);
        boxCerrar.setPadding(new Insets(15, 0, 0, 0));
        contenido.getChildren().add(boxCerrar);

        Scene scene = new Scene(contenido, 420, 450);
        ventana.setScene(scene);
        ventana.show();
    }

    private void mostrarDialogoEditar(Empresa empresa) {
        Stage ventanaEditar = new Stage();
        ventanaEditar.setTitle("Editar Empresa - " + empresa.getNombre());
        ventanaEditar.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        // Campos
        TextField txtNombre = new TextField(empresa.getNombre());
        TextField txtSector = new TextField(empresa.getSector());
        TextField txtUbicacion = new TextField(empresa.getUbicacion());
        TextField txtCiclos = new TextField("");
        
        String desc = empresa.getDescripcion();
        if (desc != null && desc.startsWith("Ciclos:")) {
            int idx = desc.indexOf("\n\n");
            if (idx > 0) {
                txtCiclos.setText(desc.substring(8, idx).trim());
                desc = desc.substring(idx + 2);
            }
        }
        
        TextArea txtDesc = new TextArea(desc);
        txtDesc.setPrefRowCount(3);
        txtDesc.setWrapText(true);
        
        TextArea txtResena = new TextArea(empresa.getResena() != null ? empresa.getResena() : "");
        txtResena.setPrefRowCount(2);
        txtResena.setWrapText(true);
        
        Spinner<Integer> spinTotal = new Spinner<>(1, 20, empresa.getPlazasTotales());
        Spinner<Integer> spinOcupadas = new Spinner<>(0, 20, empresa.getPlazasOcupadas());

        // Grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));

        int row = 0;
        grid.add(new Label("Nombre:"), 0, row);
        grid.add(txtNombre, 1, row++);
        grid.add(new Label("Sector:"), 0, row);
        grid.add(txtSector, 1, row++);
        grid.add(new Label("Ubicacion:"), 0, row);
        grid.add(txtUbicacion, 1, row++);
        grid.add(new Label("Ciclos:"), 0, row);
        grid.add(txtCiclos, 1, row++);
        grid.add(new Label("Descripcion:"), 0, row);
        grid.add(txtDesc, 1, row++);
        grid.add(new Label("Resena:"), 0, row);
        grid.add(txtResena, 1, row++);
        grid.add(new Label("Plazas totales:"), 0, row);
        grid.add(spinTotal, 1, row++);
        grid.add(new Label("Plazas ocupadas:"), 0, row);
        grid.add(spinOcupadas, 1, row++);

        // Botones
        Button btnGuardar = new Button("Guardar");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox hboxBotones = new HBox(10, btnGuardar, btnCancelar);
        hboxBotones.setAlignment(Pos.CENTER);
        
        grid.add(hboxBotones, 1, row);

        btnGuardar.setOnAction(e -> {
            empresa.setNombre(txtNombre.getText());
            empresa.setSector(txtSector.getText());
            empresa.setUbicacion(txtUbicacion.getText());
            
            String nuevaDesc = txtDesc.getText();
            if (!txtCiclos.getText().isEmpty()) {
                nuevaDesc = "Ciclos: " + txtCiclos.getText() + "\n\n" + nuevaDesc;
            }
            empresa.setDescripcion(nuevaDesc);
            empresa.setResena(txtResena.getText());
            empresa.setPlazasTotales(spinTotal.getValue());
            empresa.setPlazasOcupadas(spinOcupadas.getValue());
            
            cargarEmpresas();
            ventanaEditar.close();
        });

        btnCancelar.setOnAction(e -> ventanaEditar.close());

        Scene scene = new Scene(grid, 500, 450);
        ventanaEditar.setScene(scene);
        ventanaEditar.show();
    }

    @FXML
    private void mostrarDialogoAnadir() {
        Dialog<Empresa> dialog = new Dialog<>();
        dialog.setTitle("Anadir Empresa");

        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: white;");

        Label titulo = new Label("Nueva Empresa");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titulo.setTextFill(Color.web("#4facfe"));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la empresa");
        txtNombre.setPrefWidth(300);
        
        TextField txtSector = new TextField();
        txtSector.setPromptText("Ej: Tecnologia, Consultoria IT...");
        
        TextField txtUbicacion = new TextField();
        txtUbicacion.setPromptText("Ciudad o direccion");
        
        TextArea txtDesc = new TextArea();
        txtDesc.setPromptText("Descripcion de la empresa y actividades...");
        txtDesc.setPrefRowCount(2);
        
        TextArea txtResena = new TextArea();
        txtResena.setPromptText("Resena o comentarios adicionales...");
        txtResena.setPrefRowCount(2);
        
        // Campo Ciclos
        TextField txtCiclos = new TextField();
        txtCiclos.setPromptText("Ej: DAM, DAW, ASIR, SMR...");
        
        Spinner<Integer> spinPlazas = new Spinner<>(1, 10, 4);
        spinPlazas.setPrefWidth(80);

        // Labels con estilo
        Label lblNombre = new Label("Nombre:");
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label lblSector = new Label("Sector:");
        lblSector.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label lblUbicacion = new Label("Ubicacion:");
        lblUbicacion.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label lblDesc = new Label("Descripcion:");
        lblDesc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label lblResena = new Label("Resena:");
        lblResena.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label lblCiclos = new Label("Ciclos:");
        lblCiclos.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label lblPlazas = new Label("Plazas:");
        lblPlazas.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        grid.addRow(0, lblNombre, txtNombre);
        grid.addRow(1, lblSector, txtSector);
        grid.addRow(2, lblUbicacion, txtUbicacion);
        grid.addRow(3, lblCiclos, txtCiclos);
        grid.addRow(4, lblDesc, txtDesc);
        grid.addRow(5, lblResena, txtResena);
        grid.addRow(6, lblPlazas, spinPlazas);

        contenido.getChildren().addAll(titulo, grid);
        
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Estilizar botones
        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btnOk.setText("Guardar");
        btnOk.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button btnCancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        btnCancel.setText("Cancelar");

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !txtNombre.getText().isEmpty()) {
                // Incluir ciclos en la descripcion o crear campo separado
                String descripcionCompleta = txtDesc.getText();
                if (!txtCiclos.getText().trim().isEmpty()) {
                    descripcionCompleta = "Ciclos: " + txtCiclos.getText().trim() + "\n\n" + descripcionCompleta;
                }
                return new Empresa(txtNombre.getText(), descripcionCompleta, txtResena.getText(),
                        txtSector.getText(), txtUbicacion.getText(), spinPlazas.getValue(), 0);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(emp -> {
            dataService.agregarEmpresa(emp);
            // Añadir datos simulados para la nueva empresa
            Random rand = new Random();
            valoraciones.put(emp.getNombre(), 3.5 + rand.nextDouble() * 1.5);
            ordenCreacion.put(emp.getNombre(), -1); // Nueva empresa, más reciente
            distancias.put(emp.getNombre(), 0.5 + rand.nextDouble() * 10);
            cargarEmpresas();
        });
    }

    // Listas de empresas del usuario (simuladas)
    private Map<String, List<String>> listasUsuario = new HashMap<>();

    private void inicializarListasUsuario() {
        // Simular listas del usuario con nombres de empresas
        listasUsuario.put("Favoritos", Arrays.asList("TechSolutions S.L.", "CloudServices Inc"));
        listasUsuario.put("Para contactar", Arrays.asList("DataAnalytics Corp"));
    }

    @FXML
    public void mostrarMisListas() {
        if (listasUsuario.isEmpty()) {
            inicializarListasUsuario();
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Mis Listas");

        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(25));
        contenido.setPrefSize(400, 280);
        contenido.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        Label titulo = new Label("Mis Listas");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.WHITE);

        contenido.getChildren().addAll(titulo,
                crearItemLista("❤️", "Favoritos", listasUsuario.get("Favoritos").size() + " empresas", "#FF6B6B", "#FFEBEE", dialog),
                crearItemLista("📞", "Para contactar", listasUsuario.get("Para contactar").size() + " empresa", "#4ECDC4", "#E0F7FA", dialog));

        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setStyle("-fx-background-color: transparent;");
        dialog.showAndWait();
    }

    private HBox crearItemLista(String emoji, String nombre, String cantidad, String colorIcono, String colorFondo, Dialog<Void> parentDialog) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(15));
        item.setStyle("-fx-background-color: " + colorFondo + "; -fx-background-radius: 15; -fx-cursor: hand;");

        // Emoji
        Label lblEmoji = new Label(emoji);
        lblEmoji.setFont(Font.font(24));
        lblEmoji.setMinWidth(40);
        lblEmoji.setAlignment(Pos.CENTER);

        // Info
        VBox info = new VBox(2);
        Label lblNombre = new Label(nombre);
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblNombre.setTextFill(Color.web("#333333"));
        Label lblCant = new Label(cantidad);
        lblCant.setFont(Font.font("Arial", 12));
        lblCant.setTextFill(Color.web("#666666"));
        info.getChildren().addAll(lblNombre, lblCant);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Flecha
        Label flecha = new Label("›");
        flecha.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        flecha.setTextFill(Color.web(colorIcono));

        item.getChildren().addAll(lblEmoji, info, spacer, flecha);

        // Hover effect
        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"));
        item.setOnMouseExited(e -> item.setStyle("-fx-background-color: " + colorFondo + "; -fx-background-radius: 15; -fx-cursor: hand;"));

        // Click para ver empresas de la lista
        item.setOnMouseClicked(e -> {
            parentDialog.close();
            mostrarEmpresasLista(nombre, emoji, colorIcono);
        });

        return item;
    }

    private void mostrarEmpresasLista(String nombreLista, String emoji, String color) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(nombreLista);

        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(20));
        contenido.setPrefWidth(450);
        contenido.setStyle("-fx-background-color: #F5F5F5;");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label lblEmoji = new Label(emoji);
        lblEmoji.setFont(Font.font(30));
        Label lblTitulo = new Label(nombreLista);
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.web(color));
        header.getChildren().addAll(lblEmoji, lblTitulo);

        contenido.getChildren().add(header);

        // Lista de empresas
        List<String> nombresEmpresas = listasUsuario.get(nombreLista);
        
        if (nombresEmpresas == null || nombresEmpresas.isEmpty()) {
            Label lblVacio = new Label("No hay empresas en esta lista");
            lblVacio.setFont(Font.font("Arial", 14));
            lblVacio.setTextFill(Color.GRAY);
            contenido.getChildren().add(lblVacio);
        } else {
            ScrollPane scroll = new ScrollPane();
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            scroll.setPrefHeight(300);

            VBox listaEmpresas = new VBox(10);
            listaEmpresas.setStyle("-fx-background-color: transparent;");

            for (String nombreEmpresa : nombresEmpresas) {
                // Buscar la empresa en el DataService
                Empresa empresa = dataService.getEmpresas().stream()
                        .filter(e -> e.getNombre().equals(nombreEmpresa))
                        .findFirst()
                        .orElse(null);

                if (empresa != null) {
                    listaEmpresas.getChildren().add(crearTarjetaEmpresaMini(empresa, color, dialog));
                }
            }

            scroll.setContent(listaEmpresas);
            contenido.getChildren().add(scroll);
        }

        // Botón volver
        Button btnVolver = new Button("Volver a Mis Listas");
        btnVolver.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 25; -fx-cursor: hand;");
        btnVolver.setOnAction(e -> {
            dialog.close();
            mostrarMisListas();
        });

        HBox boxBoton = new HBox(btnVolver);
        boxBoton.setAlignment(Pos.CENTER);
        boxBoton.setPadding(new Insets(10, 0, 0, 0));
        contenido.getChildren().add(boxBoton);

        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private VBox crearTarjetaEmpresaMini(Empresa empresa, String colorAccent, Dialog<Void> parentDialog) {
        VBox tarjeta = new VBox(8);
        tarjeta.setPadding(new Insets(12));
        tarjeta.setMinHeight(70);
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        // Fila 1: Nombre + Estado
        HBox fila1 = new HBox(10);
        fila1.setAlignment(Pos.CENTER_LEFT);

        Label lblNombre = new Label(empresa.getNombre());
        lblNombre.setStyle("-fx-font-family: Arial; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblEstado = new Label(empresa.estaOcupada() ? "OCUPADA" : "DISPONIBLE");
        lblEstado.setStyle("-fx-font-family: Arial; -fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + (empresa.estaOcupada() ? "#FF5722" : "#4CAF50") + "; -fx-background-radius: 4; -fx-padding: 3 6;");

        fila1.getChildren().addAll(lblNombre, spacer, lblEstado);

        // Fila 2: Info
        HBox fila2 = new HBox(15);
        fila2.setAlignment(Pos.CENTER_LEFT);
        
        Label lblUbicacion = new Label("Ubicacion: " + empresa.getUbicacion());
        lblUbicacion.setStyle("-fx-font-family: Arial; -fx-font-size: 11px; -fx-text-fill: #888888;");

        Label lblSector = new Label("Sector: " + empresa.getSector());
        lblSector.setStyle("-fx-font-family: Arial; -fx-font-size: 11px; -fx-text-fill: #888888;");

        fila2.getChildren().addAll(lblUbicacion, lblSector);

        // Fila 3: Plazas
        Label lblPlazas = new Label("Plazas: " + empresa.getEstadoPlazas());
        lblPlazas.setStyle("-fx-font-family: Arial; -fx-font-size: 11px; -fx-text-fill: #666666; -fx-font-weight: bold;");

        tarjeta.getChildren().addAll(fila1, fila2, lblPlazas);

        // Hover
        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 12; -fx-cursor: hand;"));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 12;"));

        // Click para ver detalles
        tarjeta.setOnMouseClicked(e -> {
            parentDialog.close();
            mostrarDetallesEmpresa(empresa);
        });

        return tarjeta;
    }

    @FXML
    private void abrirBusqueda() {
        cambiarVista("/fxml/busqueda.fxml", controller -> ((BusquedaController) controller).setUsuario(usuario));
    }

    @FXML
    private void abrirPerfil() {
        cambiarVista("/fxml/perfil.fxml", controller -> ((PerfilController) controller).setUsuario(usuario));
    }

    private void cambiarVista(String fxmlPath, java.util.function.Consumer<Object> setupController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            setupController.accept(loader.getController());
            Stage stage = (Stage) listaEmpresas.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}