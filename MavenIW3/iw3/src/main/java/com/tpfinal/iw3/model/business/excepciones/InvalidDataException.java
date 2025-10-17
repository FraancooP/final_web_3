package com.tpfinal.iw3.model.business.excepciones;

import java.io.Serial;

import lombok.Builder;

public class InvalidDataException extends Exception{


    //Lanzamos esta excepcion cuando se reciben datos que no cumplen con las reglas
    //del negocio Por ejemplo:
    //Caudal negativo
    //Masa acomulada negativa
    //Password de activacion invalida
    //PReset negativo o cero

    @Serial
    private static final long serialVersionUID = 1L;

    @Builder
    public InvalidDataException(String message, Throwable ex) {
        super(message, ex);
    }


    @Builder
    public InvalidDataException(String message) {
        super(message);
    }

    @Builder
    public InvalidDataException(Throwable ex) {
        super(ex);
    }





}
