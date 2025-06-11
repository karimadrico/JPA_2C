package es.ubu.lsi.dao.multas;

import java.util.List;
import javax.persistence.TypedQuery;
import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.multas.HistoricoIncidencia;
import es.ubu.lsi.model.multas.IncidenciaPK;

public class HistoricoIncidenciaDAO extends JpaDAO<HistoricoIncidencia, IncidenciaPK> {

    public HistoricoIncidenciaDAO() {
        super();
    }

    @Override
    public List<HistoricoIncidencia> findAll() {
        return getEntityManager().createQuery("SELECT h FROM HistoricoIncidencia h", HistoricoIncidencia.class).getResultList();
    }
    
    public List<HistoricoIncidencia> findByNif(String nif) {
        TypedQuery<HistoricoIncidencia> query = getEntityManager().createQuery(
            "SELECT h FROM HistoricoIncidencia h WHERE h.conductor.nif = :nif", HistoricoIncidencia.class);
        query.setParameter("nif", nif);
        return query.getResultList();
    }
} 