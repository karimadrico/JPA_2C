package es.ubu.lsi.model.multas;

import javax.persistence.*;

@Entity
@Table(name = "HISTORICOINCIDENCIA")
public class HistoricoIncidencia {
    @EmbeddedId
    private IncidenciaPK id;
    
    private String anotacion;
    
    @MapsId("nif")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NIF", insertable = false, updatable = false)
    private Conductor conductor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDTIPO", nullable = false)
    private TipoIncidencia tipoIncidencia;

    public HistoricoIncidencia() {
    }

    public HistoricoIncidencia(Incidencia incidencia) {
        this.id = new IncidenciaPK(incidencia.getId().getFecha(), incidencia.getId().getNif());
        this.anotacion = incidencia.getAnotacion();
        this.conductor = incidencia.getConductor();
        this.tipoIncidencia = incidencia.getTipoIncidencia();
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
        return "HistoricoIncidencia [id=" + id + ", anotacion=" + anotacion + "]";
    }
} 