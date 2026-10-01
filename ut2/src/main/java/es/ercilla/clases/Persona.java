package es.ercilla.clases;

import java.math.BigDecimal;
import java.sql.Date;

public class Persona {
    private Integer idPersona;
    private String nombre;
    private BigDecimal ingresoAnual;
    private Date fecha_nacimiento;
    private String telefono;
    private boolean tieneCarnet;
    private Float altura;
    
    public Persona(){
        super();
    }

    

    public Persona(Integer idPersona, String nombre, BigDecimal ingresoAnual, Date fecha_nacimiento, String telefono,
            boolean tieneCarnet, Float altura) {
        this.idPersona = idPersona;
        this.nombre = nombre;
        this.ingresoAnual = ingresoAnual;
        this.fecha_nacimiento = fecha_nacimiento;
        this.telefono = telefono;
        this.tieneCarnet = tieneCarnet;
        this.altura = altura;
    }

        public Persona(String nombre, BigDecimal ingresoAnual, Date fecha_nacimiento, String telefono,
            boolean tieneCarnet, Float altura) {
        this.nombre = nombre;
        this.ingresoAnual = ingresoAnual;
        this.fecha_nacimiento = fecha_nacimiento;
        this.telefono = telefono;
        this.tieneCarnet = tieneCarnet;
        this.altura = altura;
    }


    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getIngresoAnual() {
        return ingresoAnual;
    }

    public void setIngresoAnual(BigDecimal ingresoAnual) {
        this.ingresoAnual = ingresoAnual;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isTieneCarnet() {
        return tieneCarnet;
    }

    public void setTieneCarnet(boolean tieneCarnet) {
        this.tieneCarnet = tieneCarnet;
    }

    public Float getAltura() {
        return altura;
    }

    public void setAltura(Float altura) {
        this.altura = altura;
    }



    @Override
    public String toString() {
        return "Persona [idPersona=" + idPersona + ", nombre=" + nombre + ", ingresoAnual=" + ingresoAnual
                + ", fecha_nacimiento=" + fecha_nacimiento + ", telefono=" + telefono + ", tieneCarnet=" + tieneCarnet
                + ", altura=" + altura + "]";
    }

    


}
