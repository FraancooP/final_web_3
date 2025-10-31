package com.tpfinal.iw3.integration.cli1.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpfinal.iw3.integration.cli1.model.ClienteCli1;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.IClienteCli1Business;
import com.tpfinal.iw3.integration.cli1.model.persistence.ClienteCli1Repository;
import com.tpfinal.iw3.integration.cli1.util.MapperEntity;
import com.tpfinal.iw3.model.Cliente;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IClienteBusiness;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClienteCli1Business implements IClienteCli1Business {

    @Autowired
    private ClienteCli1Repository clienteCli1DAO;

    @Autowired
    private IClienteBusiness clienteBaseBusiness;

    @Autowired
    private MapperEntity mapperEntity;

    @Override
    public ClienteCli1 load(String idCli1) throws NotFoundException, BusinessException {
        Optional<ClienteCli1> cliente;
        try {
            cliente = clienteCli1DAO.findOneByIdCli1(idCli1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al buscar cliente CLI1");
        }
        if (cliente.isEmpty()) {
            throw new NotFoundException("No se encuentra el cliente CLI1 con idCli1=" + idCli1);
        }
        return cliente.get();
    }

    @Override
    public List<ClienteCli1> list() throws BusinessException {
        try {
            return clienteCli1DAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al listar clientes CLI1");
        }
    }

    @Override
    @Transactional
    public ClienteCli1 add(ClienteCli1 cliente) throws BusinessException {
        try {
            // Buscar si ya existe el cliente base por razón social (no por código externo)
            Cliente clienteBase = clienteBaseBusiness.loadByRazonSocial(cliente.getRazonSocial());
            // Si existe, solo crear el mapeo
            mapperEntity.map(cliente, clienteBase);
            throw new BusinessException("El cliente ya existe con id=" + cliente.getId());
        } catch (NotFoundException e) {
            // No existe, crearlo
        }

        // Verificar que no exista el mapeo CLI1
        if (clienteCli1DAO.findOneByIdCli1(cliente.getIdCli1()).isPresent()) {
            throw new BusinessException("Ya existe el cliente CLI1 con idCli1=" + cliente.getIdCli1());
        }

        try {
            return clienteCli1DAO.save(cliente);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al crear cliente CLI1");
        }
    }

    @Override
    @Transactional
    public Cliente loadOrCreate(ClienteCli1 cliente) throws BusinessException, NotFoundException {
        Optional<Cliente> clienteBase = Optional.empty();

        // 1. Intentar buscar cliente base por razón social
        try {
            clienteBase = Optional.ofNullable(clienteBaseBusiness.loadByRazonSocial(cliente.getRazonSocial()));
        } catch (NotFoundException ignored) {
        }

        // 2. Si NO existe, crear nuevo
        if (clienteBase.isEmpty()) {
            try {
                return add(cliente);
            } catch (BusinessException e) {
                // Si falla, intentar cargarlo de nuevo
                try {
                    return clienteBaseBusiness.loadByRazonSocial(cliente.getRazonSocial());
                } catch (NotFoundException ex) {
                    throw new BusinessException("Error al crear/cargar cliente: " + e.getMessage());
                }
            }
        }

        // 3. Si existe, solo crear mapeo CLI1
        mapperEntity.map(cliente, clienteBase.get());
        return clienteBase.get();
    }
}