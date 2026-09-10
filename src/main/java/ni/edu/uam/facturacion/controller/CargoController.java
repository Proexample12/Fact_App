package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ni.edu.uam.facturacion.model.Cargo;

public class CargoController {
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private TableView<Cargo> tblCargos;
    @FXML private TableColumn<Cargo, Integer> colId;
    @FXML private TableColumn<Cargo, String> colNombre;
    @FXML private TableColumn<Cargo, String> colDescripcion;

    private final ObservableList<Cargo> cargos =
            FXCollections.observableArrayList();
    private int siguienteId = 1;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        tblCargos.setItems(cargos);
    }

    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isBlank() || descripcion.isBlank()) {
            mensaje(Alert.AlertType.WARNING, "Complete el nombre y la descripción.");
            return;
        }

        boolean existe = cargos.stream()
                .anyMatch(cargo -> cargo.getNombre().equalsIgnoreCase(nombre));
        if (existe) {
            mensaje(Alert.AlertType.WARNING, "Ya existe un cargo con ese nombre.");
            return;
        }

        cargos.add(new Cargo(siguienteId++, nombre, descripcion));
        mensaje(Alert.AlertType.INFORMATION, "Cargo agregado correctamente.");
        limpiar();
    }

    @FXML
    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    private void limpiar() {
        txtNombre.clear();
        txtDescripcion.clear();
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
