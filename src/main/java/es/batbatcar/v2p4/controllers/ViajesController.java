package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;
import es.batbatcar.v2p4.utils.Validator;

import java.time.LocalDate;
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
    public String getViajesAction(Model model) {
        model.addAttribute("viajes", viajesRepository.findAll());
        model.addAttribute("titulo", "Listado de viajes");
        return "viaje/listado";
    }
    
    
    @GetMapping("/viajes/add")
    public String getViajesAdd(Model model) {
    	return "viaje/viaje_form";
    }
    
    
    @PostMapping(value = "/viajes/add")
    public String postAddViajeAction(@RequestParam Map<String, String> params, RedirectAttributes redirectAttribuetes) {
    	
    	try {
    		String ruta = params.get("ruta");
        	int plazas = Integer.parseInt(params.get("plazas"));
        	String propietario = params.get("propietario");
            double precio = Double.parseDouble(params.get("precio"));
            int duracion = Integer.parseInt(params.get("duracion"));
            String diaSalida = params.get("dia_salida");
            
            String horaSalida = params.get("hora_salida");
            String minutoSalida = params.get("minuto_salida");
            String salida = horaSalida + ":" + minutoSalida;
            
            List<String> errors = new ArrayList<>();    
         
            if(!Validator.isValidText(ruta, '-')) {
        		errors.add( "Ruta: La ruta No cumple con el formato establecido: ParadaIncial - Paradas - ParadaFinal");
        	}
            
            if(!Validator.isValidNumberMin(plazas, 1)) {
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
            

        	if (errors.size()>0) {
            	redirectAttribuetes.addFlashAttribute("errors",errors);
            	return "redirect:/viajes/add";
            }
        	
        	redirectAttribuetes.addFlashAttribute("confirmacion", "Viaje insertado con éxito.");
        	return "redirect:/viajes";
            
    	}catch(NumberFormatException e) {
            List<String> errors = new ArrayList<>();    	
            errors.add("Debes completar todos los campos");
    		
        	redirectAttribuetes.addFlashAttribute("errors",errors);
    		return "redirect:/viajes/add";
    	}
    	
    	
    	
    	
        
        
        
        
        
        
        
    	

    	
    }
    
    

}
