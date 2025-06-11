package es.ubu.lsi.model.multas;

import javax.persistence.Embeddable;

@Embeddable
public class DireccionPostal {
    private String direccion;
    private String codigoPostal;
    private String ciudad;

    public DireccionPostal() {
    }

    public DireccionPostal(String direccion, String codigoPostal, String ciudad) {
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return "DireccionPostal [direccion=" + direccion + ", codigoPostal=" + codigoPostal + ", ciudad=" + ciudad + "]";
    }
} 