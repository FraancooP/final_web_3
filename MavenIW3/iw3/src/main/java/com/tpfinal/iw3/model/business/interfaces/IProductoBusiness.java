package com.tpfinal.iw3.model.business.interfaces;

import java.util.List;

import com.tpfinal.iw3.model.Producto;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface IProductoBusiness {

    public List<Producto> list() throws BusinessException;

    public Producto add(Producto producto) throws BusinessException, DuplicateException;

    public Producto update(Producto producto) throws NotFoundException, DuplicateException, BusinessException;

    public Producto load(long id) throws NotFoundException, BusinessException;

    public Producto load(String nombre) throws NotFoundException, BusinessException;

    public void delete(Producto producto) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

}
