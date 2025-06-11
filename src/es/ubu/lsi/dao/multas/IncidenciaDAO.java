package es.ubu.lsi.dao.multas;

import java.util.Date;
import java.util.List;
import javax.persistence.TypedQuery;
import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.multas.Incidencia;
import es.ubu.lsi.model.multas.IncidenciaPK;

public class IncidenciaDAO extends JpaDAO<Incidencia, IncidenciaPK> {

    public IncidenciaDAO() {
        super();
    }

    @Override
    public List<Incidencia> findAll() {
        return getEntityManager().createQuery("SELECT i FROM Incidencia i", Incidencia.class).getResultList();
    }
    
    public List<Incidencia> findByNif(String nif) {
        TypedQuery<Incidencia> query = getEntityManager().createQuery(
            "SELECT i FROM Incidencia i WHERE i.conductor.nif = :nif", Incidencia.class);
        query.setParameter("nif", nif);
        return query.getResultList();
    }
    
    public void deleteByNif(String nif) {
        getEntityManager().createQuery(
            "DELETE FROM Incidencia i WHERE i.conductor.nif = :nif")
            .setParameter("nif", nif)
            .executeUpdate();
    }
    
    public Incidencia create(Date fecha, String nif, String anotacion) {
        Incidencia incidencia = new Incidencia();
        incidencia.setId(new IncidenciaPK(fecha, nif));
        incidencia.setAnotacion(anotacion);
        getEntityManager().persist(incidencia);
        return incidencia;
    }
} 