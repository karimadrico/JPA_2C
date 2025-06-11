package es.ubu.lsi.service.multas;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import es.ubu.lsi.model.multas.*;
import es.ubu.lsi.service.PersistenceService;
import es.ubu.lsi.service.multas.Service;
import es.ubu.lsi.dao.multas.*;

public class ServiceImpl extends PersistenceService implements Service {
    
    private VehiculoDAO vehiculoDAO;
    private ConductorDAO conductorDAO;
    private TipoIncidenciaDAO tipoIncidenciaDAO;
    private IncidenciaDAO incidenciaDAO;
    private HistoricoIncidenciaDAO historicoIncidenciaDAO;
    
    public ServiceImpl() {
        EntityManager em = this.getEntityManager();
        vehiculoDAO = new VehiculoDAO();
        vehiculoDAO.setEntityManager(em);
        conductorDAO = new ConductorDAO();
        conductorDAO.setEntityManager(em);
        tipoIncidenciaDAO = new TipoIncidenciaDAO();
        tipoIncidenciaDAO.setEntityManager(em);
        incidenciaDAO = new IncidenciaDAO();
        incidenciaDAO.setEntityManager(em);
        historicoIncidenciaDAO = new HistoricoIncidenciaDAO();
        historicoIncidenciaDAO.setEntityManager(em);
    }

    @Override
    public void insertarIncidencia(Date fecha, String nif, long tipo) throws PersistenceException {
        try {
            begin();
            
            Conductor conductor = conductorDAO.findByNif(nif);
            if (conductor == null) {
                throw new PersistenceException("No existe el conductor con NIF: " + nif);
            }
            
            TipoIncidencia tipoIncidencia = tipoIncidenciaDAO.findById(tipo);
            if (tipoIncidencia == null) {
                throw new PersistenceException("No existe el tipo de incidencia con ID: " + tipo);
            }
            
            int puntosRestantes = conductor.getPuntos() - tipoIncidencia.getValor();
            if (puntosRestantes < 0) {
                throw new PersistenceException("El conductor no tiene puntos suficientes");
            }
            
            Incidencia incidencia = new Incidencia();
            incidencia.setId(new IncidenciaPK(fecha, nif));
            incidencia.setConductor(conductor);
            incidencia.setTipoIncidencia(tipoIncidencia);
            incidenciaDAO.persist(incidencia);
            
            conductor.setPuntos(puntosRestantes);
            conductor.getIncidencias().add(incidencia);
            
            commit();
        } catch (Exception e) {
            rollback();
            throw new PersistenceException(e.getMessage());
        }
    }

    @Override
    public void indultar(String nif) throws PersistenceException {
        try {
            begin();
            
            Conductor conductor = conductorDAO.findByNif(nif);
            if (conductor == null) {
                throw new PersistenceException("No existe el conductor con NIF: " + nif);
            }
            
            List<Incidencia> incidencias = incidenciaDAO.findByNif(nif);
            for (Incidencia incidencia : incidencias) {
                HistoricoIncidencia historico = new HistoricoIncidencia(incidencia);
                historicoIncidenciaDAO.persist(historico);
                conductor.getHistoricos().add(historico);
            }
            
            conductor.getIncidencias().clear();
            incidenciaDAO.deleteByNif(nif);
            conductor.setPuntos(12);
            
            commit();
        } catch (Exception e) {
            rollback();
            throw new PersistenceException(e.getMessage());
        }
    }

    @Override
    public List<Vehiculo> consultarVehiculos() throws PersistenceException {
        try {
            begin();
            List<Vehiculo> vehiculos = vehiculoDAO.findAllWithGraph();
            commit();
            return vehiculos;
        } catch (Exception e) {
            rollback();
            throw new PersistenceException(e.getMessage());
        }
    }
} 