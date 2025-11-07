package com.tpfinal.iw3.integration.cli3.model.business.interfaces;

import com.tpfinal.iw3.model.DetalleOrden;
import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.ConflictException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

/**
 * Interfaz de negocio para CLI3 (Sistema de Control de Carga).
 * 
 * CLI3 es responsable del Punto 3: activación de carga, recepción de datos en tiempo real, y cierre.
 * 
 * FLUJO COMPLETO:
 * 1. validatePassword() → Activar carga con contraseña (CON_PESAJE_INICIAL → CERRADA_PARA_CARGAR)
 * 2. receiveDetails() → Recibir y almacenar datos de carga (bucle continuo)
 * 3. closeOrder() → Cerrar orden y finalizar proceso (CERRADA_PARA_CARGAR → FINALIZADA)
 */
public interface IOrdenCli3Business {

    /**
     * PUNTO 3.1: Valida la contraseña de activación y activa la carga.
     * 
     * Validaciones:
     * - La contraseña debe existir en la BD
     * - El estado de la orden debe ser CON_PESAJE_INICIAL
     * 
     * Cambios:
     * - Cambia estado a CERRADA_PARA_CARGAR
     * - Registra fechaInicioCarga
     * 
     * @param password Contraseña de 5 dígitos generada en Punto 2
     * @return Orden con id, password, preset (para enviar a dispositivo CLI3)
     * @throws NotFoundException Si no existe orden con esa contraseña
     * @throws BusinessException Si hay errores de persistencia
     * @throws ConflictException Si el estado de la orden no es CON_PESAJE_INICIAL
     */
    Orden validatePassword(Integer password) 
            throws NotFoundException, BusinessException, ConflictException;

    /**
     * PUNTO 3.2: Recibe y almacena datos de carga en tiempo real.
     * 
     * Este método se invoca repetidamente mientras el dispositivo CLI3
     * envía datos del proceso de carga (masa, densidad, temperatura, caudal).
     * 
     * Validaciones:
     * - Estado de la orden debe ser CERRADA_PARA_CARGAR
     * - masaAcomulada debe ser >= última masa registrada (monotónica creciente)
     * - caudal debe ser >= 0
     * 
     * Acciones:
     * - Almacena el detalle en BD (tabla detalle_orden)
     * - Actualiza últimos valores en cabecera de orden
     * - Actualiza ultimaActualizacion con timestamp actual
     * 
     * @param detalle Detalle de carga con: masaAcomulada, densidad, temperatura, caudal, estampaTiempo
     * @return Orden actualizada
     * @throws NotFoundException Si no se encuentra la orden
     * @throws BusinessException Si hay errores de persistencia
     * @throws ConflictException Si el estado no es CERRADA_PARA_CARGAR o datos inválidos
     */
    Orden receiveDetails(DetalleOrden detalle) 
            throws NotFoundException, BusinessException, ConflictException;

    /**
     * PUNTO 3.3: Cierra la orden y finaliza el proceso de carga.
     * 
     * Este método se invoca cuando el dispositivo CLI3 detecta que la carga
     * ha finalizado (masa acumulada >= preset, o señal manual de fin).
     * 
     * Validaciones:
     * - La contraseña debe existir en la BD
     * - Estado debe ser CON_PESAJE_INICIAL (durante la carga)
     * 
     * Acciones:
     * - Cambia estado a CERRADA_PARA_CARGAR
     * - Registra fechaFinCarga
     * - Limpia contraActivacion = null (seguridad, evita reutilización)
     * 
     * @param password Contraseña de 5 dígitos (misma que se usó para validar)
     * @return Orden cerrada
     * @throws NotFoundException Si no se encuentra la orden con esa password
     * @throws BusinessException Si hay errores de persistencia
     * @throws ConflictException Si el estado no es CON_PESAJE_INICIAL
     */
    Orden closeOrder(Integer password) 
            throws BusinessException, NotFoundException, ConflictException;
}