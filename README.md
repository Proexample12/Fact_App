# Sistema de Facturacion JavaFX

Proyecto Maven con JavaFX configurado para Java 21.

## Requisitos

- JDK Temurin 21
- Maven Wrapper incluido (`mvnw.cmd` en Windows)
- PostgreSQL con la base de datos `Fact_App`

## Base de datos

La conexión está en `src/main/java/ni/edu/uam/facturacion/util/ConexionDB.java`.

Valores por defecto:

- Servidor: `localhost`
- Puerto: `5432`
- Base de datos: `Fact_App`
- Usuario: `postgres`
- Contraseña: `admin`

Si el usuario no es `postgres`, cambie la variable de entorno antes de ejecutar:

```powershell
$env:FACT_APP_DB_USER="usuario_postgres"
$env:FACT_APP_DB_PASSWORD="admin"
```

También puede ajustar `FACT_APP_DB_SERVER`, `FACT_APP_DB_PORT` y `FACT_APP_DB_NAME`.

El script de tablas está en `src/main/resources/database/fact_app_schema.sql`.

## Ejecucion

```powershell
.\mvnw.cmd clean javafx:run
```
