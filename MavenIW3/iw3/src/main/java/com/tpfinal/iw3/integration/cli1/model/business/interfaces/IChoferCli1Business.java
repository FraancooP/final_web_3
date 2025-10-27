package com.tpfinal.iw3.integration.cli1.model.business.interfaces;

import java.util.List;
import com.tpfinal.iw3.integration.cli1.model.ChoferCli1;
import com.tpfinal.iw3.model.Chofer;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface IChoferCli1Business {

    ChoferCli1 load(String idCli1) throws NotFoundException, BusinessException;

    List<ChoferCli1> list() throws BusinessException;

    ChoferCli1 add(ChoferCli1 chofer) throws BusinessException;

    Chofer loadOrCreate(ChoferCli1 chofer) throws BusinessException, NotFoundException;
}