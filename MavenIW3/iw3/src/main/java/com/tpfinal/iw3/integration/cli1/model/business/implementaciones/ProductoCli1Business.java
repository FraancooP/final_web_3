package com.tpfinal.iw3.integration.cli1.model.business.implementaciones;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpfinal.iw3.integration.cli1.model.ProductoCli1;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.IProductoCli1Business;
import com.tpfinal.iw3.integration.cli1.model.persistence.ProductoCli1Repository;
import com.tpfinal.iw3.integration.cli1.util.MapperEntity;
import com.tpfinal.iw3.model.Producto;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductoCli1Business implements IProductoCli1Business {

    @Autowired
    private ProductoCli1Repository productoCli1DAO;

    @Autowired
    private MapperEntity mapperEntity;

    @Override
    public ProductoCli1 load(String idCli1) throws NotFoundException, BusinessException {
        Optional<ProductoCli1> producto;
        try {
            producto = productoCli1DAO.findOneByIdCli1(idCli1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al buscar producto CLI1");
        }
        if (producto.isEmpty()) {
            throw new NotFoundException("No se encuentra el producto CLI1 con idCli1=" + idCli1);
        }
        return producto.get();
    }

    @Override
    @Transactional
    public Producto load(ProductoCli1 productoCli1) throws BusinessException, NotFoundException {
        return mapperEntity.map(productoCli1);
    }
}