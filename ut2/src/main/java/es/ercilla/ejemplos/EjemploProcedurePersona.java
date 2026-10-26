package es.ercilla.ejemplos;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/*
  CREATE TABLE IF NOT EXISTS persona (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ingreso_anual DECIMAL(10,2) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    telefono VARCHAR(20),
    tiene_carnet BOOLEAN NOT NULL,
    altura_metros REAL
  );

  INSERT INTO persona (nombre, ingreso_anual, fecha_nacimiento, telefono, tiene_carnet, altura_metros)
  VALUES
    ('Ana García', 24000.00, '1996-04-12', NULL, TRUE, 1.65),
    ('Pedro Ortega', 30000.00, '1981-02-20', '600123456', FALSE, 1.80),
    ('Lucía Pérez', 28500.50, '1972-06-08', '600654321', TRUE, NULL),
    ('David Ruiz', 21500.00, '2000-11-03', NULL, FALSE, 1.75),
    ('Marta López', 35000.00, '1988-09-25', '600987654', TRUE, 1.70);
*/

public class EjemploProcedurePersona {

  // Record para almacenar el resultado de la ejecución del procedimiento
  public record ResultadoIngresos(String nombre, BigDecimal ingresos) {

  }

  public static void main(String[] args) {
    try {
      // 1. Establecer conexión con BD
      Connection conexion = crearConexion("ad", "dam86", "20999936Q");

      // 2. Crear procedimiento para aumentar el ingreso anual de una persona
      crearProcedimientoAumentarIngresos(conexion);

      // 3. Aumentar ingresos a las personas 1 y 2
      ResultadoIngresos rs1 = aumentarIngresos(conexion, 10, 500);
      ResultadoIngresos rs2 = aumentarIngresos(conexion, 11, 3000);

      System.out.println(rs1.nombre() + " - " + rs1.ingresos());
      System.out.println(rs2.nombre() + " - " + rs2.ingresos());

      // 4. Cerrar conexión
      conexion.close();
    } catch (SQLException e) {
      System.err.println("Error JDBC: " + e.getSQLState());
      System.err.println(e.getMessage());
    }
  }

  // Crea una conexión con la BD
  public static Connection crearConexion(String bd, String usuario, String clave) throws SQLException {
    Connection conexion = DriverManager.getConnection(
        "jdbc:postgresql://192.168.29.12:10363/" + bd,
        usuario,
        clave);

    System.out.println("Conexión con \"" + bd + "\" establecida");

    return conexion;
  }

  public static void crearProcedimientoAumentarIngresos(Connection conexion) throws SQLException {
    String procedimientoSql = """
            CREATE OR REPLACE PROCEDURE aumentar_ingresos(
                IN p_id INTEGER,
                IN p_incremento NUMERIC,
                OUT p_nombre VARCHAR,
                OUT p_ingresos_nuevo NUMERIC
            )
            LANGUAGE plpgsql
            AS $$
            BEGIN
                IF p_incremento IS NULL OR p_incremento <= 0 THEN
                    RAISE EXCEPTION 'El incremento debe ser mayor que cero';
                END IF;

                UPDATE persona
                SET ingreso_anual = ingreso_anual + p_incremento
                WHERE id = p_id
                RETURNING nombre, ingreso_anual INTO p_nombre, p_ingresos_nuevo;

                IF NOT FOUND THEN
                    RAISE EXCEPTION 'No existe la persona con id %', p_id;
                END IF;
            END;
            $$;
        """;

    // Crear sentencia de creación del procedimiento
        Statement sentenciaProcedimiento = conexion.createStatement();

    // Ejecutar la sentencia
        sentenciaProcedimiento.execute(procedimientoSql);

    // Cerrar el statement
        sentenciaProcedimiento.close();
  }

  public static ResultadoIngresos aumentarIngresos(Connection conexion, int idPersona, double aumentoIngresos)
      throws SQLException {
        // Crear el CallableStatement para llamar al procedimiento
            CallableStatement sentenciaLlamada = conexion.prepareCall("CALL aumentar_ingresos(?,?,?,?)");
        
        // Definir los parámetros de salida del procedimiento para poder recoger los
        // valores después

        sentenciaLlamada.registerOutParameter(3, Types.VARCHAR);
        sentenciaLlamada.registerOutParameter(4, Types.NUMERIC);

        // Definir los valores de los parámetros de entrada del procedimientos

        sentenciaLlamada.setInt(1, idPersona);
        sentenciaLlamada.setBigDecimal(2, new BigDecimal(aumentoIngresos));

        // Obtener los valores de los parámetros de salida registrados anteriormente
        sentenciaLlamada.execute();
        String nombrePersona = sentenciaLlamada.getString(3);
        BigDecimal ingresosNuevos = sentenciaLlamada.getBigDecimal(4);

        // Cerrar el callablestatement
        sentenciaLlamada.close();

        return new ResultadoIngresos(nombrePersona, ingresosNuevos);
  }
}