package es.ercilla.ejercicios;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import es.ercilla.clases.Equipo;

/**
 * 
 * Ejercicio2
 */
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
                        List<Equipo> equipos = mostrarEquipos(conexion);
                        for (Equipo equipo : equipos) {
                            System.out.println(equipo);
                        }
                        break;
                    case 1:
                        importarPartidos(conexion);

                        break;
                    case 2:
                        salir = true;
                        break;                               
                    default:
                        break;
                }
            }while (!salir);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        teclado.close();
        
    }

    private static List<Equipo> mostrarEquipos(Connection conexion){
        List<Equipo> equipos = new ArrayList<Equipo>();

        try {
            ResultSet rs = conexion.createStatement().executeQuery("SELECT * FROM EQUIPO ORDER BY CAST(PUNTOS AS INT) ASC;");

            while(rs.next()){
                equipos.add(new Equipo(
                    rs.getInt("ID_EQUIPO"),
                    rs.getString("NOMBRE"),
                    rs.getInt("PUNTOS"),
                    rs.getString("LOCALIDAD")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return equipos;
    }

    private static void importarPartidos(Connection conexion){
        try(BufferedReader br = new BufferedReader(new FileReader("jornada-1.csv"))){
            br.readLine();
            String linea = br.readLine();
            int maxId = getMaxId(conexion);
            while (linea != null) {
                String datos[] = linea.split(";");
                if(Integer.parseInt(datos[0]) > maxId){
                    int id_local = getIdEquipoNombre(conexion, datos[1]);
                    int id_visitante = getIdEquipoNombre(conexion, datos[2]);

                    if(id_local != 0 && id_visitante != 0){
                        try {
                            PreparedStatement sentenciaP = conexion.prepareStatement("INSERT INTO PARTIDO VALUES(?,?,?,?,?,?);");
                            sentenciaP.setInt(1, Integer.parseInt(datos[0]));
                            sentenciaP.setInt(2, id_local);
                            sentenciaP.setInt(3, id_visitante);
                            sentenciaP.setTimestamp(4, Timestamp.valueOf(datos[3]));
                            sentenciaP.setInt(5, Integer.parseInt(datos[4]));
                            sentenciaP.setString(6, datos[5]);
                            sentenciaP.executeUpdate();

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
                    
                }
                linea = br.readLine();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void actualizarPuntuacion(Connection conexion, int idEquipo, int puntos){
        try {
            PreparedStatement sentenciaP = conexion.prepareStatement("UPDATE EQUIPO SET PUNTOS = PUNTOS + ? WHERE ID_EQUIPO = ?");
            sentenciaP.setInt(1, puntos);
            sentenciaP.setInt(2, idEquipo);
            sentenciaP.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static int getIdEquipoNombre(Connection conexion, String nombre){
        try {
            PreparedStatement sentenciaP = conexion.prepareStatement("SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = ?");
            sentenciaP.setString(1, nombre);
            ResultSet rs = sentenciaP.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int getMaxId(Connection conexion){
        try {
            ResultSet rs = conexion.createStatement().executeQuery("SELECT MAX(ID_PARTIDO) FROM PARTIDO;");
            if(rs.next()){
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 0;
    }
}
