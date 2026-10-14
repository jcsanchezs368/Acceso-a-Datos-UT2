package es.ercilla.clases;

public class Equipo {
    private int id;
    private String nombre;
    private int puntos;
    private String localidad;


    public Equipo(){
        super();
    }
    
    public Equipo(int id, String nombre, int puntos, String localidad) {
        this.id = id;
        this.nombre = nombre;
        this.puntos = puntos;
        this.localidad = localidad;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public int getPuntos() {
        return puntos;
    }
    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
    public String getLocalidad() {
        return localidad;
    }
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    @Override
    public String toString() {
        return "Equipo [id=" + id + ", nombre=" + nombre + ", puntos=" + puntos + ", localidad=" + localidad + "]";
    }

    


}
