package com.tpfinal.iw3.integration.cli2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpfinal.iw3.integration.cli2.model.business.interfaces.IOrdenCli2Business;
import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para CLI2 (Sistema de Balanza).
 * 
 * Endpoint: POST /api/v1/integration/cli2/ordenes/pesaje-inicial
 * 
 * Este endpoint es llamado por el sistema externo de la balanza cuando
 * el camión llega a pesarse (tara) antes de la carga.
 * 
 * INPUT (Headers):
 *   - License-Plate: Patente del camión (ej: "ABC123")
 *   - Initial-Weight: Peso de la tara en kg (ej: "8500.5")
 * 
 * OUTPUT:
 *   - Body: Contraseña de activación de 5 dígitos (texto plano)
 *   - Header Order-Id: ID de la orden actualizada
 *   - Status: 200 OK si exitoso, 404 si no encuentra orden, 400 si error
 */
@RestController
@RequestMapping("/api/v1/integration/cli2/ordenes")
@Slf4j
public class OrdenCli2RestController {

    @Autowired
    private IOrdenCli2Business ordenCli2Business;

    /**
     * Registra el pesaje inicial (tara) de un camión.
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
}
