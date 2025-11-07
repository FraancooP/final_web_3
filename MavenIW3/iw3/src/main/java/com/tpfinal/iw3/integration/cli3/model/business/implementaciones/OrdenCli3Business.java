package com.tpfinal.iw3.integration.cli3.model.business.implementaciones;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpfinal.iw3.integration.cli3.model.business.interfaces.IOrdenCli3Business;
import com.tpfinal.iw3.model.DetalleOrden;
import com.tpfinal.iw3.model.EstadoOrden;
import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.ConflictException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.persistence.DetalleOrdenRepository;
import com.tpfinal.iw3.model.persistence.OrdenRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de la lógica de negocio para CLI3 (Sistema de Control de Carga).
 * 
 * FLUJO DEL PUNTO 3 (según consigna):
 * 1. Chofer ingresa contraseña de 5 dígitos en dispositivo CLI3
 * 2. Sistema valida contraseña (estado se MANTIENE en CON_PESAJE_INICIAL)
 * 3. Dispositivo CLI3 envía datos periódicos de carga (masa, densidad, temp, caudal)
 * 4. Sistema almacena TODOS los detalles conforme se reciben
 * 5. Al finalizar, dispositivo CLI3 cierra orden (estado cambia a CERRADA_PARA_CARGAR)
 * 
 * ESTADOS:
 * - Antes de validar password: CON_PESAJE_INICIAL
 * - Después de validar password: CON_PESAJE_INICIAL (NO cambia)
 * - Durante la carga (envío detalles): CON_PESAJE_INICIAL
 * - Después de cerrar orden: CERRADA_PARA_CARGAR
 * - Después de pesaje final (Punto 5): FINALIZADA
 * 
 * IMPORTANTE: La orden NO pasa a FINALIZADA en CLI3.closeOrder().
 * El estado FINALIZADA se alcanza en el Punto 5 (pesaje final - CLI2).
 * 
 * NOTA: Por el momento se almacenan TODOS los registros que llegan.
 * La configuración de frecuencia de almacenamiento es opcional y se implementará después.
 */
