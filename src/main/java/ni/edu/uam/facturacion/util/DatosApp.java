package ni.edu.uam.facturacion.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ni.edu.uam.facturacion.model.Cargo;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.model.Producto;

public final class DatosApp {
    public static final ObservableList<Producto> productos =
            FXCollections.observableArrayList();

    public static final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList(
                    new Categoria(1, "Alimentos", true),
                    new Categoria(2, "Bebidas", true),
                    new Categoria(3, "Limpieza", true));

    public static final ObservableList<Cargo> cargos =
            FXCollections.observableArrayList();

    private static int siguienteProductoId = 1;
    private static int siguienteCategoriaId = 4;
    private static int siguienteCargoId = 1;

    private DatosApp() {
    }

    public static int siguienteProductoId() {
        return siguienteProductoId++;
    }

    public static int siguienteCategoriaId() {
        return siguienteCategoriaId++;
    }

    public static int siguienteCargoId() {
        return siguienteCargoId++;
    }
}
