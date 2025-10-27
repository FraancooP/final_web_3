package com.tpfinal.iw3.integration.cli1.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpfinal.iw3.integration.cli1.model.CamionCli1;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.ICamionCli1Business;
import com.tpfinal.iw3.integration.cli1.model.persistence.CamionCli1Repository;
import com.tpfinal.iw3.integration.cli1.util.MapperEntity;
import com.tpfinal.iw3.model.Camion;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.ICamionBusiness;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CamionCli1Business implements ICamionCli1Business {

    @Autowired
    private CamionCli1Repository camionCli1DAO;

    @Autowired
    private ICamionBusiness camionBaseBusiness;

    @Autowired
    private MapperEntity mapperEntity;

    @Override
    public CamionCli1 load(String idCli1) throws NotFoundException, BusinessException {
        Optional<CamionCli1> camion;
        try {
            camion = camionCli1DAO.findOneByIdCli1(idCli1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al buscar camión CLI1");
        }
        if (camion.isEmpty()) {
            throw new NotFoundException("No se encuentra el camión CLI1 con idCli1=" + idCli1);
        }
        return camion.get();
    }

    @Override
    public List<CamionCli1> list() throws BusinessException {
        try {
            return camionCli1DAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al listar camiones CLI1");
        }
    }

    @Override
    @Transactional
    public CamionCli1 add(CamionCli1 camion) throws BusinessException {
        try {
            // Buscar si ya existe el camión base por patente
            Camion camionBase = camionBaseBusiness.load(camion.getPatente());
            mapperEntity.map(camion, camionBase);
            throw new BusinessException("El camión ya existe con id=" + camion.getId());
        } catch (NotFoundException e) {
            // No existe, crearlo
        }

        if (camionCli1DAO.findOneByIdCli1(camion.getIdCli1()).isPresent()) {
            throw new BusinessException("Ya existe el camión CLI1 con idCli1=" + camion.getIdCli1());
        }

        try {
            return camionCli1DAO.save(camion);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al crear camión CLI1");
        }
    }

    @Override
    @Transactional
    public Camion loadOrCreate(CamionCli1 camion) throws BusinessException, NotFoundException {
        Optional<Camion> camionBase = Optional.empty();

        // Intentar buscar por patente
        try {
            camionBase = Optional.ofNullable(camionBaseBusiness.load(camion.getPatente()));
        } catch (NotFoundException ignored) {
        }

        // Si NO existe, crear nuevo
        if (camionBase.isEmpty()) {
            try {
                return add(camion);
            } catch (BusinessException e) {
                try {
                    return camionBaseBusiness.load(camion.getPatente());
                } catch (NotFoundException ex) {
                    throw new BusinessException("Error al crear/cargar camión: " + e.getMessage());
                }
            }
        }

        // Si existe, solo crear mapeo CLI1
        mapperEntity.map(camion, camionBase.get());
        return camionBase.get();
    }
}