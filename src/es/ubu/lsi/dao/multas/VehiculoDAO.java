package es.ubu.lsi.dao.multas;

import java.util.List;
import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;
import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.multas.Vehiculo;

public class VehiculoDAO extends JpaDAO<Vehiculo, String> {

    public VehiculoDAO() {
        super();
    }

    @Override
    public List<Vehiculo> findAll() {
        return getEntityManager().createQuery("SELECT v FROM Vehiculo v", Vehiculo.class).getResultList();
    }
    
    public List<Vehiculo> findAllWithGraph() {
        EntityGraph<Vehiculo> graph = getEntityManager().createEntityGraph(Vehiculo.class);
        graph.addAttributeNodes("conductores");
        graph.addSubgraph("conductores").addAttributeNodes("incidencias", "historicos");
        graph.addSubgraph("conductores").addSubgraph("incidencias").addAttributeNodes("tipoIncidencia");
        graph.addSubgraph("conductores").addSubgraph("historicos").addAttributeNodes("tipoIncidencia");
        
        TypedQuery<Vehiculo> query = getEntityManager().createQuery(
            "SELECT DISTINCT v FROM Vehiculo v LEFT JOIN FETCH v.conductores", Vehiculo.class);
        query.setHint("javax.persistence.fetchgraph", graph);
        
        return query.getResultList();
    }
} 