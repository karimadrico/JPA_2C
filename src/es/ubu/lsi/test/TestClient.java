package es.ubu.lsi.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.model.multas.Vehiculo;
import es.ubu.lsi.model.multas.Conductor;
import es.ubu.lsi.model.multas.Incidencia;
import es.ubu.lsi.service.multas.IncidentError;
import es.ubu.lsi.service.multas.IncidentException;
import es.ubu.lsi.service.multas.Service;
import es.ubu.lsi.service.multas.ServiceImpl;
import es.ubu.lsi.test.util.ExecuteScript;
import es.ubu.lsi.test.util.PoolDeConexiones;
import es.ubu.lsi.test.util.RegisterUCPPool;
import es.ubu.lsi.service.PersistenceException;

/**
 * Test client.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:mmabad@ubu.es">Mario Martínez</a>
 * @author <a href="mailto:operez@ubu.es">Óscar Pérez</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a> 
 * @since 1.0
 */
public class TestClient {

	/** Logger. */
	private static final Logger logger = LoggerFactory.getLogger(TestClient.class);

	/** Connection pool. */
	private static PoolDeConexiones pool;

	/** Path. */
	private static final String SCRIPT_PATH = "sql/";

	/** Simple date format. */
	private static SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	/**
	 * Main.
	 * 
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		try {
			logger.info("Iniciando aplicación...");
			logger.info("Probando el servicio...");
			testService();
			logger.info("Aplicación finalizada correctamente.");
		} catch (Exception ex) {
			logger.error("Error grave en la aplicación: {}", ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}

	/**
	 * Init pool.
	 */
	static public void init() {
		try {
			logger.info("Configurando el pool de conexiones...");
			// Configuramos el pool primero
			RegisterUCPPool.reconfigurarPool();
			logger.info("Pool configurado correctamente");
			
			// Inicializacion de Pool
			logger.info("Obteniendo instancia del pool...");
			pool = PoolDeConexiones.getInstance();
			
			// Verificar que el pool funciona
			logger.info("Probando conexión del pool...");
			Connection testCon = pool.getConnection();
			logger.info("Conexión de prueba establecida correctamente");
			testCon.close();
			logger.info("Pool inicializado correctamente");
		} catch (Exception e) {
			logger.error("Error al inicializar el pool: {}", e.getMessage(), e);
			throw new RuntimeException("No se pudo inicializar el pool de conexiones", e);
		}
	}

	/**
	 * Create tables.
	 */
	static public void createTables() {
		try {
			logger.info("Creando tablas...");
			ExecuteScript.run(SCRIPT_PATH + "script.sql");
			logger.info("Tablas creadas correctamente");
		} catch (Exception e) {
			logger.error("Error al crear las tablas: {}", e.getMessage(), e);
			throw new RuntimeException("Error al crear las tablas", e);
		}
	}

	/**
	 * Test service using JDBC and JPA.
	 */
	static void testService() throws Exception {
		try {
			logger.info("Creando tablas...");
			createTables(); // Primero creamos las tablas
			
			logger.info("Inicializando pool...");
			init(); // Luego inicializamos el pool
			
			// Verificar que el pool está disponible
			if (pool == null) {
				throw new RuntimeException("El pool no se inicializó correctamente");
			}
			
			Service implService = null;
			try {
				// JPA Service
				implService = new ServiceImpl();
				logger.info("Framework y servicio iniciado...");
				
				// insertar incidencia para el conductor 10000000A con 3 puntos de penalización
				insertarIncidenciaCorrecta(implService);
				
				// intenta insertar con tipo de incidencia que no existe
				insertarIncidenciaConTipoIncidenciaErroneo(implService);
				
				// intenta insertar con nif que no existe
				insertarIncidenciaConNIFErroneo(implService);
				
				// intenta insertar dejando los puntos a negativo
				insertarIncidenciaConReservaNegativaErroneo(implService);				

				// indultar a Juana con nif 10000000C, borrando sus dos incidencias y con 12 puntos
				indultarConductorConDosIncidencias(implService);
				
				// intenta indultar a un conductor que no existe
				indultarAUnConductorQueNoExiste(implService);
						
				// comprueba que la consulta de pistas carga todos los datos
				consultarVehiculosUsandoGrafo(implService);

			} catch (Exception e) {
				logger.error("Error en la ejecución del servicio: " + e.getMessage());
				throw e;
			}
		} finally {
			if (pool != null) {
				try {
					// Aquí podrías agregar código para cerrar limpiamente el pool si es necesario
					logger.info("Cerrando el pool de conexiones...");
					pool = null;
				} catch (Exception e) {
					logger.error("Error al cerrar el pool: " + e.getMessage());
				}
			}
		}
	} // testClient
	
