package com.tpfinal.iw3.integration.cli2.model.business.interfaces;

import com.tpfinal.iw3.model.Conciliacion;
import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.ConciliacionException;
import com.tpfinal.iw3.model.business.excepciones.ConflictException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

/**
 * Interfaz de negocio para CLI2 (Sistema de Balanza/Pesaje).
 * 
 * CLI2 es responsable de:
 * - Punto 2: registro del pesaje inicial (tara del camión)
 * - Punto 5: registro del pesaje final y generación de conciliación
 * 
 * Flujo completo:
 * 1. Camión llega → CLI2 registra pesaje inicial (tara)
 * 2. Camión carga producto → CLI3 registra datos de carga
 * 3. Carga finalizada → CLI2 registra pesaje final y genera conciliación
 */
public interface IOrdenCli2Business {

    /**
     * PUNTO 2: Registra el pesaje inicial (tara) de una orden.
     * 
     * @param patente Patente del camión que está siendo pesado
     * @param pesoTara Peso de la tara en kilogramos
     * @return La orden actualizada con tara, contraseña y nuevo estado
     * @throws NotFoundException Si no existe orden pendiente con esa patente
     * @throws BusinessException Si hay errores de validación o persistencia
     */
    Orden registerInitialWeighing(String patente, Double pesoTara) 
            throws NotFoundException, BusinessException;
    
    /**
     * PUNTO 5: Registra el pesaje final y cambia orden a estado FINALIZADA.
     * 
     * @param patente Patente del camión pesado
     * @param pesoFinal Peso final del camión con carga (kg)
     * @return Conciliación con todos los cálculos
     * @throws NotFoundException Si no existe orden para esa patente
     * @throws ConflictException Si la orden no está en estado CERRADA_PARA_CARGAR
     * @throws BusinessException Si hay errores de cálculo o persistencia
     */
    Conciliacion registerFinalWeighing(String patente, Double pesoFinal) 
            throws NotFoundException, ConflictException, BusinessException, ConciliacionException;
    
    /**
     * PUNTO 5: Obtiene la conciliación de una orden finalizada.
     * Solo disponible para órdenes en estado FINALIZADA.
     * 
     * @param ordenId ID de la orden
     * @return Conciliación con todos los cálculos
     * @throws NotFoundException Si no existe la orden
     * @throws ConflictException Si la orden no está FINALIZADA
     * @throws ConciliacionException Si faltan datos para calcular la conciliación
     * @throws BusinessException Si hay errores al obtener datos
     */
    Conciliacion getReconciliation(Long ordenId) 
            throws NotFoundException, ConflictException, ConciliacionException, BusinessException;
}
