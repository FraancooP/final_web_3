package com.tpfinal.iw3.integration.cli1.model.business.interfaces;

import java.util.List;
import com.tpfinal.iw3.integration.cli1.model.ClienteCli1;
import com.tpfinal.iw3.model.Cliente;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface IClienteCli1Business {

    ClienteCli1 load(String idCli1) throws NotFoundException, BusinessException;

    List<ClienteCli1> list() throws BusinessException;

    ClienteCli1 add(ClienteCli1 cliente) throws BusinessException;



    Cliente loadOrCreate(ClienteCli1 cliente) throws BusinessException, NotFoundException;
}