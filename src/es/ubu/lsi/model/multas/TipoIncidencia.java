package es.ubu.lsi.model.multas;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "TIPOINCIDENCIA")
public class TipoIncidencia {
    @Id
    @Column(name = "ID", nullable = false)
    private Long id;
    
    @Column(nullable = false)
    private String descripcion;
    
    @Column(nullable = false)
    private Integer valor;
    
    @OneToMany(mappedBy = "tipoIncidencia", cascade = CascadeType.ALL)
    private Set<Incidencia> incidencias = new HashSet<>();
    
    @OneToMany(mappedBy = "tipoIncidencia", cascade = CascadeType.ALL)
    private Set<HistoricoIncidencia> historicos = new HashSet<>();

    public TipoIncidencia() {
        this.incidencias = new HashSet<>();
        this.historicos = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
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
        return "TipoIncidencia [id=" + id + ", descripcion=" + descripcion + ", valor=" + valor + "]";
    }
} 