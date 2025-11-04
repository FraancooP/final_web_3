package com.tpfinal.iw3.integration.cli2.model.business.implementaciones;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpfinal.iw3.integration.cli2.model.business.interfaces.IOrdenCli2Business;
import com.tpfinal.iw3.model.EstadoOrden;
import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.persistence.OrdenRepository;
import com.tpfinal.iw3.util.ActivationPasswordGenerator;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de la lógica de negocio para CLI2 (Pesaje Inicial).
 * 
 * Flujo del Punto 2:
 * 1. El camión llega a la balanza con la orden ya creada (estado 1)
 * 2. CLI2 busca la orden por patente del camión
 * 3. Se registra el peso de la tara
 * 4. Se genera una contraseña única de 5 dígitos
 * 5. Se cambia el estado a CON_PESAJE_INICIAL (estado 2)
 * 6. La contraseña se entrega al chofer para activar la carga
 */
@Service
@Slf4j
public class OrdenCli2Business implements IOrdenCli2Business {

    @Autowired
    private OrdenRepository ordenRepository;

    @Override
    @Transactional
    public Orden registerInitialWeighing(String patente, Double pesoTara) 
            throws NotFoundException, BusinessException {
        
        log.info("CLI2: Registrando pesaje inicial para patente={}, peso={} kg", patente, pesoTara);

        // 1. Buscar orden PENDIENTE_PESAJE_INICIAL por patente
        Optional<Orden> ordenOpt = ordenRepository.findByCamion_PatenteAndEstado(
            patente, 
            EstadoOrden.PENDIENTE_PESAJE_INICIAL
        );

        if (ordenOpt.isEmpty()) {
            log.warn("CLI2: No se encontró orden pendiente de pesaje para patente={}", patente);
            throw new NotFoundException(
                "No se encontró orden pendiente de pesaje inicial para el camión con patente: " + patente
            );
        }

        Orden orden = ordenOpt.get();
        log.info("CLI2: Orden encontrada id={}, numeroOrden={}", orden.getId(), orden.getNumeroOrden());

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
}
