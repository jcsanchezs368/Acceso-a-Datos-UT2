package ejercicios.ejercicio2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;

public class Liga {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        
        try(Connection conexion = DriverManager.getConnection("jdbc:postgresql://192.168.29.12:10363/ad", "dam86", "20999936Q")) {
            boolean salir = false;
            do{
                System.out.println("0. Mostrar la clasificación");
                System.out.println("1. Importar partidos desde un fichero CSV");
                System.out.println("2. Salir");
                System.out.print("Selecciona una opción: ");
                int opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 0:
                        mostrarClasificacion(conexion);
                        break;
                    case 1:
                        System.out.print("Introduzca la ruta del fichero CSV a leer: ");
                        Path ruta = Path.of(teclado.nextLine());

                        if(Files.isRegularFile(ruta)){
                            importarPartidos(conexion, ruta);
                        }else{
                            System.err.println("No se ha encontrado la ruta al fichero.");
                        }
                        break;
                    case 2:
                        salir = true;
                        conexion.close();
                        break;                               
                    default:
                        System.err.println("Opción incorrecta.");
                        break;
                }
            }while (!salir);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        teclado.close();
    }

    private static void mostrarClasificacion(Connection conexion){
        String query = """
            SELECT
                e.nombre,
                e.puntos,
                COUNT(p.id) AS num_partidos
            FROM liga.equipo e
            LEFT JOIN liga.partido p
                ON e.id = p.id_eq_local
                OR e.id = p.id_eq_visitante
            GROUP BY e.nombre, e.puntos
            ORDER BY e.puntos desc;
        """;
        try (ResultSet rs = conexion.createStatement().executeQuery(query)) {
            int ranking = 1;
            while (rs.next()) {
                System.out.println("POSICIÓN EN LA CLASIFICACIÓN: " + ranking 
                    + " \n\tNOMBRE EQUIPO: " + rs.getString("nombre") 
                    + " \n\tPUNTUACIÓN: " + rs.getInt("puntos") 
                    + " \n\tPARTIDOS JUGADOS: " + rs.getInt("num_partidos")
                 );
                 ranking++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void importarPartidos(Connection conexion, Path ruta){
        try(BufferedReader br = new BufferedReader(new FileReader(ruta.toFile()))){
            br.readLine();
            String linea = br.readLine();
            while (linea != null) {
                String datos[] = linea.split(";");
                if(!existePartido(conexion, Integer.parseInt(datos[0]))){
                    int id_local = getIdEquipoNombre(conexion, datos[1]);
                    int id_visitante = getIdEquipoNombre(conexion, datos[2]);

                    if(id_local != 0 && id_visitante != 0){
                        try (PreparedStatement sentenciaP = conexion.prepareStatement("INSERT INTO LIGA.PARTIDO VALUES(?,?,?,?,?,?);")) {
                            sentenciaP.setInt(1, Integer.parseInt(datos[0]));
                            sentenciaP.setInt(2, id_local);
                            sentenciaP.setInt(3, id_visitante);
                            sentenciaP.setTimestamp(4, Timestamp.valueOf(datos[3]));
                            sentenciaP.setInt(5, Integer.parseInt(datos[4]));
                            sentenciaP.setString(6, datos[5]);
                            sentenciaP.executeUpdate();

                            System.out.println("Partido con ID: " + Integer.parseInt(datos[0]) + " insertado.");
                            String resultado[] = datos[5].split("-");
                            int golLocal = Integer.parseInt(resultado[0]);
                            int golVisitante = Integer.parseInt(resultado[1]);
                            if(golLocal == golVisitante){
                                actualizarPuntuacion(conexion, id_local, 1);
                                actualizarPuntuacion(conexion, id_visitante, 1);
                            }else if(golLocal > golVisitante){
                                actualizarPuntuacion(conexion, id_local, 3);
                            }else{
                                actualizarPuntuacion(conexion, id_visitante, 3);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    System.out.println("Partido con ID: " + Integer.parseInt(datos[0]) + " ya existe. Omitiendo...");
                }
                linea = br.readLine();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void actualizarPuntuacion(Connection conexion, int idEquipo, int puntos){
        try (PreparedStatement sentenciaP = conexion.prepareStatement("UPDATE LIGA.EQUIPO SET PUNTOS = PUNTOS + ? WHERE ID = ?");){
            sentenciaP.setInt(1, puntos);
            sentenciaP.setInt(2, idEquipo);
            sentenciaP.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static int getIdEquipoNombre(Connection conexion, String nombre){
        try (PreparedStatement sentenciaP = conexion.prepareStatement("SELECT ID FROM LIGA.EQUIPO WHERE NOMBRE = ?")){
            sentenciaP.setString(1, nombre);

            try(ResultSet rs = sentenciaP.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static boolean existePartido(Connection conexion, int idPartido){
        try (PreparedStatement sentenciaP = conexion.prepareStatement(("SELECT ID FROM LIGA.PARTIDO WHERE ID = ?"))){
            sentenciaP.setInt(1, idPartido);
            try (ResultSet rs = sentenciaP.executeQuery()){
                if(rs.next()){
                    return true;
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
