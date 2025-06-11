package es.ubu.lsi.dao.multas;

import java.util.List;
import javax.persistence.TypedQuery;
import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.multas.Conductor;

public class ConductorDAO extends JpaDAO<Conductor, String> {

    @Override
    public List<Conductor> findAll() {
        return getEntityManager().createQuery("SELECT c FROM Conductor c", Conductor.class).getResultList();
    }
    
    public Conductor findByNif(String nif) {
        TypedQuery<Conductor> query = getEntityManager().createQuery(
            "SELECT c FROM Conductor c WHERE c.nif = :nif", Conductor.class);
        query.setParameter("nif", nif);
        List<Conductor> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public void actualizarPuntos(String nif, int puntos) {
        Conductor conductor = findByNif(nif);
        if (conductor != null) {
            conductor.setPuntos(puntos);
            getEntityManager().merge(conductor);
        }
    }
    
    public void restaurarPuntos(String nif) {
        Conductor conductor = findByNif(nif);
        if (conductor != null) {
            conductor.setPuntos(12);
            getEntityManager().merge(conductor);
        }
    }
} 