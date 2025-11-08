package com.tpfinal.iw3.integration.cli1.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpfinal.iw3.integration.cli1.model.CamionCli1;
import com.tpfinal.iw3.integration.cli1.model.ChoferCli1;
import com.tpfinal.iw3.integration.cli1.model.CisternaCli1;
import com.tpfinal.iw3.integration.cli1.model.ClienteCli1;
import com.tpfinal.iw3.integration.cli1.model.ProductoCli1;
import com.tpfinal.iw3.integration.cli1.model.persistence.CamionCli1Repository;
import com.tpfinal.iw3.integration.cli1.model.persistence.ChoferCli1Repository;
import com.tpfinal.iw3.integration.cli1.model.persistence.CisternaCli1Repository;
import com.tpfinal.iw3.integration.cli1.model.persistence.ClienteCli1Repository;
import com.tpfinal.iw3.integration.cli1.model.persistence.ProductoCli1Repository;
import com.tpfinal.iw3.model.Camion;
import com.tpfinal.iw3.model.Chofer;
import com.tpfinal.iw3.model.Cisterna;
import com.tpfinal.iw3.model.Cliente;
import com.tpfinal.iw3.model.Producto;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IProductoBusiness;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MapperEntity {

    @Autowired
    private ClienteCli1Repository clienteCli1DAO;

    @Autowired
    private ChoferCli1Repository choferCli1DAO;

    @Autowired
    private CamionCli1Repository camionCli1DAO;

    @Autowired
    private ProductoCli1Repository productoCli1DAO;

    @Autowired
    private CisternaCli1Repository cisternaCli1DAO;

    @Autowired
    private IProductoBusiness productoBaseBusiness;



    /**
     * Mapea ProductoCli1 con Producto Base.
     * 
     * IMPORTANTE: El producto DEBE existir previamente en la BD.
     * No se crean productos automáticamente para mantener control sobre el catálogo.
     * 
     * Flujo:
     * 1. Verificar si ya existe el mapeo CLI1 → Producto
     * 2. Si existe, retornar el producto mapeado
     * 3. Si NO existe mapeo, buscar producto base por nombre
     * 4. Si el producto NO existe → LANZA NotFoundException (rechaza la orden)
     * 5. Si existe, crear el mapeo y retornar
     * 
     * @throws NotFoundException Si el producto no existe en la BD
     * @throws BusinessException Si hay error en el mapeo
     */
    @Transactional
    public Producto map(ProductoCli1 productoCli1) throws BusinessException, NotFoundException {

        // 1. Verificar si ya está mapeado
        Optional<ProductoCli1> productoExistente = productoCli1DAO.findOneByIdCli1(productoCli1.getIdCli1());
        if (productoExistente.isPresent()) {
            // Si ya existe el mapeo, retornar el producto base asociado
            ProductoCli1 productoCli1Existente = productoExistente.get();
            log.debug("Producto CLI1 ya mapeado: idCli1={}, productoId={}", 
                     productoCli1.getIdCli1(), productoCli1Existente.getId());
            return productoBaseBusiness.load(productoCli1Existente.getId());
        }

        // 2. Buscar producto base por nombre - DEBE EXISTIR
        Producto productoBase;
        try {
            productoBase = productoBaseBusiness.load(productoCli1.getNombre());
            log.info("Producto base encontrado: nombre='{}', id={}", 
                    productoBase.getNombre(), productoBase.getId());
        } catch (NotFoundException nf) {
            // NO auto-crear: el producto debe existir previamente en la BD
            log.error("Producto NO encontrado en BD: nombre='{}', idCli1='{}'", 
                     productoCli1.getNombre(), productoCli1.getIdCli1());
            throw new NotFoundException(
                "El producto '" + productoCli1.getNombre() + "' no existe en el sistema. " +
                "Por favor, cree el producto antes de enviar órdenes con este producto."
            );
        }

        // 3. Crear el mapeo CLI1 → Producto
        try {
            productoCli1.setId(productoBase.getId());
            productoCli1.setNombre(productoBase.getNombre());
            productoCli1.setDescripcion(productoBase.getDescripcion());
            productoCli1.setCodigoExterno(productoBase.getCodigoExterno());
            productoCli1DAO.insertProductoCli1(productoBase.getId(), productoCli1.getIdCli1());
            
            log.info("Mapeo creado exitosamente: ProductoCli1 idCli1='{}' → Producto id={}", 
                    productoCli1.getIdCli1(), productoBase.getId());
            
            return productoBase;
        } catch (Exception e) {
            log.error("Error al mapear producto CLI1: {}", e.getMessage(), e);
            throw new BusinessException("Error al crear mapeo de producto CLI1");
        }
    }

    //Mapea de ClienteCli1
    @Transactional
    public void map(ClienteCli1 clienteCli1, Cliente cliente) throws BusinessException {
        if (clienteCli1DAO.findOneByIdCli1(clienteCli1.getIdCli1()).isEmpty()) {
            try {
                clienteCli1DAO.insertClienteCli1(cliente.getId(), clienteCli1.getIdCli1());
            } catch (Exception e) {
                log.error("Error al mapear cliente CLI1", e);
                throw new BusinessException("Error al mapear cliente CLI1");
            }
        }
    }
     /**
     * Mapea ChoferCli1 → Chofer base
     */
    @Transactional
    public void map(ChoferCli1 choferCli1, Chofer chofer) throws BusinessException {
        if (choferCli1DAO.findOneByIdCli1(choferCli1.getIdCli1()).isEmpty()) {
            try {
                choferCli1DAO.insertChoferCli1(chofer.getId(), choferCli1.getIdCli1());
            } catch (Exception e) {
                log.error("Error al mapear chofer CLI1", e);
                throw new BusinessException("Error al mapear chofer CLI1");
            }
        }
    }
    /**
     * Mapea CamionCli1 → Camion base
     */
    @Transactional
    public void map(CamionCli1 camionCli1, Camion camion) throws BusinessException {
        if (camionCli1DAO.findOneByIdCli1(camionCli1.getIdCli1()).isEmpty()) {
            try {
                camionCli1DAO.insertCamionCli1(camion.getId(), camionCli1.getIdCli1());
            } catch (Exception e) {
                log.error("Error al mapear camión CLI1", e);
                throw new BusinessException("Error al mapear camión CLI1");
            }
        }
    }
    @Transactional
    public void map(CisternaCli1 cisternaCli1, Cisterna cisterna) throws BusinessException {
        if (cisternaCli1DAO.findOneByIdCli1(cisternaCli1.getIdCli1()).isEmpty()) {
            try {
                cisternaCli1DAO.insertCisternaCli1(cisterna.getId(), cisternaCli1.getIdCli1());
            } catch (Exception e) {
                log.error("Error al mapear cisterna CLI1", e);
                throw new BusinessException("Error al mapear cisterna CLI1");
            }
        }
    }
}
