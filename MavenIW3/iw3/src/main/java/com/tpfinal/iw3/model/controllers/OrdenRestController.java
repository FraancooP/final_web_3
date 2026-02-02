package com.tpfinal.iw3.model.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpfinal.iw3.model.Conciliacion;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.ConciliacionException;
import com.tpfinal.iw3.model.business.excepciones.ConflictException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IOrdenBusiness;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST principal para el modelo Orden.
 * 
 * Este controlador centraliza operaciones generales sobre órdenes,
 * independientemente del punto de integración (CLI1, CLI2, CLI3).
 * 
 * Endpoints:
 * - GET /api/v1/ordenes/{ordenId}/conciliacion - Obtener conciliación de una orden
 */
@RestController
@RequestMapping("/api/v1/ordenes")
@Slf4j
@Tag(name = "Órdenes", description = "Endpoints principales del modelo Orden")
public class OrdenRestController {

    @Autowired
    private IOrdenBusiness ordenBusiness;

    /**
     * Obtiene la lista completa de órdenes del sistema.
     * 
     * @return ResponseEntity con la lista de órdenes o error
     * 
     * Ejemplo de uso:
     * GET /api/v1/ordenes
     * 
     * Respuesta exitosa (200 OK):
     * [
     *   {
     *     "id": 1,
     *     "numeroOrden": 12345,
     *     "estado": "EN_CARGA",
     *     "preset": 30000.0,
     *     "masaAcumulada": 15000.0,
     *     ...
     *   }
     * ]
     */
    @Operation(
        summary = "Listar todas las órdenes",
        description = "Retorna la lista completa de órdenes del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de órdenes obtenida exitosamente"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(mediaType = "text/plain")
        )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listarOrdenes() {
        log.info("REST Orden: Listando todas las órdenes");
        
        try {
            var ordenes = ordenBusiness.list();
            log.info("REST Orden: Se encontraron {} órdenes", ordenes.size());
            return ResponseEntity.ok(ordenes);
            
        } catch (BusinessException e) {
            log.error("REST Orden: Error al listar órdenes - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al listar órdenes: " + e.getMessage());
                
        } catch (Exception e) {
            log.error("REST Orden: Error inesperado al listar órdenes", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Obtiene la conciliación de una orden finalizada.
     * 
     * La conciliación incluye:
     * - Pesajes (inicial y final)
     * - Masa neta calculada por balanza
     * - Masa acumulada de los detalles de carga
     * - Diferencia entre balanza y detalles
     * - Porcentaje de diferencia
     * - Promedios de temperatura, densidad y caudal
     * 
     * @param ordenId ID de la orden
     * @return ResponseEntity con la conciliación o error
     * 
     * Ejemplo de uso:
     * GET /api/v1/ordenes/123/conciliacion
     * 
     * Respuesta exitosa (200 OK):
     * {
     *   "ordenId": 123,
     *   "numeroOrden": 12345,
     *   "estadoOrden": "FINALIZADA",
     *   "pesajeInicial": 8500.5,
     *   "pesajeFinal": 48500.5,
     *   "netoPorBalanza": 40000.0,
     *   "ultimaMasaAcumulada": 39800.0,
     *   "diferencia": 200.0,
     *   "porcentajeDiferencia": 0.5,
     *   "promedioTemperatura": 25.5,
     *   "promedioDensidad": 850.2,
     *   "promedioCaudal": 120.5
     * }
     * 
     * Errores posibles:
     * - 404 NOT FOUND: Orden no existe
     * - 409 CONFLICT: Orden no está finalizada
     * - 422 UNPROCESSABLE ENTITY: Error al calcular conciliación
     * - 500 INTERNAL SERVER ERROR: Error general de negocio
     */
    @Operation(
        summary = "Obtener conciliación de orden",
        description = "Retorna los datos de conciliación de una orden finalizada, incluyendo pesajes, diferencias y promedios"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Conciliación obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Conciliacion.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Orden no encontrada",
            content = @Content(mediaType = "text/plain")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Orden no está finalizada",
            content = @Content(mediaType = "text/plain")
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Error al calcular conciliación",
            content = @Content(mediaType = "text/plain")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(mediaType = "text/plain")
        )
    })
    @GetMapping(value = "/{ordenId}/conciliacion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getConciliacion(@PathVariable Long ordenId) {

        log.info("REST Orden: Consultando conciliación para ordenId={}", ordenId);

        try {
            Conciliacion conciliacion = ordenBusiness.getReconciliation(ordenId);

            log.info("REST Orden: Conciliación recuperada - Orden={}, Estado={}, Diferencia={}kg", 
                     conciliacion.getNumeroOrden(), 
                     conciliacion.getEstadoOrden(),
                     conciliacion.getDiferencia());

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(conciliacion);

        } catch (NotFoundException e) {
            log.error("REST Orden: Orden no encontrada - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Error: Orden no encontrada - " + e.getMessage());

        } catch (ConflictException e) {
            log.error("REST Orden: Orden no finalizada - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Error: La orden debe estar finalizada para obtener conciliación - " + e.getMessage());

        } catch (ConciliacionException e) {
            log.error("REST Orden: Error en conciliación - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Error de conciliación: " + e.getMessage());

        } catch (BusinessException e) {
            log.error("REST Orden: Error de negocio - {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno: " + e.getMessage());

        } catch (Exception e) {
            log.error("REST Orden: Error inesperado al obtener conciliación", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error inesperado: " + e.getMessage());
        }
    }
}
