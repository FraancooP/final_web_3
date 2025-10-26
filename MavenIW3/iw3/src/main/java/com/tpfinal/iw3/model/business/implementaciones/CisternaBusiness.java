package com.tpfinal.iw3.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpfinal.iw3.model.Cisterna;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.ICisternaBusiness;
import com.tpfinal.iw3.model.persistence.CisternaRepository;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class CisternaBusiness implements ICisternaBusiness {


    @Autowired
    private CisternaRepository cisternaDAO;


    @Override
    public List<Cisterna> list() throws BusinessException {
        try {
            return cisternaDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Cisterna load(long id) throws NotFoundException, BusinessException {
        Optional<Cisterna> cisternaFound;

        try {
            cisternaFound = cisternaDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (cisternaFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Cisterna id= " + id).build();
        return cisternaFound.get();
    }


    @Override
    public Cisterna load(String codigoExterno) throws NotFoundException, BusinessException {
        Optional<Cisterna> cisternaFound;

        try {
            cisternaFound = cisternaDAO.findByCodigoExterno(codigoExterno);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (cisternaFound.isEmpty())
            throw NotFoundException.builder().message("No se Encuentra el Cisterna con Código Externo " + codigoExterno).build();

        return cisternaFound.get();
    }


    @Override
    public Cisterna add(Cisterna cisterna) throws DuplicateException, BusinessException {
        try {
            load(cisterna.getId());
            throw DuplicateException.builder().message("Ya existe el Cisterna id= " + cisterna .getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            load(cisterna.getCodigoExterno());
            throw DuplicateException.builder().message("Ya existe el Cisterna con Código Externo " + cisterna.getCodigoExterno()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return cisternaDAO.save(cisterna);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Crear Nuevo Cisterna").build();
        }
    }

    @Override
    public Cisterna update(Cisterna cisterna) throws NotFoundException, BusinessException, DuplicateException {
        load(cisterna.getId());

        Optional<Cisterna> cisternaFound;
        try {
            cisternaFound = cisternaDAO.findByCodigoExternoAndIdNot(cisterna.getCodigoExterno(), cisterna.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (cisternaFound.isPresent()) {
            throw DuplicateException.builder().message("Ya Existe un Cisterna con Código Externo =" + cisterna.getCodigoExterno()).build();
        }

        try {
            return cisternaDAO.save(cisterna);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Actualizar Cisterna").build();
        }
    }

    @Override
    public void delete(Cisterna cisterna) throws NotFoundException, BusinessException {
        delete(cisterna.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            cisternaDAO.deleteById(id);
        } catch (Exception e){
            throw BusinessException.builder().ex(e).build();
        }
    }




}
