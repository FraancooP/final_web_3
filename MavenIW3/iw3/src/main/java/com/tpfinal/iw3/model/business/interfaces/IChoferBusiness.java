package com.tpfinal.iw3.model.business.interfaces;

import java.util.List;

import com.tpfinal.iw3.model.Chofer;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
public interface IChoferBusiness {

    public List<Chofer> list() throws BusinessException;

    public Chofer load(long id) throws BusinessException, NotFoundException;

    public Chofer load(String documento) throws BusinessException, NotFoundException;

    public Chofer add(Chofer chofer) throws BusinessException, DuplicateException;

    public Chofer update(Chofer chofer) throws BusinessException, DuplicateException, NotFoundException;

    public void delete(long id) throws BusinessException, NotFoundException;

    public void delete(Chofer chofer) throws BusinessException, NotFoundException;
}
