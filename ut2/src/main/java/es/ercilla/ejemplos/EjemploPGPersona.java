package es.ercilla.ejemplos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import es.ercilla.clases.Persona;

public class EjemploPGPersona {

    public static void main(String[] args) {
        try {
            Connection conexion = crearConexion("ad", "dam86", "20999936Q");

            crearTablaPersona(conexion);

            Persona p1 = new Persona("Pepe", new BigDecimal(25000), Date.valueOf("2000-01-01"), null, true, null);

            //insertarNuevaPersona(conexion, p1);

            actualizarIngresoAnual(conexion, 2, new BigDecimal(35000));
            actualizarIngresoAnual(conexion, 3, new BigDecimal(67000));
            
            System.out.println(consultarTodasPersonas(conexion));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        
    }

    public static Connection crearConexion(String bd, String usuario, String clave) throws SQLException{
        try {
            return DriverManager.getConnection("jdbc:postgresql://192.168.29.12:10363/" + bd, usuario, clave);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void crearTablaPersona(Connection conexion){
        String sql = 
        """
        CREATE TABLE IF NOT EXISTS PERSONA(
        ID SERIAL PRIMARY KEY NOT NULL,
        NOMBRE VARCHAR(100) NOT NULL,
        INGRESO_ANUAL DECIMAL(10,2) NOT NULL,
        FECHA_NACIMIENTO DATE NOT NULL,
        TELEFONO VARCHAR(20),
        TIENE_CARNET BOOLEAN NOT NULL,
        ALTURA_METROS REAL
        );""";
        try{
            conexion.createStatement().executeUpdate(sql);
        }catch(SQLException e){
            e.printStackTrace();
        }
            
    }

    public static Integer insertarNuevaPersona(Connection conexion, Persona nuevaPersona){
        String sql = """
                INSERT INTO PERSONA(NOMBRE, INGRESO_ANUAL, FECHA_NACIMIENTO, TELEFONO, TIENE_CARNET, ALTURA_METROS) VALUES(?,?,?,?,?,?);
                """;
        
        try {
            PreparedStatement sentenciaParametrizada = conexion.prepareStatement(sql);    
            sentenciaParametrizada.setString(1, nuevaPersona.getNombre());
            sentenciaParametrizada.setBigDecimal(2, nuevaPersona.getIngresoAnual());
            sentenciaParametrizada.setDate(3, nuevaPersona.getFecha_nacimiento());
            if(nuevaPersona.getTelefono() == null){
                sentenciaParametrizada.setNull(1, Types.VARCHAR);
            }else{
                sentenciaParametrizada.setString(4, nuevaPersona.getTelefono());
            }
            
            sentenciaParametrizada.setBoolean(5, nuevaPersona.isTieneCarnet());
            if(nuevaPersona.getAltura() == null){
                sentenciaParametrizada.setNull(6, Types.REAL);
            }else{
                sentenciaParametrizada.setObject(6, nuevaPersona.getAltura());
            }
            

            return sentenciaParametrizada.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
        
    }

    public static ArrayList<Persona> consultarTodasPersonas(Connection conexion){
        ArrayList<Persona> personas = new ArrayList<Persona>();
        String sql = """
                SELECT * FROM PERSONA;
                """;
        try{
            ResultSet rs = conexion.createStatement().executeQuery(sql);

            while (rs.next()) {
                personas.add(new Persona(
                    rs.getObject("ID", Integer.class),
                    rs.getString("NOMBRE"),
                    rs.getBigDecimal("INGRESO_ANUAL"),
                    rs.getDate("FECHA_NACIMIENTO"),
                    rs.getObject("TELEFONO", String.class),
                    rs.getBoolean("TIENE_CARNET"),
                    rs.getObject("ALTURA_METROS", Float.class)
                ));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return personas;
    }

    public Persona buscarIdPersona(Connection conexion, Integer idPersona){
        String sql = "SELECT * FROM PERSONAS WHERE ID = ?";

        
        try {
            PreparedStatement sentenciaParametrizada = conexion.prepareStatement(sql);
            sentenciaParametrizada.setObject(1, idPersona);
            ResultSet rs = sentenciaParametrizada.executeQuery();

            if(rs.next()){
                return new Persona(
                    rs.getObject("ID", Integer.class),
                    rs.getString("NOMBRE"),
                    rs.getBigDecimal("INGRESO_ANUAL"),
                    rs.getDate("FECHA_NACIMIENTO"),
                    rs.getObject("TELEFONO", String.class),
                    rs.getBoolean("TIENE_CARNET"),
                    rs.getObject("ALTURA_METROS", Float.class)
                );
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static Persona actualizarIngresoAnual(Connection conexion, Integer idPersona, BigDecimal ingresoAnualActualizado){
        String sql = "UPDATE PERSONA SET INGRESO_ANUAL = ? WHERE ID = ? RETURNING *;";
        try {
            PreparedStatement sentenciaParametrizada = conexion.prepareStatement(sql);
            sentenciaParametrizada.setBigDecimal(1, ingresoAnualActualizado);
            sentenciaParametrizada.setInt(2, idPersona);
            ResultSet rs = sentenciaParametrizada.executeQuery();

            if (rs.next()) {
                return new Persona(
                    rs.getObject("ID", Integer.class),
                    rs.getString("NOMBRE"),
                    rs.getBigDecimal("INGRESO_ANUAL"),
                    rs.getDate("FECHA_NACIMIENTO"),
                    rs.getObject("TELEFONO", String.class),
                    rs.getBoolean("TIENE_CARNET"),
                    rs.getObject("ALTURA_METROS", Float.class)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Persona eliminarPersona(Connection conexion, Integer idPersona){
        Persona p = buscarIdPersona(conexion, idPersona);
        if(p != null){
            String sql = """
                    DELETE FROM PERSONAS WHERE ID = ?;
                    """;
            try {
                PreparedStatement sentenciaParametrizada = conexion.prepareStatement(sql);
                sentenciaParametrizada.setObject(1, idPersona);
                sentenciaParametrizada.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return p;
    }




}
