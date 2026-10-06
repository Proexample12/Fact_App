package ni.edu.uam.facturacion.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionDB {
    private static final String DEFAULT_SERVER = "localhost";
    private static final String DEFAULT_PORT = "5432";
    private static final String DEFAULT_DATABASE = "Fact_App";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "admin";

    private ConexionDB() {
    }

    public static Connection getConnection() throws SQLException {
        String url = crearUrl();
        Properties properties = crearPropiedades();
        return DriverManager.getConnection(url, properties);
    }

    private static String crearUrl() {
        String servidor = obtenerConfiguracion("factapp.db.server", "FACT_APP_DB_SERVER", DEFAULT_SERVER);
        String puerto = obtenerConfiguracion("factapp.db.port", "FACT_APP_DB_PORT", DEFAULT_PORT);
        String baseDatos = obtenerConfiguracion("factapp.db.name", "FACT_APP_DB_NAME", DEFAULT_DATABASE);

        return "jdbc:postgresql://" + servidor + ":" + puerto + "/" + baseDatos
                + "?sslmode=disable&connectTimeout=10";
    }

    private static Properties crearPropiedades() {
        Properties properties = new Properties();
        properties.setProperty("user", obtenerConfiguracion("factapp.db.user", "FACT_APP_DB_USER", DEFAULT_USER));
        properties.setProperty("password", obtenerConfiguracion(
                "factapp.db.password",
                "FACT_APP_DB_PASSWORD",
                DEFAULT_PASSWORD
        ));

        return properties;
    }

    private static String obtenerConfiguracion(String propiedad, String variableEntorno, String valorDefecto) {
        String valor = System.getProperty(propiedad);
        if (valor != null && !valor.isBlank()) {
            return valor.trim();
        }

        valor = System.getenv(variableEntorno);
        if (valor != null && !valor.isBlank()) {
            return valor.trim();
        }

        return valorDefecto;
    }
}
