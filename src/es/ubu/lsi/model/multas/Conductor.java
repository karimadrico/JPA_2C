package es.ubu.lsi.model.multas;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "CONDUCTOR")
public class Conductor {
    @Id
    @Column(name = "NIF", nullable = false)
    private String nif;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellido;
    
    @Column(nullable = false)
    private Integer puntos;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "direccion", column = @Column(name = "DIRECCION")),
        @AttributeOverride(name = "codigoPostal", column = @Column(name = "CP")),
        @AttributeOverride(name = "ciudad", column = @Column(name = "CIUDAD"))
    })
    private DireccionPostal direccionPostal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDAUTO")
    private Vehiculo vehiculo;
    
    @OneToMany(mappedBy = "conductor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Incidencia> incidencias = new HashSet<>();
    
    @OneToMany(mappedBy = "conductor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HistoricoIncidencia> historicos = new HashSet<>();

    public Conductor() {
        this.puntos = 12; // Inicializar con puntos máximos
        this.incidencias = new HashSet<>();
        this.historicos = new HashSet<>();
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public DireccionPostal getDireccionPostal() {
        return direccionPostal;
    }

    public void setDireccionPostal(DireccionPostal direccionPostal) {
        this.direccionPostal = direccionPostal;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
        if (vehiculo != null) {
            vehiculo.getConductores().add(this);
        }
    }

    public Set<Incidencia> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(Set<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    public Set<HistoricoIncidencia> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(Set<HistoricoIncidencia> historicos) {
        this.historicos = historicos;
    }

    @Override
    public String toString() {
        return "Conductor [nif=" + nif + ", nombre=" + nombre + ", apellido=" + apellido + 
               ", direccionPostal=" + direccionPostal + ", puntos=" + puntos + "]";
    }
} 