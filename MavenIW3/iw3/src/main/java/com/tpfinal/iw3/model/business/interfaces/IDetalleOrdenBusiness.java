package com.tpfinal.iw3.model.business.interfaces;

import java.util.List;

import com.tpfinal.iw3.model.DetalleOrden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface IDetalleOrdenBusiness {

    public DetalleOrden load(long id) throws NotFoundException, BusinessException;

    public List<DetalleOrden> listByOrder(long idOrder) throws NotFoundException, BusinessException;

    DetalleOrden add(DetalleOrden detail) throws DuplicateException, BusinessException;

}
