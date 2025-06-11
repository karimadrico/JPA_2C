package es.ubu.lsi.model.multas;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "VEHICULO")
public class Vehiculo {
    @Id
    @Column(name = "IDAUTO", nullable = false)
    private String idauto;
    
    @Column(nullable = false)
    private String nombre;
    
    @Embedded
    private DireccionPostal direccionPostal;
    
    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Conductor> conductores = new HashSet<>();

    public Vehiculo() {
        this.conductores = new HashSet<>();
    }

    public String getIdauto() {
        return idauto;
    }

    public void setIdauto(String idauto) {
        this.idauto = idauto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public DireccionPostal getDireccionPostal() {
        return direccionPostal;
    }

    public void setDireccionPostal(DireccionPostal direccionPostal) {
        this.direccionPostal = direccionPostal;
    }

    public Set<Conductor> getConductores() {
        return conductores;
    }

    public void setConductores(Set<Conductor> conductores) {
        this.conductores = conductores;
    }

    public void addConductor(Conductor conductor) {
        conductores.add(conductor);
        conductor.setVehiculo(this);
    }

    public void removeConductor(Conductor conductor) {
        conductores.remove(conductor);
        conductor.setVehiculo(null);
    }

    @Override
    public String toString() {
        return "Vehiculo [idauto=" + idauto + ", nombre=" + nombre + ", direccionPostal=" + direccionPostal + "]";
    }
} 