package com.tpfinal.iw3.integration.cli1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpfinal.iw3.integration.cli1.model.OrdenCli1;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.IOrdenCli1Business;

@RestController
@RequestMapping(value = "${Spring.rest.prefix}", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrdenCli1RestController {

    @Autowired
    private IOrdenCli1Business ordenCli1Business;

    /**
     * Endpoint que recibe JSON de CLI1 para crear una orden externa
     * 
     * Flujo:
     * 1. Recibe el JSON crudo de CLI1
     * 2. Usa el deserializador personalizado para parsear
     * 3. Sincroniza todas las entidades (Cliente, Chofer, Camión, Cisterna, Producto)
     * 4. Crea la orden en estado PENDIENTE_PESAJE_INICIAL
     * 
     * @param json HttpEntity con el JSON de CLI1
     * @return ResponseEntity con la orden creada (201 Created)
     */
    @PostMapping(value = "/integration/cli1/ordenes/externa", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addExternal(HttpEntity<String> json) {
        try {
            OrdenCli1 orden = ordenCli1Business.addExternal(json.getBody());
            
            return new ResponseEntity<>(orden, HttpStatus.CREATED);
            
        } catch (Exception e) {
            return new ResponseEntity<>(
                "Error al procesar la orden: " + e.getMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}