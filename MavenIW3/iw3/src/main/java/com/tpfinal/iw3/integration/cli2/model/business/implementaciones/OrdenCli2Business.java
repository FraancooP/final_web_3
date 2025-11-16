package com.tpfinal.iw3.integration.cli2.model.business.implementaciones;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpfinal.iw3.integration.cli2.model.business.interfaces.IOrdenCli2Business;
import com.tpfinal.iw3.model.Conciliacion;
import com.tpfinal.iw3.model.EstadoOrden;
import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.ConciliacionException;
import com.tpfinal.iw3.model.business.excepciones.ConflictException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IOrdenBusiness;
import com.tpfinal.iw3.model.persistence.OrdenRepository;
import com.tpfinal.iw3.util.ActivationPasswordGenerator;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de la lógica de negocio para CLI2 (Sistema de Balanza/Pesaje).
 * 
 * Responsabilidades:
 * - Punto 2: Pesaje inicial (tara del camión)
 * - Punto 5: Pesaje final y conciliación
 * 
 * Flujo completo:
 * 1. Camión llega → CLI2 registra tara
 * 2. Camión carga → CLI3 controla carga
 * 3. Carga finalizada → CLI2 registra pesaje final y genera conciliación
 */
@Service
@Slf4j
public class OrdenCli2Business implements IOrdenCli2Business {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private IOrdenBusiness ordenBusiness;

    /**
     * PUNTO 2: Registra el pesaje inicial (tara) del camión.
     */
    @Override
    @Transactional
    public Orden registerInitialWeighing(String patente, Double pesoTara) 
            throws NotFoundException, BusinessException {
        
        log.info("CLI2: Registrando pesaje inicial para patente={}, peso={} kg", patente, pesoTara);

        // 1. Buscar órdenes PENDIENTE_PESAJE_INICIAL por patente (ordenadas por fecha de recepción)
        List<Orden> ordenes = ordenRepository.findByCamion_PatenteAndEstadoOrderByFechaRecepcionInicialAsc(
            patente, 
            EstadoOrden.PENDIENTE_PESAJE_INICIAL
        );

        if (ordenes.isEmpty()) {
            log.warn("CLI2: No se encontró orden pendiente de pesaje para patente={}", patente);
            throw new NotFoundException(
                "No se encontró orden pendiente de pesaje inicial para el camión con patente: " + patente
            );
        }

        // Tomar la PRIMERA orden (la más antigua por fecha de recepción) - FIFO
        Orden orden = ordenes.get(0);
        
        if (ordenes.size() > 1) {
            log.info("CLI2: Se encontraron {} órdenes pendientes para patente={}. Procesando la más antigua (id={}, numeroOrden={})", 
                     ordenes.size(), patente, orden.getId(), orden.getNumeroOrden());
        } else {
            log.info("CLI2: Orden encontrada id={}, numeroOrden={}", orden.getId(), orden.getNumeroOrden());
        }

        // 2. Validar que la orden esté en el estado correcto
        if (orden.getEstado() != EstadoOrden.PENDIENTE_PESAJE_INICIAL) {
            throw new BusinessException(
                "La orden " + orden.getNumeroOrden() + " no está en estado PENDIENTE_PESAJE_INICIAL"
            );
        }

        // 3. Generar contraseña de activación única
        Integer password = generateUniquePassword();
        log.info("CLI2: Contraseña generada={} para orden={}", password, orden.getNumeroOrden());

        // 4. Actualizar la orden con los datos del pesaje inicial
        orden.setTara(pesoTara);
        orden.setFechaPesajeInicial(LocalDateTime.now());
        orden.setContraActivacion(password);
        orden.setEstado(EstadoOrden.CON_PESAJE_INICIAL);

        // 5. Persistir cambios
        try {
            orden = ordenRepository.save(orden);
            log.info("CLI2: Pesaje inicial registrado exitosamente para orden={}", orden.getNumeroOrden());
            return orden;
        } catch (Exception e) {
            log.error("CLI2: Error al guardar orden con pesaje inicial", e);
            throw new BusinessException("Error al registrar pesaje inicial: " + e.getMessage());
        }
    }

