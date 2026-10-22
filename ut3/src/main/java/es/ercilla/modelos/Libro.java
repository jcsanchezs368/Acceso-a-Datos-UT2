package es.ercilla.modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table (name="LIBRO",
        schema="LIBRERIA", // LIBRERIA.LIBRO
        check = { // Restricciones check de la tabla libro
            @CheckConstraint (
                name="chk_calif_media", //nombre restricción
                constraint = "calificacion_media BETWEEN 0 AND 10"
            )
        }) 
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estrategua de generación de ID
    private Integer id;
    // Aquí no hace falta poner el atributo name="" debido a que ambos nombres coinciden, tanto en java como en postgres
    @Column(nullable = false, length = 100) // Limitamos la longitud del String a 100 caracteres
    private String titulo;

    @Column(name="FECHA_PUBLICACION", nullable = false)
    private LocalDate fechaPublicacion;

    @Column(name="DISPONIBLE_PRESTAMO", nullable = false)
    private boolean disponiblePrestamo;
    // precision es el número de digitos que queremos que tenga el número //
    // scale es el número de decimales que puede tener nuestro número
    @Column(name="CALIFICACION_MEDIA", precision = 4, scale = 2) 
    private BigDecimal calificacionMedia;

    public Libro() {

    }

    public Libro(String titulo, LocalDate fechaPublicacion, boolean disponiblePrestamo,
            BigDecimal calificacionMedia) {
        this.titulo = titulo;
        this.fechaPublicacion = fechaPublicacion;
        this.disponiblePrestamo = disponiblePrestamo;
        this.calificacionMedia = calificacionMedia;
    }


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }
    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
    public boolean isDisponiblePrestamo() {
        return disponiblePrestamo;
    }
    public void setDisponiblePrestamo(boolean disponiblePrestamo) {
        this.disponiblePrestamo = disponiblePrestamo;
    }
    public BigDecimal getCalificacionMedia() {
        return calificacionMedia;
    }
    public void setCalificacionMedia(BigDecimal calificacionMedia) {
        this.calificacionMedia = calificacionMedia;
    }

    @Override
    public String toString() {
        return "Libro [id=" + id + ", titulo=" + titulo + ", fechaPublicacion=" + fechaPublicacion
                + ", disponiblePrestamo=" + disponiblePrestamo + ", calificacionMedia=" + calificacionMedia + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
        result = prime * result + ((fechaPublicacion == null) ? 0 : fechaPublicacion.hashCode());
        result = prime * result + (disponiblePrestamo ? 1231 : 1237);
        result = prime * result + ((calificacionMedia == null) ? 0 : calificacionMedia.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Libro other = (Libro) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (titulo == null) {
            if (other.titulo != null)
                return false;
        } else if (!titulo.equals(other.titulo))
            return false;
        if (fechaPublicacion == null) {
            if (other.fechaPublicacion != null)
                return false;
        } else if (!fechaPublicacion.equals(other.fechaPublicacion))
            return false;
        if (disponiblePrestamo != other.disponiblePrestamo)
            return false;
        if (calificacionMedia == null) {
            if (other.calificacionMedia != null)
                return false;
        } else if (!calificacionMedia.equals(other.calificacionMedia))
            return false;
        return true;
    }



}
