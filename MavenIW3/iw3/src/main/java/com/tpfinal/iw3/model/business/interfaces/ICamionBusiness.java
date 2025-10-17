package com.tpfinal.iw3.model.business.interfaces;

import java.util.List;

import com.tpfinal.iw3.model.Camion;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
//Definimos las operaciones Permitidas sobre camiones



public interface ICamionBusiness {

    public List<Camion> list() throws BusinessException;

    public Camion add(Camion camion) throws BusinessException, DuplicateException;

    public Camion update(Camion camion) throws NotFoundException, DuplicateException, BusinessException;

    public Camion load(long id) throws NotFoundException, BusinessException;

    public Camion load(String patente) throws NotFoundException, BusinessException;

    public void delete(Camion camion) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

}