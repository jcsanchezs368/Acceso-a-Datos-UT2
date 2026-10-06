package es.ercilla.ejemplos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

public class EjemploPSQLException {

  public static void main(String[] args) {
    try {
      // CREAR CONEXIÓN Y SENTENCIA
      Connection conexion = crearConexion();
      Statement sentencia = conexion.createStatement();

      // BORRAR Y CREAR TABLAS E INSERTAR DATOS
      borrarTablas(sentencia);
      crearTablas(sentencia);
      insertarDatosIniciales(sentencia);

      // EJEMPLOS ERRORES POSTGRESQL
      ejemploPrimaryKey(sentencia);
      ejemploUnique(sentencia);
      ejemploForeignKey(sentencia);
      ejemploNotNull(sentencia);
      ejemploCheck(sentencia);

      sentencia.close();
      conexion.close();

    } catch (SQLException e) {
      System.err.println("Error JDBC: " + e.getSQLState());
      System.err.println(e.getMessage());
    }
  }

  public static void imprimirError(PSQLException e) {
    System.out.println("SQLState: " + e.getSQLState());
    System.out.println("Mensaje: " + e.getMessage());

    ServerErrorMessage detalle = e.getServerErrorMessage();
    if (detalle != null) {
      System.out.println("Tabla: " + detalle.getTable());
      System.out.println("Columna: " + detalle.getColumn());
      System.out.println("Restricción: " + detalle.getConstraint());
    }
    System.out.println();
  }

  public static void borrarTablas(Statement sentencia) throws SQLException {
    sentencia.executeUpdate("DROP TABLE IF EXISTS jugador");
    sentencia.executeUpdate("DROP TABLE IF EXISTS equipo");
  }

  public static void crearTablas(Statement sentencia) throws SQLException {
    // Tablas permanentes: se conservan al cerrar la conexión.
    sentencia.executeUpdate("""
        CREATE TABLE IF NOT EXISTS equipo (
            id INTEGER CONSTRAINT pk_equipo PRIMARY KEY,
            nombre VARCHAR(50) NOT NULL
        )
        """);

    sentencia.executeUpdate("""
        CREATE TABLE IF NOT EXISTS jugador (
            id INTEGER CONSTRAINT pk_jugador PRIMARY KEY,
            nombre VARCHAR(100) NOT NULL,
            fecha_nacimiento DATE NOT NULL,
            licencia VARCHAR(100) CONSTRAINT uq_jugador_licencia UNIQUE,
            equipo_id INTEGER,
            CONSTRAINT ck_jugador_mayor_edad
                CHECK (fecha_nacimiento <= (CURRENT_DATE - INTERVAL '18 years')::DATE),
            CONSTRAINT fk_jugador_equipo
                FOREIGN KEY (equipo_id) REFERENCES equipo(id)
        )
        """);
  }

  public static void insertarDatosIniciales(Statement sentencia) throws SQLException {
    sentencia.executeUpdate("INSERT INTO equipo VALUES (1, 'Águilas') ON CONFLICT DO NOTHING");
    sentencia.executeUpdate("""
        INSERT INTO jugador (id, nombre, licencia, equipo_id, fecha_nacimiento)
        VALUES (1, 'Ana', 'LIC-001', 1, DATE '1990-01-15')
        ON CONFLICT DO NOTHING
        """);
  }

  public static void ejemploPrimaryKey(Statement sentencia) throws SQLException {
    // 1. PRIMARY KEY: el identificador 1 ya existe. SQLState: 23505.
    System.out.println("--- PRIMARY KEY ---");
    try {
      sentencia.executeUpdate("""
          INSERT INTO jugador (id, nombre, licencia, equipo_id, fecha_nacimiento)
          VALUES (1, 'Pedro', 'LIC-002', 1, DATE '1990-01-15')
          """);
    } catch (PSQLException e) {
      imprimirError(e);
    }
  }

  public static void ejemploUnique(Statement sentencia) throws SQLException {
    // 2. UNIQUE: la licencia de Ana ya existe. SQLState: 23505.
    System.out.println("--- UNIQUE ---");
    try {
      sentencia.executeUpdate("""
          INSERT INTO jugador (id, nombre, licencia, equipo_id, fecha_nacimiento)
          VALUES (2, 'Lucía', 'LIC-001', 1, DATE '1990-01-15')
          """);
    } catch (PSQLException e) {
      imprimirError(e);
    }
  }

  public static void ejemploForeignKey(Statement sentencia) throws SQLException {
    // 3. FOREIGN KEY: el equipo 99 no existe. SQLState: 23503.
    System.out.println("--- FOREIGN KEY ---");
    try {
      sentencia.executeUpdate("""
          INSERT INTO jugador (id, nombre, licencia, equipo_id, fecha_nacimiento)
          VALUES (3, 'Luis', 'LIC-003', 99, DATE '1990-01-15')
          """);
    } catch (PSQLException e) {
      imprimirError(e);
    }
  }

  public static void ejemploNotNull(Statement sentencia) throws SQLException {
    // 4. NOT NULL: el nombre es obligatorio. SQLState: 23502.
    System.out.println("--- NOT NULL ---");
    try {
      sentencia.executeUpdate("""
          INSERT INTO jugador (id, nombre, licencia, equipo_id, fecha_nacimiento)
          VALUES (4, NULL, 'LIC-004', 1, DATE '1990-01-15')
          """);
    } catch (PSQLException e) {
      imprimirError(e);
    }
  }

  public static void ejemploCheck(Statement sentencia) throws SQLException {
    // 5. CHECK: el jugador tiene 16 años y debe tener al menos 18. SQLState: 23514.
    System.out.println("--- CHECK ---");
    try {
      sentencia.executeUpdate("""
          INSERT INTO jugador (id, nombre, licencia, equipo_id, fecha_nacimiento)
          VALUES (5, 'Marta', 'LIC-005', 1, (CURRENT_DATE - INTERVAL '16 years')::DATE)
          """);
    } catch (PSQLException e) {
      imprimirError(e);
    }
  }

  public static Connection crearConexion() throws SQLException {
    Connection conexion = DriverManager.getConnection("jdbc:postgresql://192.168.29.12:10363/", "dam86", "20999936Q");
    // Cada sentencia es independiente: un error no impide ejecutar el caso
    // siguiente.
    conexion.setAutoCommit(true);
    return conexion;
  }
}