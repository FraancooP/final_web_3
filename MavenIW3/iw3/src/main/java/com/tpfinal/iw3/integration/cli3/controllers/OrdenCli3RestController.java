package com.tpfinal.iw3.integration.cli3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tpfinal.iw3.integration.cli3.model.business.interfaces.IOrdenCli3Business;
import com.tpfinal.iw3.model.DetalleOrden;
import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.ConflictException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para CLI3 (Sistema de Control de Carga).
 * 
 * Este controlador gestiona los 3 endpoints del Punto 3:
 * 1. POST /validate → Validar contraseña y activar carga
 * 2. POST /detalle → Recibir datos de carga en tiempo real
 * 3. POST /close → Cerrar orden y finalizar proceso
 * 
 * Los endpoints son públicos (sin autenticación) para permitir integración con CLI3 externo.
 */
@RestController
@RequestMapping("/api/v1/integration/cli3/ordenes")
@Slf4j
public class OrdenCli3RestController {

    @Autowired
    private IOrdenCli3Business ordenCli3Business;

    /**
     * PUNTO 3.1: Validar contraseña de activación y activar carga.
     * 
     * El chofer ingresa la contraseña de 5 dígitos en el dispositivo CLI3.
     * Si es válida, el sistema activa la carga y retorna los datos necesarios
     * para iniciar el proceso (id, contraseña, preset).
     * 
     * Request Header: Password (Integer) - Contraseña de 5 dígitos
     * Response 200: JSON {"id": 1, "password": 45678, "preset": 25000}
     * Response 404: Contraseña no encontrada
     * Response 409: Estado de orden inválido
     */
    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validatePassword(@RequestHeader("Password") Integer password) {
        try {
            log.info("CLI3 REST: Validando contraseña: {}", password);
            
            Orden orden = ordenCli3Business.validatePassword(password);
            
            // Construir respuesta JSON con id, password, preset
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode response = mapper.createObjectNode();
            response.put("id", orden.getId());
            response.put("password", orden.getContraActivacion());
            response.put("preset", orden.getPresetKg());
            
            log.info("CLI3 REST: Carga activada exitosamente. OrdenId={}, Preset={}", 
                     orden.getId(), orden.getPresetKg());
            
            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
            
        } catch (NotFoundException e) {
            log.error("CLI3 REST: Contraseña no encontrada: {}", password);
            return new ResponseEntity<>("Contraseña no encontrada: " + password, 
                                       HttpStatus.NOT_FOUND);
        } catch (ConflictException e) {
            log.error("CLI3 REST: Estado inválido: {}", e.getMessage());
            return new ResponseEntity<>("Estado de orden inválido: " + e.getMessage(), 
                                       HttpStatus.CONFLICT);
        } catch (BusinessException e) {
            log.error("CLI3 REST: Error de negocio: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * PUNTO 3.2: Recibir datos de carga en tiempo real.
     * 
     * El dispositivo CLI3 envía datos periódicos durante el proceso de carga:
     * masa acumulada, densidad, temperatura, caudal, timestamp.
     * 
     * Este endpoint se invoca repetidamente (bucle) mientras dura la carga.
     * 
     * Request Header: Order-Id (Long) - ID de la orden
     * Request Body: JSON simple con {masaAcomulada, densidad, temperatura, caudal}
     * Response 200: Header Order-Id con ID de orden
     * Response 404: Orden no encontrada
     * Response 409: Estado inválido o datos inválidos
     */
    @PostMapping(value = "/detalle", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> receiveLoadData(
            @RequestHeader("Order-Id") Long ordenId,
            @RequestBody DetalleOrden detalleRequest) {
        try {
            log.debug("CLI3 REST: Recibiendo detalle de carga: ordenId={}, masa={}", 
                     ordenId, detalleRequest.getMasaAcomulada());
            
            // Crear detalle completo con la orden
            DetalleOrden detalle = new DetalleOrden();
            Orden orden = new Orden();
            orden.setId(ordenId);
            detalle.setOrden(orden);
            detalle.setMasaAcomulada(detalleRequest.getMasaAcomulada());
            detalle.setDensidad(detalleRequest.getDensidad());
            detalle.setTemperatura(detalleRequest.getTemperatura());
            detalle.setCaudal(detalleRequest.getCaudal());
            
            Orden ordenActualizada = ordenCli3Business.receiveDetails(detalle);
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Order-Id", String.valueOf(ordenActualizada.getId()));
            
            log.debug("CLI3 REST: Detalle almacenado exitosamente. OrdenId={}", ordenActualizada.getId());
            
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
            
        } catch (NotFoundException e) {
            log.error("CLI3 REST: Orden no encontrada: {}", e.getMessage());
            return new ResponseEntity<>("Orden no encontrada: " + e.getMessage(), 
                                       HttpStatus.NOT_FOUND);
        } catch (ConflictException e) {
            log.error("CLI3 REST: Datos inválidos o estado incorrecto: {}", e.getMessage());
            return new ResponseEntity<>("Datos o estado inválidos: " + e.getMessage(), 
                                       HttpStatus.CONFLICT);
        } catch (BusinessException e) {
            log.error("CLI3 REST: Error de negocio: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * PUNTO 3.3: Cerrar orden y finalizar proceso de carga.
     * 
     * El dispositivo CLI3 detecta que la carga finalizó (masa acumulada >= preset
     * o señal manual) y envía la petición de cierre.
     * 
     * Request Header: OrderId (Long) - ID de la orden
     * Response 200: Header Order-Id con ID de orden cerrada
     * Response 404: Orden no encontrada
     * Response 409: Estado inválido
     */
    @PostMapping("/close")
    public ResponseEntity<?> closeOrder(@RequestHeader("OrderId") Long orderId) {
        try {
            log.info("CLI3 REST: Cerrando orden: ordenId={}", orderId);
            
            Orden orden = ordenCli3Business.closeOrder(orderId);
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Order-Id", String.valueOf(orden.getId()));
            
            log.info("CLI3 REST: Orden cerrada exitosamente. OrdenId={}, NumeroOrden={}", 
                     orden.getId(), orden.getNumeroOrden());
            
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
            
        } catch (NotFoundException e) {
            log.error("CLI3 REST: Orden no encontrada: {}", e.getMessage());
            return new ResponseEntity<>("Orden no encontrada: " + e.getMessage(), 
                                       HttpStatus.NOT_FOUND);
        } catch (ConflictException e) {
            log.error("CLI3 REST: Estado inválido: {}", e.getMessage());
            return new ResponseEntity<>("Estado de orden inválido: " + e.getMessage(), 
                                       HttpStatus.CONFLICT);
        } catch (BusinessException e) {
            log.error("CLI3 REST: Error de negocio: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
