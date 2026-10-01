package es.ercilla.ejemplos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import es.ercilla.clases.Producto;

public class EjemploH2Producto {
    public static void main(String[] args) {
        try {
            Connection conexion = DriverManager.getConnection("jdbc:h2:./datos/tienda", "sa", "");

            String sentenciaCrearTablaString = """
                CREATE TABLE IF NOT EXISTS producto (
                id UUID PRIMARY KEY NOT NULL,
                nombre VARCHAR(100) NOT NULL,
                precio DECIMAL(8,2) NOT NULL,
                stock INTEGER NOT NULL,
                garantia_meses INTEGER,
                disponible BOOLEAN NOT NULL,
                fecha_alta DATE
            );
            """;

            Statement sentenciaCreateTable = conexion.createStatement();

            int respuesta = sentenciaCreateTable.executeUpdate(sentenciaCrearTablaString);

            sentenciaCreateTable.close();

            //INSERTAR PRODUCTOS:

            System.out.println(respuesta);

            PreparedStatement sentenciaInsertarProducto = conexion.prepareStatement("INSERT INTO PRODUCTO(id, nombre, precio, stock, garantia_meses, disponible, fecha_alta) VALUES(?, ?, ?, ?, ?, ?, ?);");

            Producto p1 = new Producto(UUID.randomUUID(), "Teclado", new BigDecimal("25.99"), 15, null, true, Date.valueOf("2026-01-01"));

            sentenciaInsertarProducto.setObject(1, p1.getId());
            sentenciaInsertarProducto.setString(2, p1.getNombre());
            sentenciaInsertarProducto.setObject(3, p1.getPrecio());
            sentenciaInsertarProducto.setInt(4, p1.getStock());
            sentenciaInsertarProducto.setObject(5, p1.getGarantiaMeses());
            sentenciaInsertarProducto.setBoolean(6, p1.isDisponible());
            sentenciaInsertarProducto.setDate(7, p1.getFechaAlta());

            int resultadoInserccion = sentenciaInsertarProducto.executeUpdate();
            System.out.println(resultadoInserccion);

            sentenciaInsertarProducto.close();
            
            Statement sentenciaConsultaProductos = conexion.createStatement();
            
            String consulta = "SELECT * FROM producto;";

            ResultSet rs = sentenciaConsultaProductos.executeQuery(consulta);

            ArrayList<Producto> productos = new ArrayList<Producto>();

            while (rs.next()) {
                Producto producto = new Producto(
                    rs.getObject("id", UUID.class), //por cada objeto recibido, se debe de parsear al objeto necesario para crear nuestro objeto en java
                    rs.getString("nombre"),
                    rs.getObject("precio", BigDecimal.class),
                    rs.getInt("stock"),
                    rs.getObject("garantia_meses", Integer.class),
                    rs.getBoolean("disponible"),
                    rs.getDate("fecha_alta")
                );

                productos.add(producto);
            }

            System.out.println(productos);

            sentenciaConsultaProductos.close();
            rs.close();

            
            conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
