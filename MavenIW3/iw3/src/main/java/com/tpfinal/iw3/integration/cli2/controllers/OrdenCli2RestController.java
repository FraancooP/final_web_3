package com.tpfinal.iw3.integration.cli2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpfinal.iw3.integration.cli2.model.business.interfaces.IOrdenCli2Business;
import com.tpfinal.iw3.model.Conciliacion;
import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.ConciliacionException;
import com.tpfinal.iw3.model.business.excepciones.ConflictException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para CLI2 (Sistema de Balanza).
 * 
 * Endpoints:
 * - POST /api/v1/integration/cli2/ordenes/pesaje-inicial (Punto 2)
 * - POST /api/v1/integration/cli2/ordenes/pesaje-final (Punto 5)
 * - GET /api/v1/integration/cli2/ordenes/{ordenId}/conciliacion (Punto 5)
 * 
 * Este controlador maneja todos los procesos de pesaje del sistema.
 */
@RestController
@RequestMapping("/api/v1/integration/cli2/ordenes")
@Slf4j
public class OrdenCli2RestController {

    @Autowired
    private IOrdenCli2Business ordenCli2Business;

    /**
     * PUNTO 2: Registra el pesaje inicial (tara) de un camión.
     * 
     * Ejemplo de uso con curl:
     * curl -X POST http://localhost:8081/api/v1/integration/cli2/ordenes/pesaje-inicial \
     *   -H "License-Plate: ABC123" \
     *   -H "Initial-Weight: 8500.5"
     * 
     * Respuesta exitosa:
     *   Status: 200 OK
     *   Header: Order-Id: 1
     *   Body: 45678
     */
    @PostMapping(value = "/pesaje-inicial", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> registerInitialWeighing(
            @RequestHeader(value = "License-Plate", required = true) String patente,
            @RequestHeader(value = "Initial-Weight", required = true) Double pesoTara) {

        log.info("CLI2 REST: Recibiendo petición de pesaje inicial - Patente={}, Peso={} kg", 
                 patente, pesoTara);

        try {
            // Validaciones básicas
            if (patente == null || patente.trim().isEmpty()) {
                log.warn("CLI2 REST: Patente vacía o nula");
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: La patente es obligatoria (header License-Plate)");
            }

            if (pesoTara == null || pesoTara <= 0) {
                log.warn("CLI2 REST: Peso inválido={}", pesoTara);
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: El peso debe ser mayor a cero (header Initial-Weight)");
            }

            // Registrar pesaje inicial
            Orden orden = ordenCli2Business.registerInitialWeighing(patente, pesoTara);

            // Preparar respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.add("Order-Id", orden.getId().toString());

            String password = orden.getContraActivacion().toString();

            log.info("CLI2 REST: Pesaje registrado exitosamente - Orden={}, Password={}", 
                     orden.getNumeroOrden(), password);

            return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(password);

        } catch (NotFoundException e) {
            log.error("CLI2 REST: Orden no encontrada - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Error: " + e.getMessage());

        } catch (BusinessException e) {
            log.error("CLI2 REST: Error de negocio - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage());

        } catch (Exception e) {
            log.error("CLI2 REST: Error inesperado", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * PUNTO 5: Registra el pesaje final y devuelve la conciliación.
     * 
     * Ejemplo de uso con curl:
     * curl -X POST http://localhost:8081/api/v1/integration/cli2/ordenes/pesaje-final \
     *   -H "License-Plate: ABC123" \
     *   -H "Final-Weight: 25450.75" \
     *   -H "Content-Type: application/json"
     * 
     * Respuesta exitosa:
     *   Status: 200 OK
     *   Body: { ordenId, numeroOrden, pesajeInicial, pesajeFinal, ... }
     */
    @PostMapping(value = "/pesaje-final", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerFinalWeighing(
            @RequestHeader(value = "License-Plate", required = true) String patente,
            @RequestHeader(value = "Final-Weight", required = true) Double pesoFinal) {

        log.info("CLI2 REST: Recibiendo petición de pesaje final - Patente={}, Peso={} kg", 
                 patente, pesoFinal);

        try {
            // Validaciones básicas
            if (patente == null || patente.trim().isEmpty()) {
                log.warn("CLI2 REST: Patente vacía o nula");
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: La patente es obligatoria (header License-Plate)");
            }

            if (pesoFinal == null || pesoFinal <= 0) {
                log.warn("CLI2 REST: Peso final inválido={}", pesoFinal);
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: El peso final debe ser mayor a cero (header Final-Weight)");
            }

            // Registrar pesaje final y obtener conciliación
            Conciliacion conciliacion = ordenCli2Business.registerFinalWeighing(patente, pesoFinal);

            log.info("CLI2 REST: Pesaje final registrado - Orden={}, Diferencia={} kg", 
                     conciliacion.getNumeroOrden(), conciliacion.getDiferencia());

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(conciliacion);

        } catch (NotFoundException e) {
            log.error("CLI2 REST: Orden no encontrada - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Error: " + e.getMessage());

        } catch (ConflictException e) {
            log.error("CLI2 REST: Conflicto de estado - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Error: " + e.getMessage());

        } catch (ConciliacionException e) {
            log.error("CLI2 REST: Error en conciliación - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Error de conciliación: " + e.getMessage());

        } catch (BusinessException e) {
            log.error("CLI2 REST: Error de negocio - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage());

        } catch (Exception e) {
            log.error("CLI2 REST: Error inesperado", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * PUNTO 5: Consulta la conciliación de una orden finalizada.
     * 
     * Ejemplo de uso con curl:
     * curl -X GET http://localhost:8081/api/v1/integration/cli2/ordenes/1/conciliacion
     * 
     * Respuesta exitosa:
     *   Status: 200 OK
     *   Body: { ordenId, numeroOrden, pesajeInicial, pesajeFinal, ... }
     */
    @GetMapping(value = "/{ordenId}/conciliacion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReconciliation(@PathVariable Long ordenId) {

        log.info("CLI2 REST: Consultando conciliación para ordenId={}", ordenId);

        try {
            Conciliacion conciliacion = ordenCli2Business.getReconciliation(ordenId);

            log.info("CLI2 REST: Conciliación recuperada - Orden={}, Estado={}", 
                     conciliacion.getNumeroOrden(), conciliacion.getEstadoOrden());

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(conciliacion);

        } catch (NotFoundException e) {
            log.error("CLI2 REST: Orden no encontrada - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Error: " + e.getMessage());

        } catch (ConflictException e) {
            log.error("CLI2 REST: Orden no finalizada - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Error: " + e.getMessage());

        } catch (ConciliacionException e) {
            log.error("CLI2 REST: Error en conciliación - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Error de conciliación: " + e.getMessage());

        } catch (BusinessException e) {
            log.error("CLI2 REST: Error de negocio - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage());

        } catch (Exception e) {
            log.error("CLI2 REST: Error inesperado", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }
}