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
import com.tpfinal.iw3.model.business.interfaces.IDetalleOrdenBusiness;
import com.tpfinal.iw3.model.persistence.DetalleOrdenRepository;
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
    private DetalleOrdenRepository detalleOrdenRepository;

    @Autowired
    private IDetalleOrdenBusiness detalleOrdenBusiness;

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

        // 6. Generar conciliación
        return calculateReconciliation(orden);
    }

    /**
     * PUNTO 5: Obtiene la conciliación de una orden finalizada.
     */
    @Override
    @Transactional(readOnly = true)
    public Conciliacion getReconciliation(Long ordenId) 
            throws NotFoundException, ConflictException, ConciliacionException, BusinessException {
        
        log.info("CLI2: Consultando conciliación para ordenId={}", ordenId);

        // 1. Buscar orden
        Optional<Orden> ordenOpt;
        try {
            ordenOpt = ordenRepository.findById(ordenId);
        } catch (Exception e) {
            log.error("CLI2: Error al buscar orden id={}: {}", ordenId, e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        }

        if (ordenOpt.isEmpty()) {
            log.error("CLI2: No se encontró orden con id={}", ordenId);
            throw new NotFoundException("Orden no encontrada con id: " + ordenId);
        }

        Orden orden = ordenOpt.get();

        // 2. Validar estado FINALIZADA
        if (orden.getEstado() != EstadoOrden.FINALIZADA) {
            log.error("CLI2: La orden {} no está finalizada. Estado actual: {}", 
                     orden.getNumeroOrden(), orden.getEstado());
            throw new ConflictException(
                "La conciliación solo está disponible para órdenes FINALIZADAS. Estado actual: " + orden.getEstado()
            );
        }

        // 3. Calcular conciliación
        return calculateReconciliation(orden);
    }

    /**
     * Calcula la conciliación para una orden.
     * Requiere: pesajeInicial, pesajeFinal, ultimaMasaAcomulada.
     */
    private Conciliacion calculateReconciliation(Orden orden) 
            throws ConciliacionException, BusinessException {
        
        log.debug("CLI2: Calculando conciliación para orden={}", orden.getId());

        // Validar datos requeridos
        if (orden.getTara() == null) {
            throw new ConciliacionException("Falta pesaje inicial (tara)");
        }
        if (orden.getPesajeFinal() == null) {
            throw new ConciliacionException("Falta pesaje final");
        }
        if (orden.getUltimaMasaAcomulada() == null) {
            throw new ConciliacionException("No hay masa acumulada registrada");
        }

        // Calcular neto por balanza
        Double netoPorBalanza = orden.getPesajeFinal() - orden.getTara();
        
        // Calcular diferencia
        Double diferencia = netoPorBalanza - orden.getUltimaMasaAcomulada();
        
        // Calcular porcentaje de diferencia
        Double porcentajeDiferencia = 0.0;
        if (orden.getUltimaMasaAcomulada() > 0) {
            porcentajeDiferencia = (diferencia / orden.getUltimaMasaAcomulada()) * 100.0;
        }

        // Calcular promedios de los detalles almacenados
        Float promedioTemperatura = detalleOrdenBusiness.calculateAverageTemperature(orden.getId());
        Float promedioDensidad = detalleOrdenBusiness.calculateAverageDensity(orden.getId());
        Float promedioCaudal = detalleOrdenBusiness.calculateAverageFlowRate(orden.getId());

        // Contar cantidad de detalles
        Optional<List<com.tpfinal.iw3.model.DetalleOrden>> detallesOpt = 
            detalleOrdenRepository.findByOrdenId(orden.getId());
        int cantidadDetalles = detallesOpt.isPresent() ? detallesOpt.get().size() : 0;

        // Construir objeto de conciliación
        Conciliacion conciliacion = Conciliacion.builder()
            .ordenId(orden.getId())
            .numeroOrden(orden.getNumeroOrden())
            .pesajeInicial(orden.getTara())
            .pesajeFinal(orden.getPesajeFinal())
            .productoCargado(orden.getUltimaMasaAcomulada())
            .netoPorBalanza(netoPorBalanza)
            .diferencia(diferencia)
            .porcentajeDiferencia(porcentajeDiferencia)
            .promedioTemperatura(promedioTemperatura)
            .promedioDensidad(promedioDensidad)
            .promedioCaudal(promedioCaudal)
            .presetKg(orden.getPresetKg())
            .estadoOrden(orden.getEstado().name())
            .cantidadDetalles(cantidadDetalles)
            .build();

        log.info("CLI2: Conciliación calculada - Orden={}, Neto balanza={} kg, Producto cargado={} kg, Diferencia={} kg ({} %)", 
                 orden.getNumeroOrden(), netoPorBalanza, orden.getUltimaMasaAcomulada(), 
                 diferencia, String.format("%.2f", porcentajeDiferencia));

        return conciliacion;
    }
}
