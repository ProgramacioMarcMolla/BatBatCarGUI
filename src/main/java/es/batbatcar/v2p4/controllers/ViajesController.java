package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;
import es.batbatcar.v2p4.utils.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ViajesController {

    @Autowired
    private ViajesRepository viajesRepository;
    
    /**
     * Endpoint que muestra el listado de todos los viajes disponibles
     *
     * */
    @GetMapping("/viajes")
    public String getViajesAction(Model model, @RequestParam Map<String, String> params) {
    	
    	if(params.containsKey("ciudad")) {
    		model.addAttribute("viajes", viajesRepository.findAll(params.get("ciudad")));
    	}else {
    		model.addAttribute("viajes", viajesRepository.findAll());
    	}
        model.addAttribute("titulo", "Listado de viajes");
        return "viaje/listado";
    }
    
    
    @GetMapping("/viajes/add")
    public String getViajesAdd(Model model) {
    	return "viaje/viaje_form";
    }
    
    
    @PostMapping(value = "/viajes/add")
    public String postAddViajeAction(@RequestParam Map<String, String> params, RedirectAttributes redirectAttribuetes) {
        List<String> errors = new ArrayList<>(); 
        
        String ruta ;
    	int plazas ;
    	String propietario;
        float precio;
        int duracion;
        String diaSalida ;
        
        String horaSalida;
        String minutoSalida ;
        String salida ;

    	try {
    		 ruta = params.get("ruta");
        	 plazas = Integer.parseInt(params.get("plazas"));
        	 propietario = params.get("propietario");
             precio = Float.parseFloat(params.get("precio"));
             duracion = Integer.parseInt(params.get("duracion"));
             diaSalida = params.get("dia_salida");
            
             horaSalida = params.get("hora_salida");
             minutoSalida = params.get("minuto_salida");
             salida = horaSalida + ":" + minutoSalida;
            
         
            if(!Validator.isValidText(ruta, '-')) {
        		errors.add( "Ruta: La ruta No cumple con el formato establecido: ParadaIncial - Paradas - ParadaFinal");
        	}
            
            if(!Validator.isValidNumberMinMax(plazas, 1,6)) {
            	errors.add("Plazas: Deben tener un valor positivo");
            }
            
            if(!Validator.isValidText(propietario,' ')) {
        		errors.add( "Propietario: El propietario debe tener un formato tal que: Nombre Apellido");
        	}
            
            
            
            if(!Validator.isValidNumberMin(precio, 1)) {
            	errors.add("Precio: Debe tener un valor positivo");
            }
            
            if(!Validator.isValidNumberMin(duracion, 1)) {
            	errors.add("Duracion: Debe tener un valor positivo");
            }
            
            if(!Validator.isValidDate(diaSalida)) {
            	errors.add("Fecha: Fecha inválida");
            }

            if(!Validator.isValidTime(salida)) {
            	errors.add("Hora y minutos salida: Inválidos");
            }
            

        	
            
    	}catch(NumberFormatException e) {
    		errors.clear();
            errors.add("Debes completar todos los campos");
    		
        	redirectAttribuetes.addFlashAttribute("errors",errors);
    		return "redirect:/viajes/add";
    	}
    	
    	if (errors.size()>0) {
        	redirectAttribuetes.addFlashAttribute("errors",errors);
        	return "redirect:/viajes/add";
        }
    	
    	
    	LocalDateTime fechaConTiempo = LocalDateTime.of(LocalDate.parse(diaSalida), LocalTime.parse(horaSalida + ":" + minutoSalida));
    	
    	Viaje viaje = new Viaje(viajesRepository.getNextCodViaje(),propietario , ruta, fechaConTiempo, duracion, precio, plazas);
    	try {
			viajesRepository.save(viaje);
		} catch (ViajeAlreadyExistsException e) {
			e.printStackTrace();
		} catch (ViajeNotFoundException e) {
			e.printStackTrace();
		}
    	
    	redirectAttribuetes.addFlashAttribute("confirmacion", "Viaje insertado con éxito.");
    	return "redirect:/viajes";
    	

    }
    

}
