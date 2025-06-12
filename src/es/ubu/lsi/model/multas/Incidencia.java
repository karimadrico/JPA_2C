package es.ubu.lsi.model.multas;

import javax.persistence.*;

@Entity
@Table(name = "INCIDENCIA")
public class Incidencia {
    @EmbeddedId
    private IncidenciaPK id;
    
    @Lob
    @Column(name = "ANOTACION")
    private String anotacion;
    
    @MapsId("nif")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NIF", insertable = false, updatable = false)
    private Conductor conductor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDTIPO", nullable = false)
    private TipoIncidencia tipoIncidencia;

    public Incidencia() {
    }

    public IncidenciaPK getId() {
        return id;
    }

    public void setId(IncidenciaPK id) {
        this.id = id;
    }

    public String getAnotacion() {
        return anotacion;
    }

    public void setAnotacion(String anotacion) {
        this.anotacion = anotacion;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
        if (id != null) {
            id.setNif(conductor.getNif());
        }
    }

    public TipoIncidencia getTipoIncidencia() {
        return tipoIncidencia;
    }

    public void setTipoIncidencia(TipoIncidencia tipoIncidencia) {
        this.tipoIncidencia = tipoIncidencia;
    }

    @Override
    public String toString() {
        return "Incidencia [id=" + id + ", anotacion=" + anotacion + "]";
    }
} 