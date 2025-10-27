package com.tpfinal.iw3.integration.cli1.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpfinal.iw3.integration.cli1.model.ChoferCli1;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.IChoferCli1Business;
import com.tpfinal.iw3.integration.cli1.model.persistence.ChoferCli1Repository;
import com.tpfinal.iw3.integration.cli1.util.MapperEntity;
import com.tpfinal.iw3.model.Chofer;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IChoferBusiness;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChoferCli1Business implements IChoferCli1Business {

    @Autowired
    private ChoferCli1Repository choferCli1DAO;

    @Autowired
    private IChoferBusiness choferBaseBusiness;

    @Autowired
    private MapperEntity mapperEntity;

    @Override
    public ChoferCli1 load(String idCli1) throws NotFoundException, BusinessException {
        Optional<ChoferCli1> chofer;
        try {
            chofer = choferCli1DAO.findOneByIdCli1(idCli1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al buscar chofer CLI1");
        }
        if (chofer.isEmpty()) {
            throw new NotFoundException("No se encuentra el chofer CLI1 con idCli1=" + idCli1);
        }
        return chofer.get();
    }

    @Override
    public List<ChoferCli1> list() throws BusinessException {
        try {
            return choferCli1DAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al listar choferes CLI1");
        }
    }

    @Override
    @Transactional
    public ChoferCli1 add(ChoferCli1 chofer) throws BusinessException {
        try {
            // Buscar si ya existe el chofer base por documento
            Chofer choferBase = choferBaseBusiness.load(chofer.getDocumento());
            mapperEntity.map(chofer, choferBase);
            throw new BusinessException("El chofer ya existe con id=" + chofer.getId());
        } catch (NotFoundException e) {
            // No existe, crearlo
        }

        if (choferCli1DAO.findOneByIdCli1(chofer.getIdCli1()).isPresent()) {
            throw new BusinessException("Ya existe el chofer CLI1 con idCli1=" + chofer.getIdCli1());
        }

        try {
            return choferCli1DAO.save(chofer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al crear chofer CLI1");
        }
    }

    @Override
    @Transactional
    public Chofer loadOrCreate(ChoferCli1 chofer) throws BusinessException, NotFoundException {
        Optional<Chofer> choferBase = Optional.empty();

        // Intentar buscar por documento
        try {
            choferBase = Optional.ofNullable(choferBaseBusiness.load(chofer.getDocumento()));
        } catch (NotFoundException ignored) {
        }

        // Si NO existe, crear nuevo
        if (choferBase.isEmpty()) {
            try {
                return add(chofer);
            } catch (BusinessException e) {
                try {
                    return choferBaseBusiness.load(chofer.getDocumento());
                } catch (NotFoundException ex) {
                    throw new BusinessException("Error al crear/cargar chofer: " + e.getMessage());
                }
            }
        }

        // Si existe, solo crear mapeo CLI1
        mapperEntity.map(chofer, choferBase.get());
        return choferBase.get();
    }
}