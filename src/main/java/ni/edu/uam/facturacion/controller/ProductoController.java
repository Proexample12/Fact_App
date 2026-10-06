package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.dao.ProductoDAO;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.model.Producto;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Objects;

public class ProductoController {
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private String rutaImagen;

    @FXML
    private void initialize() {
        cmbCategoria.setItems(categorias);

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tblProductos.setItems(productos);
        tblProductos.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> cargarFormulario(seleccionado)
        );
        chkActivo.setSelected(true);
        cargarDatos();
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivo = chooser.showOpenDialog(txtCodigo.getScene().getWindow());
        if (archivo != null) {
            rutaImagen = archivo.toURI().toString();
            imgProducto.setImage(new Image(rutaImagen));
        }
    }

    @FXML
    private void guardar() {
        try {
            Producto producto = obtenerProductoFormulario(null);

            if (productoDAO.existeCodigo(producto.getCodigo())) {
                mostrarAdvertencia("Código duplicado", "Ya existe un producto con ese código.");
                return;
            }

            productoDAO.guardar(producto);
            mostrarExito("Producto registrado", "La información fue almacenada correctamente.");
            cargarProductos();
            limpiar();
        } catch (IllegalArgumentException e) {
            mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible registrar el producto.");
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un producto", "Debe seleccionar el producto que desea actualizar.");
            return;
        }

        try {
            Producto producto = obtenerProductoFormulario(seleccionado.getId());

            if (productoDAO.existeCodigoEnOtroRegistro(producto.getCodigo(), producto.getId())) {
                mostrarAdvertencia("Código duplicado", "Ya existe otro producto con ese código.");
                return;
            }

            productoDAO.actualizar(producto);
            mostrarExito("Producto actualizado", "El producto se actualizó correctamente.");
            cargarProductos();
            limpiar();
        } catch (IllegalArgumentException e) {
            mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible actualizar el producto.");
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un producto", "Debe seleccionar el producto que desea eliminar.");
            return;
        }

        if (!confirmar("Eliminar producto", "¿Desea eliminar el producto seleccionado?")) {
            return;
        }

        try {
            productoDAO.eliminar(seleccionado.getId());
            mostrarExito("Producto eliminado", "El producto se eliminó correctamente.");
            cargarProductos();
            limpiar();
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible eliminar el producto.");
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        imgProducto.setImage(null);
        rutaImagen = null;
        tblProductos.getSelectionModel().clearSelection();
    }

    @FXML
    private void cerrar() {
        ((Stage) txtCodigo.getScene().getWindow()).close();
    }

    private Producto obtenerProductoFormulario(Integer id) {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (codigo.isEmpty()) {
            txtCodigo.requestFocus();
            throw new IllegalArgumentException("El código es obligatorio.");
        }

        if (nombre.isEmpty()) {
            txtNombre.requestFocus();
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        Categoria categoria = cmbCategoria.getSelectionModel().getSelectedItem();
        if (categoria == null) {
            cmbCategoria.requestFocus();
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }

        BigDecimal precio;

        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            txtPrecio.requestFocus();
            throw new IllegalArgumentException("El precio debe ser un valor numérico.");
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            txtPrecio.requestFocus();
            throw new IllegalArgumentException("El precio de venta debe ser mayor que cero.");
        }

        int existencia;

        try {
            existencia = Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            txtExistencia.requestFocus();
            throw new IllegalArgumentException("La existencia debe ser un número entero.");
        }

        if (existencia < 0) {
            txtExistencia.requestFocus();
            throw new IllegalArgumentException("La existencia no puede ser negativa.");
        }

        return new Producto(
                id,
                codigo,
                nombre,
                categoria,
                precio,
                existencia,
                rutaImagen,
                chkActivo.isSelected()
        );
    }

    private void cargarDatos() {
        cargarCategorias();
        cargarProductos();
    }

    private void cargarCategorias() {
        try {
            categorias.setAll(categoriaDAO.listar());
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible cargar las categorías.");
            System.err.println(e.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            productos.setAll(productoDAO.listar());
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible cargar los productos.");
            System.err.println(e.getMessage());
        }
    }

    private void cargarFormulario(Producto producto) {
        if (producto == null) {
            return;
        }

        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(producto.getPrecioVenta().toPlainString());
        txtExistencia.setText(String.valueOf(producto.getExistencia()));
        seleccionarCategoria(producto.getCategoria());
        chkActivo.setSelected(producto.isActivo());
        rutaImagen = producto.getRutaImagen();
        imgProducto.setImage(rutaImagen == null || rutaImagen.isBlank() ? null : new Image(rutaImagen));
    }

    private void seleccionarCategoria(Categoria categoriaProducto) {
        if (categoriaProducto == null) {
            cmbCategoria.getSelectionModel().clearSelection();
            return;
        }

        categorias.stream()
                .filter(categoria -> Objects.equals(categoria.getId(), categoriaProducto.getId()))
                .findFirst()
                .ifPresentOrElse(
                        cmbCategoria::setValue,
                        () -> cmbCategoria.getSelectionModel().clearSelection()
                );
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
