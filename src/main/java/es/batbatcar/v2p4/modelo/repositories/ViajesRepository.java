package es.batbatcar.v2p4.modelo.repositories;

import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNoValidaException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ViajeNoAbiertoException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dao.inmemorydao.InMemoryReservaDAO;
import es.batbatcar.v2p4.modelo.dao.inmemorydao.InMemoryViajeDAO;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.EstadoViaje;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.dao.interfaces.ReservaDAO;
import es.batbatcar.v2p4.modelo.dao.interfaces.ViajeDAO;
import es.batbatcar.v2p4.modelo.dao.sqldao.SQLReservaDAO;
import es.batbatcar.v2p4.modelo.dao.sqldao.SQLViajeDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Repository
public class ViajesRepository {

    private final ViajeDAO viajeDAO;
    private final ReservaDAO reservaDAO;

    public ViajesRepository(@Autowired SQLViajeDAO viajeDAO, @Autowired SQLReservaDAO reservaDAO) {
        this.viajeDAO = viajeDAO;
        this.reservaDAO = reservaDAO;
    }
    
    /** 
     * Obtiene un conjunto de todos los viajes
     * @return
     */
    public Set<Viaje> findAll() {
        
    	// Se recuperan todos los viajes del DAO de viajes
    	Set<Viaje> viajes = viajeDAO.findAll();
        
    	// Se completa la información acerca de las reservas de cada viaje a través del DAO de reservas
        for (Viaje viaje : viajes) {
        	if (this.reservaDAO.findAllByTravel(viaje).size() > 0) {
            	viaje.setSeHanRealizadoReservas(true);
            }
		}
        return viajes;
    }
    
    public Set<Viaje> findAll(String city) {
		TreeSet<Viaje> viajesDePaso = new TreeSet<>();
        for (Viaje viaje:findAll()) {
            if(viaje.tieneEstaCiudadDestino(city)){
                viajesDePaso.add(viaje);
            }
        }
        return viajesDePaso;
	}
    
    /**
     * Obtiene el código del siguiente viaje
     * @returnnull
     */
    public int getNextCodViaje() {
        return this.viajeDAO.findAll().size() + 1;
    }
    
    /**
     * Guarda el viaje (actualiza si ya existe o añade si no existe)
     * @param viaje
     * @throws ViajeAlreadyExistsException
     * @throws ViajeNotFoundException
     */
    public void save(Viaje viaje) throws ViajeAlreadyExistsException, ViajeNotFoundException {
    	
    	if (viajeDAO.findById(viaje.getCodViaje()) == null) {
    		viajeDAO.add(viaje);
    	} else {
    		viajeDAO.update(viaje);
    	}
    }
    
    public void update(Viaje viaje) throws ViajeNotFoundException {
    	viajeDAO.update(viaje);
    }
	
    /**
     * Encuentra todas las reservas de @viaje
     * @param viaje
     * @return
     */
	public List<Reserva> findReservasByViaje(Viaje viaje) {
		return reservaDAO.findAllByTravel(viaje);
	}
	
	/**
	 * Guarda la reserva
	 * @param reserva
	 * @throws ReservaAlreadyExistsException
	 * @throws ReservaNotFoundException
	 */
    public void save(Reserva reserva) throws ReservaAlreadyExistsException, ReservaNotFoundException {
    	
    	if (reservaDAO.findById(reserva.getCodigoReserva()) == null) {
    		reservaDAO.add(reserva);
    	} else {
    		reservaDAO.update(reserva);
    	}
    }
    
    /**
     * 
     * Elimina la reserva
     * @param reserva
     * @throws ReservaNotFoundException
     */
	public void remove(Reserva reserva) throws ReservaNotFoundException {
		reservaDAO.remove(reserva);
	}
	
	public Viaje findViajeById(int cod) {
		return viajeDAO.findById(cod);
	}

	public Viaje findViajeSiPermiteReserva(int codViaje, String usuario, String plazas) throws ViajeNoAbiertoException, ReservaNoValidaException, ViajeNotFoundException {
		Viaje viaje = findViajeById(codViaje);
		
		 if (viaje == null) {
		        throw new ViajeNotFoundException("El viaje no está disponible");
		    }
		
		List<Reserva> reservas = reservaDAO.findAllByTravel(viaje);
    	int plazasReservadas = 0;
    	
    	if(reservas != null && !reservas.isEmpty()) {
    		for(Reserva reserva: reservas) {
        		if (reserva.getUsuario().equals(usuario)) {
        			throw new ReservaNoValidaException("Ya has realizado una reserva");
        		}
        		
        		plazasReservadas += reserva.getPlazasSolicitadas();
        	}
    	}
    	
    	
		
		if(!viaje.getEstado().equals(EstadoViaje.ABIERTO)) {
			throw new ViajeNoAbiertoException(codViaje);
		}
		
		if (plazas == null || plazas.isEmpty()) {
	        throw new ReservaNoValidaException("El número de plazas no puede ser nulo o vacío");
	    } 
				
		if((Integer.parseInt(plazas) + plazasReservadas ) > viaje.getPlazasOfertadas()) {
			throw new ReservaNoValidaException("No quedan suficientes plazas, plazas disponibles = " + (viaje.getPlazasOfertadas()-plazasReservadas));
		}
		
		if (viaje.getPropietario().equals(usuario)) {
    		throw new ReservaNoValidaException("El propietario no puede realizar reservas");
    	}
		
		
		return viaje;
	}
	
	public int getPlazasOcupadas(Viaje viaje) {
		List<Reserva> reservas = reservaDAO.findAllByTravel(viaje);
		int plazasReservadas = 0;
		for(Reserva reserva: reservas) {
    		plazasReservadas += reserva.getPlazasSolicitadas();
    	}
		return plazasReservadas;
	}
	
	public int getNumReservas(Viaje viaje) {
		List<Reserva> reservas = reservaDAO.findAllByTravel(viaje);
		int numReservas = 0;
		for(Reserva reserva: reservas) {
			numReservas++;
			}
		return numReservas;
	}
	
	public String getNewCodReserva(Viaje viaje) {
	    List<Reserva> reservas = reservaDAO.findAllByTravel(viaje);
	    if (reservas.isEmpty()) {
	        return viaje.getCodViaje() + "-1";
	    }
	    
	    String codigoUltimaReserva = reservas.get(reservas.size() - 1).getCodigoReserva();
	    int numReserva = Integer.parseInt(codigoUltimaReserva.substring(codigoUltimaReserva.indexOf('-') + 1));
	    
	    String codigoNuevaReserva = viaje.getCodViaje() + "-" + (numReserva + 1);

	    return codigoNuevaReserva;
	}

	public Reserva findReservaById(String codReserva) {
		
		return reservaDAO.findById(codReserva);
		
	}

	
	
	
	
}
