package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNoValidaException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ViajeNoAbiertoException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservaController {

    @Autowired
    private ViajesRepository viajesRepository;
    
    @GetMapping("/viaje/reserva/add")
    public String getAddReservaAction(@RequestParam Map<String, String> params, Model model) {
    	if (!params.containsKey("codViaje") || params.get("codViaje").isEmpty()) {
    		return "redirect:/viajes";
    	}
    	
    	model.addAttribute("codViaje", params.get("codViaje"));
    	return "reserva/reserva_form";
    }
    
    @PostMapping("/viaje/reserva/add")
    public String postAddReservaAction(@RequestParam Map<String, String> params, RedirectAttributes redirectAttribuetes) {
    	
    	ArrayList<String> errors = new ArrayList<>();
    	
    	int codViaje = Integer.parseInt(params.get("codViaje"));
    	String usuario = "";
    	String plazas = "";
    	Viaje viaje = new Viaje(codViaje);
    	
    	try {
    		usuario = params.get("usuario");
    		plazas = params.get("plazas");
    		
    		viaje = viajesRepository.findViajeSiPermiteReserva(codViaje, usuario, plazas);
    			
    		
    		
    	}catch(ViajeNotFoundException e) {
    		errors.add(e.getMessage());    		
    	}catch(ViajeNoAbiertoException e){
    		errors.add(e.getMessage());
    	} catch (ReservaNoValidaException e) {
    		errors.add(e.getMessage());
		}
    	
    	if (errors.size()>0) {
        	redirectAttribuetes.addFlashAttribute("errors",errors);
        	redirectAttribuetes.addAttribute("codViaje", codViaje);

        	return "redirect:/viaje/reserva/add";
        }
    	
    	
    	Reserva reserva = new Reserva(viajesRepository.getNewCodReserva(viaje), usuario, Integer.parseInt(plazas), viaje);
    	try {
			
			if((viajesRepository.getPlazasOcupadas(viaje)+ reserva.getPlazasSolicitadas()) == viaje.getPlazasOfertadas()) {
				viaje.cerrarViaje();
				viajesRepository.update(viaje);
			}
			viajesRepository.save(reserva);
		} catch (ReservaAlreadyExistsException e) {
			errors.add(e.getMessage());
		} catch (ReservaNotFoundException e) {
			errors.add(e.getMessage());
		} catch (ViajeNotFoundException e) {
			errors.add(e.getMessage());
		}
    	
    	if (errors.size()>0) {
        	redirectAttribuetes.addFlashAttribute("errors",errors);
        	redirectAttribuetes.addAttribute("codViaje", codViaje);
        	return "redirect:/viaje/reserva/add";
        }
    	
    	redirectAttribuetes.addFlashAttribute("confirmacion", "Reserva insertado con éxito.");
    	return "redirect:/viajes";
    }
    
    @GetMapping("/viaje/reservas")
    public String getListadoReservas(Model model, @RequestParam Map<String, String> params) {
    	
    	if (!params.containsKey("codViaje") || params.get("codViaje").isEmpty()) {
    		return "redirect:/viajes";
    	}
    	
    	Viaje viaje =  viajesRepository.findViajeById(Integer.parseInt(params.get("codViaje")));
    	List<Reserva> reservas = viajesRepository.findReservasByViaje(viaje);
    	model.addAttribute("viaje",viaje);
    	model.addAttribute("reservas",reservas);
    	return "reserva/listado";
    	
    }
    
    
}
