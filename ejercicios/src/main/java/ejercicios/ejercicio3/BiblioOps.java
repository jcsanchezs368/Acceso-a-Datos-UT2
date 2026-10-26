package ejercicios.ejercicio3;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

public class BiblioOps {
    public static void main(String[] args) {
        try(Connection conexion = DriverManager.getConnection("jdbc:postgresql://192.168.29.12:10363/ad", "dam86", "20999936Q")){
            Scanner teclado = new Scanner(System.in);
            crearTablas(conexion);
            boolean salir = false;
            while (!salir) {
                System.out.println("\n========== BIBLIO-OPS ==========");
                System.out.println("1. Registrar un nuevo libro");
                System.out.println("2. Registrar una nueva copia");
                System.out.println("3. Registrar un nuevo socio");
                System.out.println("4. Prestar una copia a un socio");
                System.out.println("0. Salir");
                System.out.print("ELIGE UNA OPCIÓN: ");
                int opcion = teclado.nextInt();
                teclado.nextLine();
                switch (opcion) {
                    case 1:
                        registrarLibro(conexion, teclado);
                        break;
                    case 2:
                        registrarCopia(conexion, teclado);
                        break;
                    case 3:
                        registrarSocio(conexion, teclado);
                        break;
                    case 4:
                        prestarCopiaSocio(conexion, teclado);
                        break;
                    case 0:
                        salir = true;
                        conexion.close();
                        break;
                    default:
                        System.err.println("OPCIÓN INCORRECTA");
                        break;
                }
            }
            teclado.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }


    private static void registrarLibro(Connection conexion, Scanner teclado){
        String isbn = "";
        String titulo = "";
        String autor = "";
        Date fechaPublicacion = null;
        int numPaginas = 0;

        System.out.print("ISBN: ");
        isbn = teclado.nextLine();
        if(isbn.trim().length() != 13){
            System.err.println("FORMATO ISBN INCORRECTO");
            return;
        }
        if(existeIsbn(conexion, isbn)){
            System.err.println("ESE LIBRO YA ESTÁ REGISTRADO");
            return;
        }
        System.out.print("Título: ");
        titulo = teclado.nextLine();
        if(titulo.isBlank()){
            System.err.println("EL TÍTULO ES OBLIGATORIO");
            return;
        }
        System.out.print("Autor: ");
        autor = teclado.nextLine();
        if (autor.isBlank()) {
            System.err.println("EL AUTOR ES OBLIGATORIO");
        }
        System.out.print("Fecha de publicación [yyyy-mm-dd]: (OPCIONAL): ");
        String fecha = teclado.nextLine();
        if(fecha != null && !fecha.isBlank()){
            try {
                fechaPublicacion = Date.valueOf(fecha);
            } catch (IllegalArgumentException e){
                System.err.println("FORMATO DE FECHA INCORRECTO");
                return;
            }
        }

        System.out.print("Número de páginas: ");
        numPaginas = teclado.nextInt();
        teclado.nextLine();
        if(numPaginas <= 0){
            System.err.println("NÚMERO DE PÁGINAS NO PUEDE SER NEGATIVO O CERO");
            return;
        }
        String sql = "INSERT INTO BIBLIOTECA.LIBRO(ISBN, TITULO, AUTOR, F_PUBLICACION, NUM_PAGS, NUM_COPIAS_DISPONIBLES) VALUES(?,?,?,?,?,?);";

        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setString(1, isbn);
            sentenciaP.setString(2, titulo);
            sentenciaP.setString(3, autor);
            if(fechaPublicacion != null){
                sentenciaP.setDate(4, fechaPublicacion);
            }else{
                sentenciaP.setNull(4, Types.DATE);
            }

            sentenciaP.setInt(5, numPaginas);
            sentenciaP.setInt(6, 0);

            sentenciaP.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void registrarCopia(Connection conexion, Scanner teclado){
        String isbn = "";
        System.out.print("ISBN: ");
        isbn = teclado.nextLine();
        if(isbn.trim().length() != 13){
            System.err.println("FORMATO ISBN INCORRECTO");
            return;
        }
        if(!existeIsbn(conexion, isbn)){
            System.err.println("NO EXISTE EL LIBRO");
            return;
        }

        String sql = "INSERT INTO BIBLIOTECA.COPIA(ISBN, DISPONIBLE) VALUES(?,?);";
        try(PreparedStatement sentenciaP = conexion.prepareStatement(sql)){
            sentenciaP.setString(1, isbn);
            sentenciaP.setBoolean(2, true);
            sentenciaP.executeUpdate();
            cambiarNumCopiasDisponibles(conexion, isbn, 1);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void registrarSocio(Connection conexion, Scanner teclado){
        String email = "";
        String nombre = "";
        String contrasenya = "";
        String tlf = "";
        Date fechaAlta = Date.valueOf(LocalDate.now());
        System.out.print("EMAIL: ");
        email = teclado.nextLine();
        if(email.isBlank()){
            System.err.println("EMAIL OBLIGATORIO");
            return;
        }
        if(existeEmail(conexion, email)){
            System.err.println("ESE SOCIO YA ESTÁ REGISTRADO");
            return;
        }
        System.out.print("NOMBRE: ");
        nombre = teclado.nextLine();
        if(nombre.isBlank()){
            System.err.println("NOMBRE OBLIGATORIO");
            return;
        }
        System.out.print("CONTRASEÑA: ");
        contrasenya = teclado.nextLine();
        if(contrasenya.isBlank()){
            System.err.println("CONTRASEÑA OBLIGATORIA");
            return;
        }
        System.out.print("TELÉFONO (OPCIONAL): ");
        tlf = teclado.nextLine();

        String sql = "INSERT INTO BIBLIOTECA.SOCIO(EMAIL, NOMBRE, CONTRASENYA, TLF, F_ALTA, ACTIVO) VALUES(?,?,?,?,?,?);";

        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setString(1, email);
            sentenciaP.setString(2, nombre);
            sentenciaP.setString(3, contrasenya);
            if(tlf.isBlank()){
                sentenciaP.setNull(4, Types.VARCHAR);
            }else{
                sentenciaP.setString(4, tlf);
            }
            sentenciaP.setDate(5, fechaAlta);
            sentenciaP.setBoolean(6, true);
            sentenciaP.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean existeIsbn(Connection conexion, String isbn){
        String sql = "SELECT ISBN FROM BIBLIOTECA.LIBRO WHERE ISBN = ?;";
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setString(1, isbn);
            try(ResultSet rs = sentenciaP.executeQuery()){
                if(rs.next()){
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean existeEmail(Connection conexion, String email){
        String sql = "SELECT EMAIL FROM BIBLIOTECA.SOCIO WHERE EMAIL = ?;";
        try(PreparedStatement sentenciaP = conexion.prepareStatement(sql)){
            sentenciaP.setString(1, email);
            try(ResultSet rs = sentenciaP.executeQuery()) {
                if(rs.next()){
                    return true;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private static boolean existeCopia(Connection conexion, int idCopia){
        String sql = "SELECT id FROM BIBLIOTECA.COPIA WHERE ID = ?;";
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setInt(1, idCopia);
            try (ResultSet rs = sentenciaP.executeQuery()) {
                if(rs.next()){
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void prestarCopiaSocio(Connection conexion, Scanner teclado){
        int idCopia = 0;
        System.out.print("ID COPIA: ");
        idCopia = teclado.nextInt();
        teclado.nextLine();
        if(idCopia <= 0){
            System.err.println("ID DE COPIA NO PUEDE SER NEGATIVO");
        }
        if(!existeCopia(conexion, idCopia)){
            System.err.println("NO EXISTE LA COPIA");
        }
        String email = "";
        System.out.print("EMAIL SOCIO: ");
        email = teclado.nextLine();
        if(email.isBlank()){
            System.err.println("EMAIL OBLIGATORIO");
            return;
        }
        if(!existeEmail(conexion, email)){
            System.err.println("NO EXISTE EL SOCIO");
            return;
        }
        Boolean estadoCopia = disponibleCopia(conexion, idCopia);
        if(estadoCopia != null && estadoCopia == true ){
            insertarPrestamo(conexion, idCopia, email);
        }else{
            System.err.println("LA COPIA NO ESTÁ DISPONIBLE");
        }
    }

    public static void insertarPrestamo(Connection conexion, int idCopia, String email){
        String sql = "INSERT INTO BIBLIOTECA.PRESTAMO(COPIA_ID, SOCIO_EMAIL, F_PRESTAMO, F_LIMITE, F_DEVOLUCION, NUM_RENOVACIONES_PERMITIDAS) VALUES(?,?,?,?,?,?);";
            try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
                sentenciaP.setInt(1, idCopia);
                sentenciaP.setString(2, email);
                sentenciaP.setDate(3, Date.valueOf(LocalDate.now()));
                sentenciaP.setDate(4, Date.valueOf(LocalDate.now().plusDays(10)));
                sentenciaP.setNull(5, Types.DATE);
                sentenciaP.setInt(6, new Random().nextInt(4) + 1);
                sentenciaP.executeUpdate();
                prestarCopia(conexion, idCopia);
                String isbn = IsbnCopia(conexion, idCopia);
                if(isbn != null){
                    cambiarNumCopiasDisponibles(conexion, isbn, -1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    private static Boolean disponibleCopia(Connection conexion, int idCopia){
        String sql = "SELECT DISPONIBLE FROM BIBLIOTECA.COPIA WHERE ID = ?;";
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql) ) {
            sentenciaP.setInt(1, idCopia);
            try(ResultSet rs = sentenciaP.executeQuery()){
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String IsbnCopia(Connection conexion, int idCopia){
        String sql = "SELECT ISBN FROM BIBLIOTECA.COPIA WHERE ID = ?;";
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setInt(1, idCopia);
            try (ResultSet rs = sentenciaP.executeQuery()) {
                if(rs.next()){
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void cambiarNumCopiasDisponibles(Connection conexion, String isbn, int num){
        String sql = "UPDATE BIBLIOTECA.LIBRO SET NUM_COPIAS_DISPONIBLES = NUM_COPIAS_DISPONIBLES + ? WHERE ISBN = ?;";
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setInt(1, num);
            sentenciaP.setString(2, isbn);
            sentenciaP.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean prestarCopia(Connection conexion, int idCopia){
        String sql = "UPDATE BIBLIOTECA.COPIA SET DISPONIBLE = FALSE WHERE ID = ?;";
        try (PreparedStatement sentenciaP = conexion.prepareStatement(sql)) {
            sentenciaP.setInt(1, idCopia);
            sentenciaP.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
            return false;
    }

    public static void crearTablas(Connection conexion){
        String sql = """
            CREATE SCHEMA IF NOT EXISTS biblioteca;

            CREATE TABLE IF NOT EXISTS biblioteca.libro (
            isbn CHAR(13) CONSTRAINT pk_libro PRIMARY KEY,
            titulo VARCHAR(50) NOT NULL,
            autor VARCHAR(50) NOT NULL,
            f_publicacion DATE,
            num_pags INTEGER NOT NULL,
            num_copias_disponibles INTEGER NOT NULL DEFAULT 0
            );

            CREATE TABLE IF NOT EXISTS biblioteca.copia (
            id SERIAL CONSTRAINT pk_copia PRIMARY KEY,
            isbn CHAR(13) NOT NULL CONSTRAINT fk_copia_libro REFERENCES biblioteca.libro(isbn),
            disponible BOOLEAN NOT NULL DEFAULT TRUE
            );

            CREATE TABLE IF NOT EXISTS biblioteca.socio (
            email VARCHAR(50) CONSTRAINT pk_socio PRIMARY KEY,
            nombre VARCHAR(100) NOT NULL,
            contrasenya VARCHAR(50) NOT NULL,
            tlf VARCHAR(20),
            f_alta DATE NOT NULL DEFAULT CURRENT_DATE,
            activo BOOLEAN NOT NULL DEFAULT TRUE
            );

            CREATE TABLE IF NOT EXISTS biblioteca.prestamo (
            id SERIAL CONSTRAINT pk_prestamo PRIMARY KEY,
            copia_id INTEGER NOT NULL CONSTRAINT fk_prestamo_copia REFERENCES biblioteca.copia(id),
            socio_email VARCHAR(50) NOT NULL CONSTRAINT fk_prestamo_socio REFERENCES biblioteca.socio(email),
            f_prestamo DATE NOT NULL,
            f_limite DATE NOT NULL,
            f_devolucion DATE,
            num_renovaciones_permitidas INTEGER NOT NULL DEFAULT 2,
            CONSTRAINT ck_fechas_prestamo CHECK (f_limite > f_prestamo)
            );
        """;

        try(Statement sentencia = conexion.createStatement()){
            sentencia.executeUpdate(sql);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
