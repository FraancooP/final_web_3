package com.tpfinal.iw3.model.business.excepciones;

import java.io.Serial;

import lombok.Builder;
import lombok.NoArgsConstructor;



@NoArgsConstructor
public class DuplicateException extends Exception{
    //Lanzamos esta excepcion cuando intentamos crear una entidad con un campo unico
    //que ya existe Por ejemplo:
    //Patente, codigo externo, numero de orden, documento, etc.



    @Serial
    private static final long serialVersionUID = 1L;


    @Builder
    public DuplicateException(String message, Throwable ex) {
        super(message, ex);
    }

    @Builder
    public DuplicateException(String message) {
        super(message);
    }

    @Builder
	public DuplicateException(Throwable ex) {
		super(ex);
	}

}
