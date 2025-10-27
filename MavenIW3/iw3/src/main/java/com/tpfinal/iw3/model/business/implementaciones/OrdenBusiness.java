package com.tpfinal.iw3.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IOrdenBusiness;
import com.tpfinal.iw3.model.persistence.OrdenRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {

    @Autowired
    private OrdenRepository ordenDAO;

    private DetalleOrdenBusiness detalleOrdenBusiness;



    @Override
    public List<Orden> list() throws BusinessException {
        try {
            return ordenDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Orden load(long id) throws NotFoundException, BusinessException {
        Optional<Orden> ordenFound;
        try {
            ordenFound = ordenDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (ordenFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra la Orden id= " + id).build();
        return ordenFound.get();
    }

    @Override
    public Orden add(Orden orden) throws DuplicateException, BusinessException {
        try {
            load(orden.getId());
            throw DuplicateException.builder().message("Ya existe la Orden id= " + orden.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return ordenDAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Crear Nueva Orden").build();
        }
    }

    @Override
    public Orden update(Orden orden) throws NotFoundException, BusinessException {
        load(orden.getId());
        try {
            return ordenDAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Actualizar Orden").build();
        }
    }

    @Override
    public void delete(Orden orden) throws NotFoundException, BusinessException {
        delete(orden.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);
        try {
            ordenDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    
}
