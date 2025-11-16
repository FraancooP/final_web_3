package com.tpfinal.iw3.model.business.excepciones;

import java.io.Serial;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ConciliacionException extends Exception{


    //Lanzamos esta excepcion cuiando se intenta generar la Coincilacion de una orden
    //Requerimos: Orden en estado FINALIZADA, Pesaje inicial y final registrados
    //Se va a lanzar cuando:
    //Orden finzalada pero sin pesajes completos
    //No hay registros de detalle para calcular promedios
    //Falta pesaje inical o pesaje final
    //Error de calculos

    @Serial
    private static final long serialVersionUID = 1L;


    @Builder
    public ConciliacionException(String message, Throwable ex) {
        super(message, ex);
    }

    @Builder
    public ConciliacionException(String message) {
        super(message);
    }

    @Builder
	public ConciliacionException(Throwable ex) {
		super(ex);
	}
}
