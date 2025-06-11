package es.ubu.lsi.dao.multas;

import java.util.List;
import javax.persistence.TypedQuery;
import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.multas.TipoIncidencia;

public class TipoIncidenciaDAO extends JpaDAO<TipoIncidencia, Long> {

    @Override
    public List<TipoIncidencia> findAll() {
        return getEntityManager().createQuery("SELECT t FROM TipoIncidencia t", TipoIncidencia.class).getResultList();
    }
    
    public TipoIncidencia findById(Long id) {
        TypedQuery<TipoIncidencia> query = getEntityManager().createQuery(
            "SELECT t FROM TipoIncidencia t WHERE t.id = :id", TipoIncidencia.class);
        query.setParameter("id", id);
        List<TipoIncidencia> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
} 
