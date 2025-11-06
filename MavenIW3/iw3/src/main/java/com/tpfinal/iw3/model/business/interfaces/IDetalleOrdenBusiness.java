package com.tpfinal.iw3.model.business.interfaces;

import java.util.List;

import com.tpfinal.iw3.model.DetalleOrden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface IDetalleOrdenBusiness {

    public DetalleOrden load(long id) throws NotFoundException, BusinessException;

    public List<DetalleOrden> listByOrder(long idOrder) throws NotFoundException, BusinessException;

    DetalleOrden add(DetalleOrden detail) throws DuplicateException, BusinessException;

    /**
     * Calcula el promedio de temperatura de los detalles de una orden.
     * Usado para la conciliación (Punto 5).
     */
    Float calculateAverageTemperature(Long orderId);

    /**
     * Calcula el promedio de densidad de los detalles de una orden.
     * Usado para la conciliación (Punto 5).
     */
    Float calculateAverageDensity(Long orderId);

    /**
     * Calcula el promedio de caudal de los detalles de una orden.
     * Usado para la conciliación (Punto 5).
     */
    Float calculateAverageFlowRate(Long orderId);
}
