package com.tpfinal.iw3.integration.cli1.model.business.interfaces;

import com.tpfinal.iw3.integration.cli1.model.CisternaCli1;
import com.tpfinal.iw3.model.Cisterna;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface ICisternaCli1Business {

    CisternaCli1 load(String idCli1) throws NotFoundException, BusinessException;

    Cisterna loadOrCreate(CisternaCli1 cisternaCli1) throws BusinessException, NotFoundException;
}
