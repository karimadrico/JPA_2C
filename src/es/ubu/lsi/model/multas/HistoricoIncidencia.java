package es.ubu.lsi.model.multas;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "HISTORICOINCIDENCIA")
@Access(AccessType.FIELD)
public class HistoricoIncidencia implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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

    public HistoricoIncidencia() {
        System.out.println("Constructor HistoricoIncidencia() llamado");
    }

    public HistoricoIncidencia(Incidencia incidencia) {
        System.out.println("Constructor HistoricoIncidencia(Incidencia) llamado con: " + incidencia);
        this.id = new IncidenciaPK(incidencia.getId().getFecha(), incidencia.getId().getNif());
        this.anotacion = incidencia.getAnotacion();
        this.conductor = incidencia.getConductor();
        this.tipoIncidencia = incidencia.getTipoIncidencia();
        System.out.println("HistoricoIncidencia creado con ID: " + this.id + ", conductor: " + this.conductor.getNif());
    }

    public IncidenciaPK getId() {
        return id;
    }

    public void setId(IncidenciaPK id) {
        System.out.println("setId llamado con: " + id);
        this.id = id;
    }

    public String getAnotacion() {
        return anotacion;
    }

    public void setAnotacion(String anotacion) {
        System.out.println("setAnotacion llamado con: " + anotacion);
        this.anotacion = anotacion;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        System.out.println("setConductor llamado con: " + (conductor != null ? conductor.getNif() : "null"));
        this.conductor = conductor;
        if (id != null) {
            id.setNif(conductor.getNif());
        }
    }

    public TipoIncidencia getTipoIncidencia() {
        return tipoIncidencia;
    }

    public void setTipoIncidencia(TipoIncidencia tipoIncidencia) {
        System.out.println("setTipoIncidencia llamado con: " + (tipoIncidencia != null ? tipoIncidencia.getId() : "null"));
        this.tipoIncidencia = tipoIncidencia;
    }

    @Override
    public String toString() {
        return "HistoricoIncidencia [id=" + id + ", anotacion=" + anotacion + "]";
    }
} 