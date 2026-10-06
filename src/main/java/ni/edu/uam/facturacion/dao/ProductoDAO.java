package ni.edu.uam.facturacion.dao;

import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.model.Producto;
import ni.edu.uam.facturacion.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public List<Producto> listar() throws SQLException {
        String sql = """
                SELECT p.id,
                       p.codigo,
                       p.nombre,
                       p.precio_venta,
                       p.existencia,
                       p.activo,
                       c.id AS categoria_id,
                       c.nombre AS categoria_nombre,
                       c.activo AS categoria_activo
                FROM producto p
                INNER JOIN categoria c ON c.id = p.categoria_id
                ORDER BY p.nombre
                """;

        List<Producto> productos = new ArrayList<>();

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }

        return productos;
    }

    public int contar() throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM producto
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    public void guardar(Producto producto) throws SQLException {
        String sql = """
                INSERT INTO producto (codigo, nombre, categoria_id, precio_venta, existencia, activo)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getCategoria().getId());
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getExistencia());
            ps.setBoolean(6, producto.isActivo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setId(rs.getInt(1));
                }
            }
        }
    }

    public void actualizar(Producto producto) throws SQLException {
        String sql = """
                UPDATE producto
                SET codigo = ?,
                    nombre = ?,
                    categoria_id = ?,
                    precio_venta = ?,
                    existencia = ?,
                    activo = ?
                WHERE id = ?
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getCategoria().getId());
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getExistencia());
            ps.setBoolean(6, producto.isActivo());
            ps.setInt(7, producto.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = """
                DELETE FROM producto
                WHERE id = ?
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existeCodigo(String codigo) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM producto
                WHERE codigo = ?
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean existeCodigoEnOtroRegistro(String codigo, int id) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM producto
                WHERE codigo = ?
                  AND id <> ?
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, codigo);
            ps.setInt(2, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getInt("categoria_id"),
                rs.getString("categoria_nombre"),
                rs.getBoolean("categoria_activo")
        );

        return new Producto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                categoria,
                rs.getBigDecimal("precio_venta"),
                rs.getInt("existencia"),
                null,
                rs.getBoolean("activo")
        );
    }
}
