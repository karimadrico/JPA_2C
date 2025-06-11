package es.ubu.lsi.model.multas;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class IncidenciaPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA", nullable = false)
    private Date fecha;

    @Column(name = "NIF", nullable = false)
    private String nif;

    public IncidenciaPK() {
    }

    public IncidenciaPK(Date fecha, String nif) {
        this.fecha = fecha;
        this.nif = nif;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fecha == null) ? 0 : fecha.hashCode());
        result = prime * result + ((nif == null) ? 0 : nif.hashCode());
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
        IncidenciaPK other = (IncidenciaPK) obj;
        if (fecha == null) {
            if (other.fecha != null)
                return false;
        } else if (!fecha.equals(other.fecha))
            return false;
        if (nif == null) {
            if (other.nif != null)
                return false;
        } else if (!nif.equals(other.nif))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IncidenciaPK [fecha=" + fecha + ", nif=" + nif + "]";
    }
} 