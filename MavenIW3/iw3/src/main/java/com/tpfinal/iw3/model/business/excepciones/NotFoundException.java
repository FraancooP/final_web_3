package com.tpfinal.iw3.model.business.excepciones;

import java.io.Serial;

import lombok.Builder;
import lombok.NoArgsConstructor;


//Lanzamos esta excepcion cuando no se encuentra una entidad solicitada
//(Camion, Chofer, Cliente, Producto, Orden)

@NoArgsConstructor
public class NotFoundException extends Exception{


	@Serial
	private static final long serialVersionUID = 1L;

	@Builder
	public NotFoundException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public NotFoundException(String message) {
		super(message);
	}

	@Builder
	public NotFoundException(Throwable ex) {
		super(ex);
	}

}