    /**
     * Genera una contraseña de activación única de 5 dígitos.
     * Si la contraseña ya existe en la BD, genera una nueva hasta encontrar una única.
     * 
     * Este loop es importante porque aunque es poco probable que se repitan
     * (90,000 combinaciones posibles), debemos garantizar unicidad.
     * 
     * @return Contraseña única de 5 dígitos
     * @throws BusinessException Si no se puede generar una contraseña única después de muchos intentos
     */
    private Integer generateUniquePassword() throws BusinessException {
        int maxAttempts = 100;
        int attempts = 0;

        while (attempts < maxAttempts) {
            Integer password = ActivationPasswordGenerator.generate();
            
            // Verificar si ya existe en la BD
            Optional<Orden> existingOrder = ordenRepository.findByContraActivacion(password);
            
            if (existingOrder.isEmpty()) {
                // Contraseña única encontrada
                return password;
            }

            attempts++;
            log.debug("CLI2: Contraseña {} ya existe, generando nueva (intento {}/{})", 
                     password, attempts, maxAttempts);
        }

        // Si llegamos aquí, no pudimos generar una contraseña única
        log.error("CLI2: No se pudo generar contraseña única después de {} intentos", maxAttempts);
        throw new BusinessException(
            "No se pudo generar una contraseña de activación única. Por favor, intente nuevamente."
        );
    }

    /**
     * PUNTO 5: Registra el pesaje final y genera conciliación.
     */
    @Override
    @Transactional
    public Conciliacion registerFinalWeighing(String patente, Double pesoFinal) 
            throws NotFoundException, ConflictException, BusinessException, ConciliacionException {
        
        log.info("CLI2: Registrando pesaje final para patente={}, peso={} kg", patente, pesoFinal);

        // 1. Buscar orden CERRADA_PARA_CARGAR por patente (ordenadas FIFO)
        List<Orden> ordenes = ordenRepository.findByCamion_PatenteAndEstadoOrderByFechaRecepcionInicialAsc(
            patente, 
            EstadoOrden.CERRADA_PARA_CARGAR
        );

        if (ordenes.isEmpty()) {
            log.warn("CLI2: No se encontró orden cerrada para carga con patente={}", patente);
            throw new NotFoundException(
                "No se encontró orden cerrada para carga con la patente: " + patente
            );
        }

        // Tomar la PRIMERA orden (FIFO)
        Orden orden = ordenes.get(0);
        
        if (ordenes.size() > 1) {
            log.info("CLI2: Se encontraron {} órdenes cerradas para patente={}. Procesando la más antigua (id={}, numeroOrden={})", 
                     ordenes.size(), patente, orden.getId(), orden.getNumeroOrden());
        } else {
            log.info("CLI2: Orden encontrada id={}, numeroOrden={}", orden.getId(), orden.getNumeroOrden());
        }

        // 2. Validar que tenga pesaje inicial
        if (orden.getTara() == null) {
            log.error("CLI2: La orden {} no tiene pesaje inicial registrado", orden.getNumeroOrden());
            throw new ConflictException("La orden no tiene pesaje inicial registrado");
        }

        // 3. Registrar pesaje final
        orden.setPesajeFinal(pesoFinal);
        orden.setFechaPesajeFinal(LocalDateTime.now());
        
        // 4. Cambiar estado a FINALIZADA
        orden.setEstado(EstadoOrden.FINALIZADA);

        // 5. Persistir cambios
        try {
            orden = ordenRepository.save(orden);
            log.info("CLI2: Pesaje final registrado exitosamente para orden={}, estado={}", 
                     orden.getNumeroOrden(), orden.getEstado());
        } catch (Exception e) {
            log.error("CLI2: Error al guardar pesaje final", e);
            throw new BusinessException("Error al registrar pesaje final: " + e.getMessage());
        }

        // 6. Generar conciliación (delegando en OrdenBusiness)
        return ordenBusiness.getReconciliation(orden.getId());
    }

    /**
     * PUNTO 5: Obtiene la conciliación de una orden finalizada.
     * Delega en OrdenBusiness (lógica centralizada).
     */
    @Override
    @Transactional(readOnly = true)
    public Conciliacion getReconciliation(Long ordenId) 
            throws NotFoundException, ConflictException, ConciliacionException, BusinessException {
        
        log.info("CLI2: Consultando conciliación para ordenId={} (delegando a OrdenBusiness)", ordenId);
        return ordenBusiness.getReconciliation(ordenId);
    }
}
