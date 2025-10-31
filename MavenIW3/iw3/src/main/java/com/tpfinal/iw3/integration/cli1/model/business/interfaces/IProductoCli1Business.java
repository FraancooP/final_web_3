package com.tpfinal.iw3.integration.cli1.model.business.interfaces;

import com.tpfinal.iw3.integration.cli1.model.ProductoCli1;
import com.tpfinal.iw3.model.Producto;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface IProductoCli1Business {

    ProductoCli1 load(String idCli1) throws NotFoundException, BusinessException;

    Producto load(ProductoCli1 productoCli1) throws BusinessException, NotFoundException;
    
    Producto loadOrCreate(ProductoCli1 productoCli1) throws BusinessException, NotFoundException;
}