package com.tpfinal.iw3.model.business.excepciones;

import java.io.Serial;

import lombok.Builder;

//Excepciones base para todos los errores de negocio de nuestro sistema



public class BusinessException extends Exception{


	@Serial
	private static final long serialVersionUID = 1L;

	@Builder
	public BusinessException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public BusinessException(String message) {
		super(message);
	}

	@Builder
	public BusinessException(Throwable ex) {
		super(ex);
	}

}