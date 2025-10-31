package com.tpfinal.iw3.model.business.interfaces;

import java.util.List;

import com.tpfinal.iw3.model.Cliente;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;



public interface IClienteBusiness {

    public List<Cliente> list() throws BusinessException;

    public Cliente load(long id) throws BusinessException, NotFoundException;

    public Cliente load(String codigoExterno) throws BusinessException, NotFoundException;

    public Cliente loadByRazonSocial(String razonSocial) throws BusinessException, NotFoundException;

    public Cliente add(Cliente cliente) throws BusinessException, DuplicateException;

    public Cliente update(Cliente cliente) throws BusinessException, DuplicateException, NotFoundException;

    public void delete(long id) throws BusinessException, NotFoundException;

    public void delete(Cliente cliente) throws BusinessException, NotFoundException;
    
}
