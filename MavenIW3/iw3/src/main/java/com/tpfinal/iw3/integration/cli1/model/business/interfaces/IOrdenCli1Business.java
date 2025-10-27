package com.tpfinal.iw3.integration.cli1.model.business.interfaces;

import java.util.List;
import com.tpfinal.iw3.integration.cli1.model.OrdenCli1;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface IOrdenCli1Business {

    OrdenCli1 load(String numeroOrdenCli1) throws NotFoundException, BusinessException;

    List<OrdenCli1> list() throws BusinessException;

    OrdenCli1 add(OrdenCli1 orden) throws BusinessException;

    /**
     * Recibe JSON externo, lo deserializa y crea la orden.
     * Este es el método principal del Punto 1.
     */
    OrdenCli1 addExternal(String json) throws BusinessException;
}