package com.tpfinal.iw3.integration.cli1.util;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tpfinal.iw3.integration.cli1.model.*;
import com.tpfinal.iw3.integration.cli1.model.persistence.*;
import com.tpfinal.iw3.model.*;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.*;
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



    //Mapeamos ProductoCli1 con Producto Base

    @Transactional
    public Producto map(ProductoCli1 productoCli1) throws BusinessException, NotFoundException{

        //Verificamos si ya esta mapeado
        Optional<ProductoCli1> productoExistente = productoCli1DAO.findOneByIdCli1(productoCli1.getIdCli1());
        if (productoExistente.isPresent()) {
            return productoExistente.get();
        }

        //Busca producto base por nombre
        Producto productoBase = productoBaseBusiness.load(productoCli1.getNombre());



        try {
            productoCli1.setId(productoBase.getId());
            productoCli1.setNombre(productoBase.getNombre());
            productoCli1.setDescripcion(productoBase.getDescripcion());
            productoCli1.setCodigoExterno(productoBase.getCodigoExterno());
            productoCli1DAO.insertProductoCli1(productoBase.getId(), productoCli1.getIdCli1());
            return productoBase;
        } catch (Exception e) {
            log.error("Error al mapear producto CLI1", e);
            throw new BusinessException("Error al mapear producto CLI1");
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