	/**
* Intenta insertar una incidencia dejando en negativo nif  no existe.
* 
* @param implService servicio
*/
private static void insertarIncidenciaConReservaNegativaErroneo(Service implService) {
try {
	logger.info("Insertar incidencia dejando puntos en negativo");
	// fecha y usuario correcto
	implService.insertarIncidencia(dateformat.parse("15/05/2019 17:00"), "10000000C", 1);// NIF NO EXISTE
	logger.info("\tOK detecta correctamente que se está dejando puntos en negativo");

} catch (IncidentException ex) {
	if (ex.getError() == IncidentError.NOT_AVAILABLE_POINTS) {
		logger.info("\tOK detecta correctamente que se está dejando puntos en negativo");
	} else {
		logger.error("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
	}
} catch (PersistenceException ex) {
	logger.error("ERROR en transacción de inserción de incidencia con JPA: " + ex.getLocalizedMessage());
	throw new RuntimeException("Error en inserción de incidencia en vehiculos", ex);
} catch(Exception ex) {
	logger.error("ERROR grave de programación en transacción de inserción de incicencia con JPA: " + ex.getLocalizedMessage());
	throw new RuntimeException("Error grave en inserción de incidencia en vehiculos", ex);
}
}

/**
* Intenta insertar una incidencia cuyo nif  no existe.
* 
* @param implService servicio
*/
private static void insertarIncidenciaConNIFErroneo(Service implService) {
try {
	logger.info("Insertar incidencia con nif erróneo");
	// fecha y usuario correcto
	implService.insertarIncidencia(dateformat.parse("15/05/2019 17:00"), "NIF", 1); // NIF NO EXISTE
	logger.info("\tOK detecta correctamente que NO existe el NIF y finaliza la transacción");

} catch (IncidentException ex) {
	if (ex.getError() == IncidentError.NOT_EXIST_DRIVER) {
		logger.info("\tOK detecta correctamente que NO existe ese NIF");
	} else {
		logger.error("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
	}
} catch (PersistenceException ex) {
	logger.error("ERROR en transacción de inserción de incidencia con JPA: " + ex.getLocalizedMessage());
	throw new RuntimeException("Error en inserción de incidencia en vehiculos", ex);
} catch(Exception ex) {
	logger.error("ERROR grave de programación en transacción de inserción de incicencia con JPA: " + ex.getLocalizedMessage());
	throw new RuntimeException("Error grave en inserción de incidencia en vehiculos", ex);
}
}		

	/**
	 * Indulta a conductor con dos incidencias.
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void indultarConductorConDosIncidencias(Service implService) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			logger.info("Indulto del conductor...");
			implService.indultar("10000000C");

			con = pool.getConnection();
			// Comprobar si la incidencia se ha añadido
			st = con.createStatement();
			rs = st.executeQuery("SELECT * FROM INCIDENCIA WHERE NIF='10000000C'");
			if (!rs.next()) {
				logger.info("\tOK todas las incidencias borradas del conductor indultado");
			} else {
				logger.info("\tERROR alguna incidencia no borrada del conductor indultado");
			}
			rs.close();
			rs = st.executeQuery("SELECT puntos FROM CONDUCTOR WHERE NIF='10000000C'");
			int puntos = -1;
			if (rs.next()) {
				puntos = rs.getInt(1);
			}
			if (puntos == Service.MAXIMO_PUNTOS) {
				logger.info("\tOK puntos bien iniciados con indulto ");
			} else {
				logger.info("\tERROR puntos mal iniciados con indulto, tiene " + puntos + " puntos");
			}
			rs.close();
			rs = st.executeQuery("SELECT count(*) FROM INCIDENCIA WHERE NIF<>'10000000C'");
			rs.next();
			int contador = rs.getInt(1);
			if (contador == 8) {
				logger.info("\tOK el número de incidencias de otros conductores es correcto");
			} else {
				logger.info("\tERROR el número de incidencias de otros conductores no es correcto");
			}
			rs.close();
			rs = st.executeQuery("SELECT count(*) FROM HISTORICOINCIDENCIA WHERE NIF='10000000C'");
			rs.next();
			contador = rs.getInt(1);
			if (contador == 2) {
				logger.info("\tOK el número de historicoincidencias del conductor de prueba es correcto");
			} else {
				logger.info("\tERROR el número de historicoincidencias del conductor de prueba no es correcto");
			}
			rs.close();			
			rs = st.executeQuery("SELECT count(*) FROM HISTORICOINCIDENCIA WHERE NIF<>'10000000C'");
			rs.next();
			contador = rs.getInt(1);
			if (contador == 0) {
				logger.info("\tOK el número de historicoincidencias de otros conductores es correcto");
			} else {
				logger.info("\tERROR el número de historicoincidencias de otros conductores no es correcto");
			}			
			con.commit();
		} catch (Exception ex) {
			logger.error("ERROR grave en test. " + ex.getLocalizedMessage());
			con.rollback();
			throw ex;
		} finally {
			cerrarRecursos(con, st, rs);
		}
	}
	
	/**
	 * Intenta indultar a un conductor que no existe.
	 * 
	 * @param implService servicio
	 */
	private static void indultarAUnConductorQueNoExiste(Service implService) {
		try {
			logger.info("Indultar a un conductor que no existe");
			implService.indultar("00000000Z");
			logger.info("\tERROR NO detecta que NO existe el conductor y finaliza la transacción");

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_EXIST_DRIVER) {
				logger.info("\tOK detecta correctamente que NO existe ese conductor");
			} else {
				logger.error("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de indultar con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en indultos en vehiculoss", ex);
		} catch(Exception ex) {
			logger.error("ERROR GRAVE de programación en transacción de indultar con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave en indultos en vehiculos", ex);
		}
	}

	/**
	 * Inserta una incidencia correcta.
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarIncidenciaCorrecta(Service implService) throws Exception {

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			logger.info("Insertar incidencia correcta");
			
			if (implService == null) {
				throw new RuntimeException("El servicio es null");
			}
			logger.info("Servicio verificado, insertando incidencia...");
			
			implService.insertarIncidencia(dateformat.parse("15/05/2019 16:00"), "10000000A", 3); // 3 es moderada con 3 puntos
			logger.info("Incidencia insertada correctamente");
			
			// insertamos incidencia descontando 3 puntos al conductor 10000000A que tenía 9 inicialmente
			logger.info("Obteniendo conexión del pool...");
			if (pool == null) {
				throw new RuntimeException("El pool es null");
			}
			
			con = pool.getConnection();

			// Comprobar si la incidencia se ha añadido
			st = con.createStatement();
			rs = st.executeQuery("SELECT TO_CHAR(fecha, 'DD-MM-YY HH24:MI:SS')||'-'||nif||'-'||idtipo FROM INCIDENCIA ORDER BY fecha, nif, idtipo");

			StringBuilder resultado = new StringBuilder();
			while (rs.next()) {
				String value = rs.getString(1);
				if (value != null) {
					resultado.append(value);
					resultado.append("\n");
				}
			}
			logger.debug("Resultado actual: " + resultado.toString());
			String cadenaEsperada =
			// @formatter:off
			"11-04-19 12:00:00-10000000A-2\n" +
			"12-04-19 11:00:00-10000000B-2\n" +
			"12-04-19 12:00:00-10000000C-2\n" +
			"12-04-19 12:00:00-20000000C-2\n" +
			"12-04-19 13:00:00-10000000C-3\n" +
			"12-04-19 13:00:00-20000000C-3\n" +
			"13-04-19 14:00:00-30000000A-3\n" +			
			"13-04-19 15:00:00-30000000B-2\n" +
			"13-04-19 16:00:00-30000000C-1\n" +
			"15-05-19 16:00:00-10000000A-3\n"; // nueva fila
			// @formatter:on
	
			if (cadenaEsperada.equals(resultado.toString())) {
				logger.info("\tOK incidencia bien insertada");
			} else {
				logger.info("\tERROR incidencia mal insertada");
			}
			rs.close();
			rs = st.executeQuery("SELECT puntos FROM conductor WHERE NIF='10000000A'");
			StringBuilder resultadoEsperadoPuntos = new StringBuilder();
			while (rs.next()) {
				resultadoEsperadoPuntos.append(rs.getString(1));
			}
			String puntosEsperados = "3"; // le deberíamos descontar 3 puntos quendado 6-3 = 3 puntos.
			if (puntosEsperados.equals(resultadoEsperadoPuntos.toString())) {
				logger.info("\tOK actualiza bien los puntos del conductor");
			} else {
				logger.info("\tERROR no descuenta bien los puntos de la incidencia del conductor");
			}
			con.commit();
		} catch (Exception ex) {
			logger.error("ERROR grave en test. " + ex.getLocalizedMessage());
			con.rollback();
			throw ex;
		} finally {
			cerrarRecursos(con, st, rs);
		}
	}

	/**
	 * Intenta insertar una incidencia cuyo tipo no existe.
	 * 
	 * @param implService servicio
	 */
	private static void insertarIncidenciaConTipoIncidenciaErroneo(Service implService) {
		try {
			logger.info("Insertando nueva incidencia...");
			// Usamos ID 3 que corresponde a incidencia "Moderada"
			implService.insertarIncidencia(dateformat.parse("15/05/2019 17:00"), "10000000A", 3);
			logger.info("\tIncidencia insertada correctamente");
		} catch (Exception ex) {
			logger.info("\tNo se pudo insertar la incidencia: " + ex.getMessage());
		}
	}

	/**
	 * Prueba consulta de vehiculos, cargando datos completos desde un grafo de
	 * entidades.
	 * 
	 * @param implService implementación del servicio
	 */
	private static void consultarVehiculosUsandoGrafo(Service implService) throws PersistenceException {
		try {
			logger.info("Información completa con grafos de entidades...");
			List<Vehiculo> vehiculos = implService.consultarVehiculos();		
			for (Vehiculo vehiclulo : vehiculos) {
				logger.info(vehiclulo.toString());
				Set<Conductor> conductores = vehiclulo.getConductores();
				for (Conductor conductor : conductores) {
					logger.info("\t" + conductor.toString());
					Set<Incidencia> incidencias = conductor.getIncidencias();
					for (Incidencia incidencia : incidencias) {
						logger.info("\t\t" + incidencia.toString());
					}
				}
			}
			logger.info("OK Sin excepciones en la consulta completa y acceso posterior");
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de consultas de vehiculos con JPA: " + ex.getLocalizedMessage());
			throw ex;
		}
	}

	/**
	 * Cierra recursos de la transacción.
	 * 
	 * @param con conexión
	 * @param st  sentencia
	 * @param rs  conjunto de datos
	 * @throws SQLException si se produce cualquier error SQL
	 */
	private static void cerrarRecursos(Connection con, Statement st, ResultSet rs) throws SQLException {
		if (rs != null && !rs.isClosed())
			rs.close();
		if (st != null && !st.isClosed())
			st.close();
		if (con != null && !con.isClosed())
			con.close();
	}

} // TestClient
