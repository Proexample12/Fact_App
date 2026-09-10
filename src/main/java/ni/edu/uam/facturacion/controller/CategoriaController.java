package ni.edu.uam.facturacion.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.util.DatosApp;

public class CategoriaController {
    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActivo;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActivo;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tblCategorias.setItems(DatosApp.categorias);
        chkActivo.setSelected(true);
    }

    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isBlank()) {
            mensaje(Alert.AlertType.WARNING, "Ingrese el nombre de la categoría.");
            return;
        }

        boolean existe = DatosApp.categorias.stream()
                .anyMatch(categoria -> categoria.getNombre().equalsIgnoreCase(nombre));
        if (existe) {
            mensaje(Alert.AlertType.WARNING, "Ya existe una categoría con ese nombre.");
            return;
        }

        DatosApp.categorias.add(new Categoria(DatosApp.siguienteCategoriaId(),
                nombre, chkActivo.isSelected()));
        mensaje(Alert.AlertType.INFORMATION, "Categoría agregada correctamente.");
        limpiar();
    }

    @FXML
    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    private void limpiar() {
        txtNombre.clear();
        chkActivo.setSelected(true);
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
