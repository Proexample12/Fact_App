package ni.edu.uam.facturacion.controller;

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
import ni.edu.uam.facturacion.util.DatosApp;

public class CargoController {
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private TableView<Cargo> tblCargos;
    @FXML private TableColumn<Cargo, Integer> colId;
    @FXML private TableColumn<Cargo, String> colNombre;
    @FXML private TableColumn<Cargo, String> colDescripcion;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        tblCargos.setItems(DatosApp.cargos);
    }

    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isBlank() || descripcion.isBlank()) {
            mensaje(Alert.AlertType.WARNING, "Complete el nombre y la descripción.");
            return;
        }

        boolean existe = DatosApp.cargos.stream()
                .anyMatch(cargo -> cargo.getNombre().equalsIgnoreCase(nombre));
        if (existe) {
            mensaje(Alert.AlertType.WARNING, "Ya existe un cargo con ese nombre.");
            return;
        }

        DatosApp.cargos.add(new Cargo(DatosApp.siguienteCargoId(), nombre, descripcion));
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
