package es.ercilla.ejercicios;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

public class Ejercicio1 {
    public static void main(String[] args) { 
        try {
            Scanner teclado = new Scanner(System.in);
            Connection conexion = DriverManager.getConnection("jdbc:postgresql://192.168.29.12:10363/ad", "dam86", "20999936Q");
            boolean salir = false;
            do {
                System.out.println("RRHH");
                System.out.println("1. Registrar empleado");
                System.out.println("2. Eliminar empleado");
                System.out.println("3. Modificar empleado");
                System.out.println("0. Salir");
                System.out.print("Elije una opción: ");
                int opcion = teclado.nextInt();
                teclado.nextLine();
                switch (opcion) {
                case 1:
                        registrarEmpleado(conexion, teclado);
                        break;
                case 2:
                        eliminarEmpleado(conexion, teclado);
                        break;
                case 3:
                        modificarEmpleado(conexion, teclado);
                        break;
                case 0:
                        conexion.close();
                        teclado.close();
                        salir = true;
                        break;
                default:
                    System.err.println("OPCIÓN INCORRECTA");
                        break;
                }
            } while (!salir);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                System.out.println("REGISTRAR NUEVO EMPLEADO");
                System.out.print("DNI: ");
                sentenciaParametrizada.setString(1, teclado.nextLine());
                System.out.print("Introduce el nombre del empleado: ");
                sentenciaParametrizada.setString(2, teclado.nextLine());
                System.out.print("Introduce el primer apellido del empleado: ");
                sentenciaParametrizada.setString(3, teclado.nextLine());
                System.out.print("(OPCIONAL) Introduce el segundo apellido del empleado: ");
                String apellido2 = teclado.nextLine();

                if(apellido2.trim().isEmpty()){
                        sentenciaParametrizada.setNull(4, Types.VARCHAR);
                }else{
                    sentenciaParametrizada.setString(4, apellido2);
                }
                System.out.print("Introduce el email del empleado: ");
                sentenciaParametrizada.setString(5, teclado.nextLine());
                System.out.print("Introduce el teléfono del empleado: ");
                sentenciaParametrizada.setString(6, teclado.nextLine());
                System.out.print("Introduce la fecha de nacimiento del empleado (yyyy-mm-dd): ");
                sentenciaParametrizada.setDate(7, Date.valueOf(teclado.nextLine()));
                System.out.println("Seleccione el departamento: ");
                System.out.println("1) Ventas");
                System.out.println("2) Marketing");
                System.out.println("3) RRHH");

                switch (teclado.nextInt()) {
                    case 1:
                        teclado.nextLine();
                        sentenciaParametrizada.setObject(8, "ventas", Types.OTHER);
                        break;

                    case 2:
                        teclado.nextLine();
                        sentenciaParametrizada.setObject(8, "marketing", Types.OTHER);
                        break;

                    case 3:
                        teclado.nextLine();
                        sentenciaParametrizada.setObject(8, "rrhh", Types.OTHER);
                        break;
                    default:
                        System.err.println("OPCIÓN INCORRECTA");
                        break;
                }

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
                System.out.print("Introduce el DNI del empleado a borrar: ");
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
            try{
                PreparedStatement sentenciaParametrizada = null;
                String sql = null;
            System.out.println("MODIFICAR EMPLEADO");
            System.out.print("Introduzca el DNI del empleado a editar: ");
            String dni = teclado.nextLine();

            System.out.println("Elija el campo que desea editar:");
            System.out.println("1) Teléfono");
            System.out.println("2) Correo");
            switch (teclado.nextInt()) {
                case 1:
                    teclado.nextLine();
                    sql = "UPDATE empleado SET tlf=? WHERE DNI=?;";
                    sentenciaParametrizada = conexion.prepareStatement(sql);
                    System.out.println("Introduzca el teléfono nuevo: ");
                    sentenciaParametrizada.setString(1, teclado.nextLine());
                    break;

                case 2:
                    teclado.nextLine();
                    sql = "UPDATE empleado SET email=? WHERE DNI=?;";
                    sentenciaParametrizada = conexion.prepareStatement(sql);
                    System.out.println("Introduzca el correo nuevo: ");
                    sentenciaParametrizada.setString(1, teclado.nextLine());
                    break;
            
                default:
                    System.err.println("OPCION INCORRECTA");
                    break;
            }
            if(sentenciaParametrizada != null){
                sentenciaParametrizada.setString(2, dni);
                sentenciaParametrizada.executeUpdate();
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
