package es.batbatcar.v2p4.exceptions;

public class ViajeNoAbiertoException extends Exception {
	public ViajeNoAbiertoException(int codViaje) {
		super("El viaje " + codViaje + " no está abierto.");
	}

	public ViajeNoAbiertoException(String codViaje) {
		super("El viaje " + codViaje + " no está abierto.");
	}
}
