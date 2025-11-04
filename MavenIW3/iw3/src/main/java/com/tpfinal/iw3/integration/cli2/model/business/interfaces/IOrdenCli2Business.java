package com.tpfinal.iw3.integration.cli2.model.business.interfaces;

import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

/**
 * Interfaz de negocio para CLI2 (Sistema de Balanza/Pesaje).
 * 
 * CLI2 es responsable del Punto 2: registro del pesaje inicial (tara del camión).
 * Cuando el camión llega a la balanza, CLI2:
 * 1. Busca la orden PENDIENTE_PESAJE_INICIAL por patente
 * 2. Registra el peso de la tara
 * 3. Genera una contraseña de activación única de 5 dígitos
 * 4. Cambia el estado a CON_PESAJE_INICIAL
 * 5. Retorna la contraseña al chofer
 */
public interface IOrdenCli2Business {

    /**
     * Registra el pesaje inicial (tara) de una orden.
     * 
     * @param patente Patente del camión que está siendo pesado
     * @param pesoTara Peso de la tara en kilogramos
     * @return La orden actualizada con tara, contraseña y nuevo estado
     * @throws NotFoundException Si no existe orden pendiente con esa patente
     * @throws BusinessException Si hay errores de validación o persistencia
     */
    Orden registerInitialWeighing(String patente, Double pesoTara) 
            throws NotFoundException, BusinessException;
}