@Service
@Slf4j
public class OrdenCli3Business implements IOrdenCli3Business {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    /**
     * PUNTO 3.1: Valida contraseña y habilita la carga.
     * 
     * Flujo:
     * 1. Buscar orden por contraseña
     * 2. Validar estado = CON_PESAJE_INICIAL
     * 3. NO cambia el estado (se mantiene en CON_PESAJE_INICIAL)
     * 4. Registrar fechaInicioCarga
     * 5. Retornar datos para dispositivo CLI3 (id, password, preset)
     * 
     * IMPORTANTE: El estado NO cambia aquí. 
     * Se mantiene en CON_PESAJE_INICIAL hasta que se cierre la orden.
     */
    @Override
    @Transactional
    public Orden validatePassword(Integer password) 
            throws NotFoundException, BusinessException, ConflictException {
        
        log.info("CLI3: Validando contraseña de activación: {}", password);

        // 1. Buscar orden por contraseña
        Optional<Orden> ordenOpt;
        try {
            ordenOpt = ordenRepository.findByContraActivacion(password);
        } catch (Exception e) {
            log.error("CLI3: Error al buscar orden por contraseña: {}", e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden por contraseña", e);
        }

        if (ordenOpt.isEmpty()) {
            log.warn("CLI3: No se encontró orden con contraseña: {}", password);
            throw new NotFoundException("No se encontró orden con la contraseña proporcionada");
        }

        Orden orden = ordenOpt.get();
        log.info("CLI3: Orden encontrada: id={}, numeroOrden={}, estado={}", 
                 orden.getId(), orden.getNumeroOrden(), orden.getEstado());

        // 2. Validar estado
        if (orden.getEstado() != EstadoOrden.CON_PESAJE_INICIAL) {
            log.error("CLI3: Estado inválido. Esperado=CON_PESAJE_INICIAL, Actual={}", orden.getEstado());
            throw new ConflictException(
                "Estado de orden inválido. Esperado: CON_PESAJE_INICIAL, Actual: " + orden.getEstado()
            );
        }

        // 3. NO cambiar estado - se mantiene en CON_PESAJE_INICIAL
        // El cambio a CERRADA_PARA_CARGAR ocurre en closeOrder()
        
        // 4. Registrar inicio de carga
        orden.setFechaInicioCarga(LocalDateTime.now());

        // 5. Persistir cambios
        try {
            orden = ordenRepository.save(orden);
            log.info("CLI3: Contraseña validada exitosamente. Carga habilitada para orden={}, estado={}", 
                     orden.getId(), orden.getEstado());
        } catch (Exception e) {
            log.error("CLI3: Error al activar carga: {}", e.getMessage(), e);
            throw new BusinessException("Error al activar carga", e);
        }

        return orden;
    }

    /**
     * PUNTO 3.2: Recibe y almacena datos de carga en tiempo real.
     * 
     * Flujo:
     * 1. Buscar orden por ID
     * 2. Validar estado = CON_PESAJE_INICIAL (durante la carga activa)
     * 3. Validar datos entrantes (masa creciente, caudal positivo)
     * 4. Guardar detalle en BD (TODOS los registros que llegan)
     * 5. Actualizar últimos valores en cabecera
     * 
     * SIMPLIFICACIÓN: Se almacenan TODOS los registros conforme se reciben.
     * La configuración de frecuencia se implementará después.
     */
    @Override
    @Transactional
    public Orden receiveDetails(DetalleOrden detalle) 
            throws NotFoundException, BusinessException, ConflictException {
        
        long currentTime = System.currentTimeMillis();
        
        log.debug("CLI3: Recibiendo detalle de carga: ordenId={}, masa={}, densidad={}, temp={}, caudal={}", 
                  detalle.getOrden().getId(), detalle.getMasaAcomulada(), 
                  detalle.getDensidad(), detalle.getTemperatura(), detalle.getCaudal());

        // 1. Buscar orden
        Long ordenId = detalle.getOrden().getId();
        Optional<Orden> ordenOpt;
        try {
            ordenOpt = ordenRepository.findById(ordenId);
        } catch (Exception e) {
            log.error("CLI3: Error al buscar orden id={}: {}", ordenId, e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        }

        if (ordenOpt.isEmpty()) {
            log.error("CLI3: No se encontró orden con id={}", ordenId);
            throw new NotFoundException("Orden no encontrada con id: " + ordenId);
        }

        Orden orden = ordenOpt.get();

        // 2. Validar estado - debe estar en CON_PESAJE_INICIAL (carga activa)
        if (orden.getEstado() != EstadoOrden.CON_PESAJE_INICIAL) {
            log.error("CLI3: Estado inválido. Esperado=CON_PESAJE_INICIAL, Actual={}", orden.getEstado());
            throw new ConflictException(
                "Estado de orden inválido. Esperado: CON_PESAJE_INICIAL, Actual: " + orden.getEstado()
            );
        }

        // 3. Validar caudal (debe ser > 0, descartamos si es <= 0)
        if (detalle.getCaudal() <= 0) {
            log.warn("CLI3: Caudal inválido ({} kg/h), descartando registro para orden={}", 
                     detalle.getCaudal(), ordenId);
            throw new ConflictException("Caudal inválido: " + detalle.getCaudal() + ". Debe ser mayor a 0.");
        }

        // 3.1 Validar masa acumulada (debe ser > 0 y creciente)
        if (detalle.getMasaAcomulada() <= 0) {
            log.warn("CLI3: Masa acumulada inválida ({} kg), descartando registro para orden={}", 
                     detalle.getMasaAcomulada(), ordenId);
            throw new ConflictException("Masa acumulada inválida: " + detalle.getMasaAcomulada() + ". Debe ser mayor a 0.");
        }

        // 3.2 Validar que la masa acumulada sea creciente (monotónica)
        if (orden.getUltimaMasaAcomulada() != null) {
            if (detalle.getMasaAcomulada() < orden.getUltimaMasaAcomulada()) {
                log.warn("CLI3: Masa acumulada decreció. Anterior={}, Nueva={}, descartando registro", 
                         orden.getUltimaMasaAcomulada(), detalle.getMasaAcomulada());
                throw new ConflictException(
                    "Masa acumulada inválida. No puede decrecer. Anterior: " + 
                    orden.getUltimaMasaAcomulada() + ", Nueva: " + detalle.getMasaAcomulada()
                );
            }
        }

        // 4. Guardar detalle en BD (se almacenan TODOS los registros)
        detalle.setEstampaTiempo(currentTime);
        detalle.setOrden(orden);
        
        try {
            detalleOrdenRepository.save(detalle);
            log.debug("CLI3: Detalle guardado en BD para orden={}, masa={} kg", 
                     ordenId, detalle.getMasaAcomulada());
        } catch (Exception e) {
            log.error("CLI3: Error al guardar detalle: {}", e.getMessage(), e);
            throw new BusinessException("Error al almacenar detalle de carga", e);
        }

        // 5. Actualizar cabecera con últimos valores válidos
        orden.setUltimaMasaAcomulada((double) detalle.getMasaAcomulada());
        orden.setUltimaDensidad((double) detalle.getDensidad());
        orden.setUltimaTemperatura((double) detalle.getTemperatura());
        orden.setUltimoCaudal((double) detalle.getCaudal());
        orden.setUltimaActualizacion(LocalDateTime.now());
        
        // Actualizar fecha fin carga (última vez que se recibió dato)
        orden.setFechaFinCarga(LocalDateTime.now());

        // Guardar cambios de cabecera
        try {
            orden = ordenRepository.save(orden);
            log.debug("CLI3: Cabecera actualizada: ordenId={}, masa={} kg", 
                     ordenId, orden.getUltimaMasaAcomulada());
        } catch (Exception e) {
            log.error("CLI3: Error al actualizar cabecera: {}", e.getMessage(), e);
            throw new BusinessException("Error al actualizar orden", e);
        }

        return orden;
    }

    /**
     * PUNTO 3.3: Cierra la orden y bloquea recepción de nuevos datos.
     * 
     * Flujo:
     * 1. Buscar orden por CONTRASEÑA (misma que validó y habilitó la carga)
     * 2. Validar estado = CON_PESAJE_INICIAL (durante la carga)
     * 3. Cambiar estado a CERRADA_PARA_CARGAR
     * 4. Registrar fechaFinCarga
     * 5. Limpiar contraseña (seguridad)
     * 
     * IMPORTANTE: Aquí SÍ cambia el estado a CERRADA_PARA_CARGAR.
     * El cambio a FINALIZADA ocurre en el Punto 5 (pesaje final).
     */
    @Override
    @Transactional
    public Orden closeOrder(Integer password) 
            throws BusinessException, NotFoundException, ConflictException {
        
        log.info("CLI3: Cerrando recepción de datos con contraseña: {}", password);

        // 1. Buscar orden por contraseña
        Optional<Orden> ordenOpt;
        try {
            ordenOpt = ordenRepository.findByContraActivacion(password);
        } catch (Exception e) {
            log.error("CLI3: Error al buscar orden con password={}: {}", password, e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        }

        if (ordenOpt.isEmpty()) {
            log.error("CLI3: No se encontró orden con contraseña: {}", password);
            throw new NotFoundException("Orden no encontrada con la contraseña especificada");
        }

        Orden orden = ordenOpt.get();

        // 2. Validar estado - debe estar en CON_PESAJE_INICIAL (carga activa)
        if (orden.getEstado() != EstadoOrden.CON_PESAJE_INICIAL) {
            log.error("CLI3: Estado inválido para cerrar. Esperado=CON_PESAJE_INICIAL, Actual={}", 
                     orden.getEstado());
            throw new ConflictException(
                "Estado de orden inválido para cerrar. Esperado: CON_PESAJE_INICIAL, Actual: " + orden.getEstado()
            );
        }

        // 3. Cambiar estado a CERRADA_PARA_CARGAR
        orden.setEstado(EstadoOrden.CERRADA_PARA_CARGAR);
        log.info("CLI3: Cambiando estado de orden={} a CERRADA_PARA_CARGAR", orden.getId());
        
        // 4. Registrar fechaFinCarga
        orden.setFechaFinCarga(LocalDateTime.now());
        
        // 5. Limpiar contraseña por seguridad (evitar reutilización)
        orden.setContraActivacion(null);

        // 6. Persistir cambios
        try {
            orden = ordenRepository.save(orden);
            log.info("CLI3: Orden cerrada exitosamente: ordenId={}, numeroOrden={}, estado={}", 
                     orden.getId(), orden.getNumeroOrden(), orden.getEstado());
        } catch (Exception e) {
            log.error("CLI3: Error al cerrar orden: {}", e.getMessage(), e);
            throw new BusinessException("Error al cerrar orden", e);
        }

        return orden;
    }
}