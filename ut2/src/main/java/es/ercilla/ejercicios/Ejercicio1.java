package es.ercilla.ejercicios;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

import org.postgresql.util.PSQLException;

public class Ejercicio1 {
    public static void main(String[] args) { 
    }
        
        /** 
         * Solicita los datos de un empleado y lo registra en la base de datos. 
         * Guarda el segundo apellido como NULL si se deja vacío y gestiona los errores SQL. 
         * 
         * @param conexion conexión abierta con la base de datos 
         * @param teclado lector utilizado para solicitar los datos por consola 
         * @throws SQLException si se produce un error al acceder a la base de datos 
         */ 
        public static void registrarEmpleado(Connection conexion, Scanner teclado) {  
            String sql = "INSERT INTO empleado VALUES(?,?,?,?,?,?,?,?)";
            try {
                PreparedStatement sentenciaParametrizada = conexion.prepareStatement(sql);
                String dni = teclado.nextLine();
            
                sentenciaParametrizada.setString(1, dni);
                sentenciaParametrizada.setString(2, teclado.nextLine());
                sentenciaParametrizada.setString(3, teclado.nextLine());

                String apellido2 = teclado.nextLine();

                if(apellido2.trim().isEmpty()){
                        sentenciaParametrizada.setNull(4, Types.VARCHAR);
                }else{
                    sentenciaParametrizada.setString(4, apellido2);
                }

                sentenciaParametrizada.setString(5, teclado.nextLine());
                sentenciaParametrizada.setString(6, teclado.nextLine());
                sentenciaParametrizada.setDate(7, Date.valueOf(teclado.nextLine()));
                sentenciaParametrizada.setString(8, teclado.nextLine());

                sentenciaParametrizada.executeUpdate();
                
            } catch (SQLException e) {
                mostrarError(e);
            }

        } 
        
        /** 
         * Solicita un DNI y elimina al empleado correspondiente. 
         * Informa si no existe y gestiona los errores SQL de la operación. 
         * 
         * @param conexion conexión abierta con la base de datos 
         * @param teclado lector utilizado para solicitar el DNI por consola 
         * @throws SQLException si se produce un error al acceder a la base de datos 
         */ 
        public static void eliminarEmpleado(Connection conexion, Scanner teclado){ 
            String sql = "DELETE FROM empleado WHERE DNI = ?;";

            try {
                PreparedStatement sentenciaParametrizada = conexion.prepareStatement(sql);
                sentenciaParametrizada.setString(1, teclado.nextLine());
                sentenciaParametrizada.executeUpdate();

            } catch (SQLException e) {
                mostrarError(e);
            }
        } 
        
        /** 
         * Solicita un DNI, el campo que se desea modificar (tlf o email) y su nuevo valor. 
         * Actualiza al empleado, informa si no existe y gestiona los errores SQL. 
         * 
         * @param conexion conexión abierta con la base de datos 
         * @param teclado lector utilizado para solicitar los datos por consola 
         * @throws SQLException si se produce un error al acceder a la base de datos 
         */ 
        public static void modificarEmpleado(Connection conexion, Scanner teclado) {
            String sql = "UPDATE empleados SET ?=? WHERE DNI=?;";
            try{
                PreparedStatement sentenciaParametrizada = conexion.prepareStatement(sql);



            System.out.println("Elija el campo que desea editar:");
            System.out.println("1) Teléfono");
            System.out.println("2) Correo");
            switch (teclado.nextInt()) {
                case 1:
                    teclado.nextLine();
                    sentenciaParametrizada.setString(1, "telefono");
                    System.out.println("Introduzca el teléfono nuevo: ");
                    sentenciaParametrizada.setString(2, teclado.nextLine());
                    break;

                case 2:
                    teclado.nextLine();
                    sentenciaParametrizada.setString(1, "correo");
                    System.out.println("Introduzca el correo nuevo: ");
                    sentenciaParametrizada.setString(2, teclado.nextLine());
                    break;
            
                default:
                    break;
            }

                        }catch(SQLException e){
                mostrarError(e);
            }

        } 
        
        /** 
         * Muestra un mensaje de error por consola. 
         * Identifica los duplicados de DNI o email por el nombre de la restricción 
         * cuando la excepción es de PostgreSQL. 
         * Para los demás errores muestra el mensaje original de la excepción. 
         * 
         * @param e excepción SQL que se desea mostrar 
         */ 
        public static void mostrarError(SQLException e) { 
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Mensaje: " + e.getMessage());
        }

}
