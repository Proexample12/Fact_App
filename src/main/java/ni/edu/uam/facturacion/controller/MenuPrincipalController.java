package ni.edu.uam.facturacion.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import ni.edu.uam.facturacion.util.DatosApp;
import ni.edu.uam.facturacion.util.SceneManager;

import java.io.IOException;

public class MenuPrincipalController {
    @FXML private Label lblProductosRegistrados;
    @FXML private Label lblCategoriasRegistradas;
    @FXML private Label lblCargosRegistrados;

    @FXML
    private void initialize() {
        actualizarResumen();
    }

    @FXML
    private void abrirCategorias() {
        try {
            SceneManager.abrirVentana(
                    "/ni/edu/uam/facturacion/fxml/categoria-view.fxml",
                    "Gestión de categorías");
            actualizarResumen();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "No fue posible abrir Categorías.").showAndWait();
        }
    }

    @FXML
    private void abrirCargos() {
        try {
            SceneManager.abrirVentana(
                    "/ni/edu/uam/facturacion/fxml/cargo-view.fxml",
                    "Gestión de cargos");
            actualizarResumen();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "No fue posible abrir Cargos.").showAndWait();
        }
    }

    @FXML
    private void abrirProductos() {
        try {
            SceneManager.abrirVentana(
                    "/ni/edu/uam/facturacion/fxml/producto-view.fxml",
                    "Gestión de productos");
            actualizarResumen();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "No fue posible abrir Productos.").showAndWait();
        }
    }

    @FXML
    private void salir() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar la aplicación?", ButtonType.OK, ButtonType.CANCEL);
        if (alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Platform.exit();
        }
    }

    private void actualizarResumen() {
        lblProductosRegistrados.setText(String.valueOf(DatosApp.productos.size()));
        lblCategoriasRegistradas.setText(String.valueOf(DatosApp.categorias.size()));
        lblCargosRegistrados.setText(String.valueOf(DatosApp.cargos.size()));
    }
}
