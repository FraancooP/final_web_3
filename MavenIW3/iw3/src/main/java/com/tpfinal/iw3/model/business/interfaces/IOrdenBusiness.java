package com.tpfinal.iw3.model.business.interfaces;

import java.util.List;

import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface IOrdenBusiness {

    public List<Orden> list() throws BusinessException;

    public Orden load(long id) throws NotFoundException, BusinessException;

    public Orden add(Orden orden) throws DuplicateException, BusinessException;

    public Orden update(Orden orden) throws NotFoundException, BusinessException, DuplicateException;

    public void delete(Orden orden) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;


}
