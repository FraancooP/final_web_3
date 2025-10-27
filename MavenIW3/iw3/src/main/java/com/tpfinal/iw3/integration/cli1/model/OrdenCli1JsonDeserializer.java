package com.tpfinal.iw3.integration.cli1.model;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.*;
import com.tpfinal.iw3.integration.cli1.util.JsonUtilsCli1;
import com.tpfinal.iw3.model.*;

import static com.tpfinal.iw3.integration.cli1.util.JsonAttributeConstants.*;

public class OrdenCli1JsonDeserializer extends StdDeserializer<OrdenCli1> {

    private static final long serialVersionUID = 1L;

    private IClienteCli1Business clienteCli1Business;
    private IChoferCli1Business choferCli1Business;
    private ICamionCli1Business camionCli1Business;
    private IProductoCli1Business productoCli1Business;

    public OrdenCli1JsonDeserializer(Class<?> vc) {
        super(vc);
    }

    public OrdenCli1JsonDeserializer(
            Class<?> vc,
            IClienteCli1Business clienteCli1Business,
            IChoferCli1Business choferCli1Business,
            ICamionCli1Business camionCli1Business,
            IProductoCli1Business productoCli1Business) {
        super(vc);
        this.clienteCli1Business = clienteCli1Business;
        this.choferCli1Business = choferCli1Business;
        this.camionCli1Business = camionCli1Business;
        this.productoCli1Business = productoCli1Business;
    }

    @Override
    public OrdenCli1 deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        OrdenCli1 orden = new OrdenCli1();
        JsonNode node = jp.getCodec().readTree(jp);

        try {
            // 1. Parsear datos básicos de la orden
            String numeroOrden = JsonUtilsCli1.getString(node, ORDER_NUMBER_ATTRIBUTES, "");
            int preset = JsonUtilsCli1.getValue(node, ORDER_PRESET_ATTRIBUTES, 0);
            Double temperatura = JsonUtilsCli1.getDouble(node, ORDER_TEMPERATURA_ATTRIBUTES, null);

            // 2. Buscar/crear entidades relacionadas (SINCRONIZACIÓN)
            Cliente cliente = JsonUtilsCli1.getCliente(node, clienteCli1Business);
            Chofer chofer = JsonUtilsCli1.getChofer(node, choferCli1Business);
            Camion camion = JsonUtilsCli1.getCamion(node, camionCli1Business);
            Producto producto = JsonUtilsCli1.getProducto(node, productoCli1Business);

            // 3. Validar que todas las entidades existan
            if (cliente == null || chofer == null || camion == null || producto == null) {
                throw new IOException("Faltan datos obligatorios en el JSON");
            }

            // 4. Armar la orden
            orden.setNumeroOrdenCli1(numeroOrden);
            orden.setPresetKg(preset);
            orden.setCliente(cliente);
            orden.setChofer(chofer);
            orden.setCamion(camion);
            orden.setProducto(producto);
            orden.setEstado(EstadoOrden.PENDIENTE_PESAJE_INICIAL);  // ← ESTADO INICIAL
            orden.setFechaRecepcionInicial(LocalDateTime.now());
            orden.setCodigoExterno("CLI1-" + numeroOrden);

            // Inicializar valores por defecto
            orden.setNumeroOrden(0); // Se autogenera
            orden.setUltimaMasaAcomulada(0.0);
            orden.setUltimaDensidad(0.0);
            orden.setUltimaTemperatura(temperatura != null ? temperatura : 0.0);
            orden.setUltimoCaudal(0.0);
            orden.setUltimaActualizacion(LocalDateTime.now());

            return orden;

        } catch (Exception e) {
            throw new IOException("Error al deserializar orden CLI1: " + e.getMessage(), e);
        }
    }
}