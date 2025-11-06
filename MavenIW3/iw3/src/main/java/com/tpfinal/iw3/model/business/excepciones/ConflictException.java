package com.tpfinal.iw3.model.business.excepciones;

/**
 * Excepción lanzada cuando el estado de una entidad no permite realizar una operación.
 * HTTP Status Code: 409 (Conflict)
 * 
 * Ejemplos de uso:
 * - Intentar activar carga en una orden con estado incorrecto
 * - Intentar cerrar una orden que no está en estado CERRADA_PARA_CARGAR
 */
public class ConflictException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConflictException() {
        super();
    }

    public ConflictException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(Throwable cause) {
        super(cause);
    }
}
