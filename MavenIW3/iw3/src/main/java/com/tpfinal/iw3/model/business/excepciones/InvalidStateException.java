package com.tpfinal.iw3.model.business.excepciones;

import java.io.Serial;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidStateException extends Exception{

    //Lanzamos esta excepcion cuando se intenta realizar una operacio sobre una orden que no esta en el estado correcto
    //Por ejemplo:
    //Cuando Intentemos recibir datos de carga en orden cerrada
    //Intentar cerrar orden sin pesaje inicial
    //Intentar registrar pesaje inicial en orden que ya tiene pesaje inicia;


    @Serial
    private static final long serialVersionUID = 1L;


    @Builder
    public InvalidStateException(String message) {
        super(message);
    }

    @Builder
    public InvalidStateException(String message, Throwable ex) {
        super(message, ex);
    }

    @Builder
	public InvalidStateException(Throwable ex) {
		super(ex);
	}


}
