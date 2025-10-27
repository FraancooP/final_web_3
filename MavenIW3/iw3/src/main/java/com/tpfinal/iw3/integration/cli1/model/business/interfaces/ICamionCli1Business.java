package com.tpfinal.iw3.integration.cli1.model.business.interfaces;

import java.util.List;
import com.tpfinal.iw3.integration.cli1.model.CamionCli1;
import com.tpfinal.iw3.model.Camion;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface ICamionCli1Business {

    CamionCli1 load(String idCli1) throws NotFoundException, BusinessException;

    List<CamionCli1> list() throws BusinessException;

    CamionCli1 add(CamionCli1 camion) throws BusinessException;

    Camion loadOrCreate(CamionCli1 camion) throws BusinessException, NotFoundException;
}