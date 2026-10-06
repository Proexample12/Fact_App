package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.model.Categoria;

import java.sql.SQLException;

public class CategoriaController {
    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActivo;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActivo;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tblCategorias.setItems(categorias);
        tblCategorias.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionada) -> cargarFormulario(seleccionada)
        );
        chkActivo.setSelected(true);
        cargarCategorias();
    }

    @FXML
    private void guardar() {
        try {
            Categoria categoria = obtenerCategoriaFormulario(null);

            if (categoriaDAO.existeNombre(categoria.getNombre())) {
                mostrarAdvertencia("Categoría duplicada", "Ya existe una categoría con ese nombre.");
                return;
            }

            categoriaDAO.guardar(categoria);
            mostrarExito("Categoría registrada", "La categoría se guardó correctamente.");
            cargarCategorias();
            limpiar();
        } catch (IllegalArgumentException e) {
            mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible registrar la categoría.");
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        Categoria seleccionada = tblCategorias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAdvertencia("Seleccione una categoría", "Debe seleccionar la categoría que desea actualizar.");
            return;
        }

        try {
            Categoria categoria = obtenerCategoriaFormulario(seleccionada.getId());

            if (categoriaDAO.existeNombreEnOtroRegistro(categoria.getNombre(), categoria.getId())) {
                mostrarAdvertencia("Categoría duplicada", "Ya existe otra categoría con ese nombre.");
                return;
            }

            categoriaDAO.actualizar(categoria);
            mostrarExito("Categoría actualizada", "La categoría se actualizó correctamente.");
            cargarCategorias();
            limpiar();
        } catch (IllegalArgumentException e) {
            mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible actualizar la categoría.");
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        Categoria seleccionada = tblCategorias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAdvertencia("Seleccione una categoría", "Debe seleccionar la categoría que desea eliminar.");
            return;
        }

        if (!confirmar("Eliminar categoría", "¿Desea eliminar la categoría seleccionada?")) {
            return;
        }

        try {
            if (categoriaDAO.tieneProductos(seleccionada.getId())) {
                mostrarAdvertencia(
                        "Categoría con productos",
                        "No puede eliminar la categoría porque tiene productos asociados."
                );
                return;
            }

            categoriaDAO.eliminar(seleccionada.getId());
            mostrarExito("Categoría eliminada", "La categoría se eliminó correctamente.");
            cargarCategorias();
            limpiar();
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible eliminar la categoría.");
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void limpiar() {
        txtNombre.clear();
        chkActivo.setSelected(true);
        tblCategorias.getSelectionModel().clearSelection();
    }

    @FXML
    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    private Categoria obtenerCategoriaFormulario(Integer id) {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            txtNombre.requestFocus();
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }

        return new Categoria(id, nombre, chkActivo.isSelected());
    }

    private void cargarCategorias() {
        try {
            categorias.setAll(categoriaDAO.listar());
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible cargar las categorías.");
            System.err.println(e.getMessage());
        }
    }

    private void cargarFormulario(Categoria categoria) {
        if (categoria == null) {
            return;
        }

        txtNombre.setText(categoria.getNombre());
        chkActivo.setSelected(Boolean.TRUE.equals(categoria.getActivo()));
    }

    private boolean confirmar(String titulo, String texto) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, texto, ButtonType.OK, ButtonType.CANCEL);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        return alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void mostrarExito(String titulo, String texto) {
        mostrarMensaje(Alert.AlertType.INFORMATION, titulo, texto);
    }

    private void mostrarAdvertencia(String titulo, String texto) {
        mostrarMensaje(Alert.AlertType.WARNING, titulo, texto);
    }

    private void mostrarError(String titulo, String texto) {
        mostrarMensaje(Alert.AlertType.ERROR, titulo, texto);
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String texto) {
        Alert alerta = new Alert(tipo, texto, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
