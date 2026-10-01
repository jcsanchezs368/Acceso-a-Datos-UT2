package es.ercilla.clases;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;

public class Producto {
    private UUID id;
    private String nombre;
    private BigDecimal precio;
    private int stock;
    private Integer garantiaMeses; //uso el Integer para que pueda ser null, ya que en la BBDD es opcional
    private boolean disponible;
    private Date fechaAlta;
    
    public Producto(){
        super();
    };

    public Producto(UUID id, String nombre, BigDecimal precio, int stock, Integer garantiaMeses, boolean disponible,
            Date fechaAlta) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.garantiaMeses = garantiaMeses;
        this.disponible = disponible;
        this.fechaAlta = fechaAlta;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public Integer getGarantiaMeses() {
        return garantiaMeses;
    }
    public void setGarantiaMeses(Integer garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
    }
    public boolean isDisponible() {
        return disponible;
    }
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    public Date getFechaAlta() {
        return fechaAlta;
    }
    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @Override
    public String toString() {
        return "Producto [id=" + id + ", nombre=" + nombre + ", precio=" + precio + ", stock=" + stock
                + ", garantiaMeses=" + garantiaMeses + ", disponible=" + disponible + ", fechaAlta=" + fechaAlta + "]";
    }

    


    
}
