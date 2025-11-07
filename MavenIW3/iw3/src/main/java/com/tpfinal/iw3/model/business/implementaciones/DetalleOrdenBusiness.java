package com.tpfinal.iw3.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpfinal.iw3.model.DetalleOrden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IDetalleOrdenBusiness;
import com.tpfinal.iw3.model.persistence.DetalleOrdenRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DetalleOrdenBusiness implements IDetalleOrdenBusiness{

    @Autowired
    private DetalleOrdenRepository detalleOrdenDAO;

    @Override
    public DetalleOrden load(long id) throws NotFoundException, BusinessException {
        Optional<DetalleOrden> detailFound;

        try {
            detailFound = detalleOrdenDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (detailFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Detalle id= " + id).build();
        return detailFound.get();

    }

    @Override
    public DetalleOrden add(DetalleOrden detail) throws DuplicateException, BusinessException {
        try {
            load(detail.getId());
            throw DuplicateException.builder().message("Ya existe el detalle id = " + detail.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return detalleOrdenDAO.save(detail);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Crear Nuevo Detalle").build();
        }

    }

    // lista todos los detalles de una orden
    @Override
    public List<DetalleOrden> listByOrder(long idOrder) throws NotFoundException, BusinessException {
        Optional<List<DetalleOrden>> detailsFound;

        try {
            detailsFound = detalleOrdenDAO.findByOrdenId(idOrder);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (detailsFound.isEmpty())
            throw NotFoundException.builder().message("Orden sin detalles = " + idOrder).build();
        return detailsFound.get();
    }

    @Override
    public Float calculateAverageTemperature(Long orderId) {
        Double avgTemp = detalleOrdenDAO.findAverageTemperatureByOrderId(orderId);
        return avgTemp != null ? avgTemp.floatValue() : 0.0f;
    }

    @Override
    public Float calculateAverageDensity(Long orderId) {
        Double avgDensity = detalleOrdenDAO.findAverageDensityByOrderId(orderId);
        return avgDensity != null ? avgDensity.floatValue() : 0.0f;
    }

    @Override
    public Float calculateAverageFlowRate(Long orderId) {
        var avgFlow = detalleOrdenDAO.findAverageFlowRateByOrderId(orderId);
        return avgFlow != null ? avgFlow.floatValue() : 0.0f;
    }

}
