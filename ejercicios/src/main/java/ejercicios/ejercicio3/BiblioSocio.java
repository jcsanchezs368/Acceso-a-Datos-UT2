package ejercicios.ejercicio3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BiblioSocio {
    public static void main(String[] args) {
        try(Connection conexion = DriverManager.getConnection("jdbc:postgresql://192.168.29.12:10363/ad", "dam86", "20999936Q")){
            BiblioOps.crearTablas(conexion);
            Scanner teclado = new Scanner(System.in);
            String email = "";
            String contrasenya = "";
            String isbn = "";
            do{
                System.out.println("\n============ LOGIN ==============");
                System.out.print("EMAIL: ");
                email = teclado.nextLine();
                System.out.print("CONTRASEÑA: ");
                contrasenya = teclado.nextLine();
            }while(!login(conexion, email, contrasenya));

            System.out.println("BIENVENIDO " + email);

            boolean salir = false;
            while (!salir) {
                System.out.println("\n========== BIBLIO-SOCIO ==========");
                System.out.println("1. Solicitar el préstamo de un libro");
                System.out.println("2. Renovar un préstamo");
                System.out.println("0. Salir");
                int opcion = teclado.nextInt();
                teclado.nextLine();
                switch (opcion) {
                    case 1:
                        System.out.print("ISBN: ");
                        isbn = teclado.nextLine();
                        if(isbn.trim().length() != 13){
                            System.err.println("FORMATO ISBN INCORRECTO");
                            break;
                        }
                        solicitarPrestamo(conexion, email, isbn);
                        break;
                    case 2:
                        renovarPrestamo(conexion, teclado, email);
                        break;
                    case 0:
                        salir = true;
                        conexion.close();
                        break;
                
                    default:
                        break;
                }
            }
            teclado.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean login(Connection conexion, String email, String contrasenya){
        String sql = "SELECT CONTRASENYA FROM BIBLIOTECA.SOCIO WHERE EMAIL = ? AND ACTIVO=TRUE;";
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setString(1, email);
            try(ResultSet rs = sentenciaP.executeQuery()){
                if (rs.next()) {
                    if (contrasenya.equals(rs.getString("CONTRASENYA"))){
                        return true;
                    }else{
                        System.err.println("CONTRASEÑA INCORRECTA");
                        return false;
                    }
                }else{
                    System.err.println("EMAIL NO REGISTRADO");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean solicitarPrestamo(Connection conexion, String email, String isbn){
        Integer idCopia = 0;
        String sql = "SELECT MIN(ID) FROM BIBLIOTECA.COPIA WHERE ISBN = ? AND DISPONIBLE = TRUE;";
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setString(1, isbn);
            try (ResultSet rs = sentenciaP.executeQuery()) {
                if(rs.next()){
                    idCopia = rs.getInt(1);
                    if(idCopia != null && idCopia != 0){
                        BiblioOps.insertarPrestamo(conexion, idCopia, email);
                        BiblioOps.prestarCopia(conexion, idCopia);
                        BiblioOps.cambiarNumCopiasDisponibles(conexion, isbn, -1);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.err.println("NO HAY COPIAS DISPONIBLES");
        return false;
    }

    private static boolean renovarPrestamo(Connection conexion, Scanner teclado, String email){
        String sql = """
            SELECT P.ID, L.TITULO, P.F_PRESTAMO, P.F_LIMITE, P.NUM_RENOVACIONES_PERMITIDAS FROM BIBLIOTECA.PRESTAMO P
            JOIN BIBLIOTECA.COPIA C ON C.ID = P.COPIA_ID
            JOIN BIBLIOTECA.LIBRO L ON L.ISBN = C.ISBN
            WHERE P.SOCIO_EMAIL = ? AND P.F_DEVOLUCION IS NULL;
        """;
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setString(1, email);
            try(ResultSet rs = sentenciaP.executeQuery()){
                System.out.println("\n========== PRÉSTAMOS ACTIVOS ==========");
                boolean hayPrestamos = false;
                while(rs.next()){
                    hayPrestamos = true;
                    System.out.println("PRÉSTAMO ID: " + rs.getInt(1));
                    System.out.println("TÍTULO LIBRO: " + rs.getString(2));
                    System.out.println("FECHA PRÉSTAMO : " + rs.getDate(3));
                    System.out.println("FECHA LÍMITE : " + rs.getDate(4 ));
                }
                if(!hayPrestamos){
                    System.err.println("NO HAY PRÉSTAMOS ACTIVOS PARA RENOVAR");
                    return false;
                }
                System.out.print("INTRODUZCA LA ID DEL PRÉSTAMO A RENOVAR: ");
                int idPrestamoRenovar = teclado.nextInt();
                teclado.nextLine();
                if(puedeRenovarPrestamo(conexion, idPrestamoRenovar, email)){
                    return actualizarFechaPrestamo(conexion, idPrestamoRenovar, email);
                }else{
                    System.err.println("NO ES POSIBLE RENOVAR EL PRÉSTAMO MÁS");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean actualizarFechaPrestamo(Connection conexion, int idPrestamo, String email){
        String sql = "UPDATE BIBLIOTECA.PRESTAMO SET F_LIMITE = F_LIMITE + 10, NUM_RENOVACIONES_PERMITIDAS = NUM_RENOVACIONES_PERMITIDAS -1 WHERE ID = ? AND SOCIO_EMAIL = ?;";
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)){
            sentenciaP.setInt(1, idPrestamo);
            sentenciaP.setString(2, email);
            sentenciaP.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("NO SE HA ENCONTRADO EL PRÉSTAMO A RENOVAR O ID DE PRÉSTAMO INCORRECTA");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean puedeRenovarPrestamo(Connection conexion, int idPrestamo, String email){
        String sql = "SELECT NUM_RENOVACIONES_PERMITIDAS FROM BIBLIOTECA.PRESTAMO WHERE ID = ? AND SOCIO_EMAIL=?;";
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setInt(1, idPrestamo);
            sentenciaP.setString(2, email);
            try(ResultSet rs = sentenciaP.executeQuery()){
                if(rs.next() && rs.getInt(1) > 0){
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
