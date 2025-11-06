package com.tpfinal.iw3.integration.cli1.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tpfinal.iw3.integration.cli1.model.OrdenCli1;
import com.tpfinal.iw3.integration.cli1.model.OrdenCli1JsonDeserializer;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.ICamionCli1Business;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.IChoferCli1Business;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.ICisternaCli1Business;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.IClienteCli1Business;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.IOrdenCli1Business;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.IProductoCli1Business;
import com.tpfinal.iw3.integration.cli1.model.persistence.OrdenCli1Repository;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenCli1Business implements IOrdenCli1Business {

    @Autowired
    private OrdenCli1Repository ordenCli1DAO;

    @Autowired
    private IClienteCli1Business clienteCli1Business;

    @Autowired
    private IChoferCli1Business choferCli1Business;

    @Autowired
    private ICamionCli1Business camionCli1Business;

    @Autowired
    private ICisternaCli1Business cisternaCli1Business;

    @Autowired
    private IProductoCli1Business productoCli1Business;

    @Override
    public OrdenCli1 load(String numeroOrdenCli1) throws NotFoundException, BusinessException {
        Optional<OrdenCli1> orden;
        try {
            orden = ordenCli1DAO.findOneByNumeroOrdenCli1(numeroOrdenCli1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al buscar orden CLI1");
        }
        if (orden.isEmpty()) {
            throw new NotFoundException("No se encuentra la orden CLI1 con numero=" + numeroOrdenCli1);
        }
        return orden.get();
    }

    @Override
    public List<OrdenCli1> list() throws BusinessException {
        try {
            return ordenCli1DAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al listar órdenes CLI1");
        }
    }

    @Override
    @Transactional
    public OrdenCli1 add(OrdenCli1 orden) throws BusinessException {
        // Verificar que no exista ya
        if (ordenCli1DAO.findOneByNumeroOrdenCli1(orden.getNumeroOrdenCli1()).isPresent()) {
            throw new BusinessException("Ya existe la orden CLI1 con numero=" + orden.getNumeroOrdenCli1());
        }

        try {
            // IMPORTANTE: Convertir numeroOrdenCli1 (String) a numeroOrden (Integer) de la clase padre
            // para evitar violación de constraint única
            try {
                Integer numeroOrden = Integer.parseInt(orden.getNumeroOrdenCli1());
                orden.setNumeroOrden(numeroOrden);
            } catch (NumberFormatException e) {
                // Si numeroOrdenCli1 no es numérico, usar hashCode como alternativa
                orden.setNumeroOrden(orden.getNumeroOrdenCli1().hashCode());
            }
            
            return ordenCli1DAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al crear orden CLI1: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrdenCli1 addExternal(String json) throws BusinessException {
        log.info("Recibiendo orden externa CLI1");

        // Crear ObjectMapper con deserializador custom
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(OrdenCli1.class, new OrdenCli1JsonDeserializer(
                OrdenCli1.class,
                clienteCli1Business,
                choferCli1Business,
                camionCli1Business,
                cisternaCli1Business,
                productoCli1Business
        ));
        mapper.registerModule(module);

        try {
            // Deserializar JSON → OrdenCli1 (con todas las entidades sincronizadas)
            OrdenCli1 orden = mapper.readValue(json, OrdenCli1.class);

            log.info("Orden deserializada: {}", orden.getNumeroOrdenCli1());

            // Guardar orden
            return add(orden);

        } catch (Exception e) {
            log.error("Error al procesar orden externa CLI1", e);
            throw new BusinessException("Error al procesar orden externa CLI1: " + e.getMessage());
        }
    }
}