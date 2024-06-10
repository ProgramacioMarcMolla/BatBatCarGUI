package es.batbatcar.v2p4.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {
	
    public static boolean isValidDateTime(String dateTime) {
        try {
            LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
    public static boolean isValidDate(String date) {
        try {
            System.out.println("try");
            LocalDate fecha = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            System.out.println("try");
            if(fecha.isBefore(LocalDate.now())) {
                System.out.println("if");
            	return false;
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidTime(String time) {
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
    
    public static boolean isValidText(String ruta, char separador) {
        if (ruta == null || ruta.trim().isEmpty()) {
            return false;
        }

        String regex = "^[A-Z][a-z]+(" + separador +"[A-Z][a-z]+)*$";
        return ruta.matches(regex);
    }
    
    public static boolean isValidNumberMin(int numero, int min) {
        return numero >= min;
    }
    
    public static boolean isValidNumberMinMax(int numero, int min, int max) {
        return numero >= min && numero <= max;
    }
    
    public static boolean isValidNumberMin(float numero, float min) {
        return numero >= min;
    }
    
    

}

