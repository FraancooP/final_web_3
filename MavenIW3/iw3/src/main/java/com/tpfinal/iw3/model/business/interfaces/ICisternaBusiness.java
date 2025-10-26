package com.tpfinal.iw3.model.business.interfaces;

import java.util.List;

import com.tpfinal.iw3.model.Cisterna;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface ICisternaBusiness {

    public List<Cisterna> list() throws BusinessException;

    public Cisterna load(long id) throws NotFoundException, BusinessException;

    public Cisterna load(String codigoExterno) throws NotFoundException, BusinessException;

    public Cisterna add(Cisterna cisterna) throws DuplicateException, BusinessException;

    public Cisterna update(Cisterna cisterna) throws NotFoundException, BusinessException, DuplicateException;

    public void delete(Cisterna cisterna) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

}
