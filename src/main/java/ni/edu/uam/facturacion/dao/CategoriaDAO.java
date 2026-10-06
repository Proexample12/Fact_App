package ni.edu.uam.facturacion.dao;

import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    public List<Categoria> listar() throws SQLException {
        String sql = """
                SELECT id, nombre, activo
                FROM categoria
                ORDER BY nombre
                """;

        List<Categoria> categorias = new ArrayList<>();

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
        }

        return categorias;
    }

    public int contar() throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM categoria
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

    public void guardar(Categoria categoria) throws SQLException {
        String sql = """
                INSERT INTO categoria (nombre, activo)
                VALUES (?, ?)
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, categoria.getNombre());
            ps.setBoolean(2, Boolean.TRUE.equals(categoria.getActivo()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setId(rs.getInt(1));
                }
            }
        }
    }

    public void actualizar(Categoria categoria) throws SQLException {
        String sql = """
                UPDATE categoria
                SET nombre = ?, activo = ?
                WHERE id = ?
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, categoria.getNombre());
            ps.setBoolean(2, Boolean.TRUE.equals(categoria.getActivo()));
            ps.setInt(3, categoria.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = """
                DELETE FROM categoria
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

    public boolean existeNombre(String nombre) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM categoria
                WHERE LOWER(nombre) = LOWER(?)
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean existeNombreEnOtroRegistro(String nombre, int id) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM categoria
                WHERE LOWER(nombre) = LOWER(?)
                  AND id <> ?
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, nombre);
            ps.setInt(2, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean tieneProductos(int categoriaId) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM producto
                WHERE categoria_id = ?
                """;

        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, categoriaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        return new Categoria(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getBoolean("activo")
        );
    }
}
