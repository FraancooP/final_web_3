package com.tpfinal.iw3.integration.cli1.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.tpfinal.iw3.integration.cli1.model.*;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.*;
import com.tpfinal.iw3.model.*;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

import static com.tpfinal.iw3.integration.cli1.util.JsonAttributeConstants.*;

public class JsonUtilsCli1 {

    /**
     * Extrae un JsonNode buscando entre múltiples posibles nombres de atributo
     */
    public static JsonNode getJsonNode(JsonNode node, String[] attrs) {
        for (String attr : attrs) {
            if (node.has(attr)) {
                return node.get(attr);
            }
        }
        return null;
    }

    /**
     * Extrae un String buscando entre múltiples posibles nombres de atributo
     */
    public static String getString(JsonNode node, String[] attrs, String defaultValue) {
        for (String attr : attrs) {
            if (node.has(attr) && !node.get(attr).isNull()) {
                return node.get(attr).asText();
            }
        }
        return defaultValue;
    }

    /**
     * Extrae un valor numérico buscando entre múltiples posibles nombres
     */
    public static int getValue(JsonNode node, String[] attrs, int defaultValue) {
        for (String attr : attrs) {
            if (node.has(attr) && !node.get(attr).isNull()) {
                return node.get(attr).asInt();
            }
        }
        return defaultValue;
    }

    /**
     * Extrae un Double buscando entre múltiples posibles nombres
     */
    public static Double getDouble(JsonNode node, String[] attrs, Double defaultValue) {
        for (String attr : attrs) {
            if (node.has(attr) && !node.get(attr).isNull()) {
                return node.get(attr).asDouble();
            }
        }
        return defaultValue;
    }

    /**
     * Obtiene o crea un Cliente desde el nodo JSON
     */
    public static Cliente getCliente(JsonNode node, IClienteCli1Business clienteCli1Business) 
            throws BusinessException, NotFoundException {
        JsonNode clienteNode = getJsonNode(node, CLIENTE_NODE_ATTRIBUTES);
        if (clienteNode != null) {
            String idCli1 = getString(clienteNode, CLIENTE_IDCLI1_ATTRIBUTES, "");
            String razonSocial = getString(clienteNode, CLIENTE_RAZON_SOCIAL_ATTRIBUTES, "");
            String contacto = getString(clienteNode, CLIENTE_CONTACTO_ATTRIBUTES, "");

            if (!idCli1.isEmpty() && !razonSocial.isEmpty()) {
                ClienteCli1 clienteCli1 = new ClienteCli1();
                clienteCli1.setIdCli1(idCli1);
                clienteCli1.setRazonSocial(razonSocial);
                clienteCli1.setContacto(contacto.isEmpty() ? "sin-contacto" : contacto);
                clienteCli1.setCodigoExterno("CLI1-" + idCli1);

                return clienteCli1Business.loadOrCreate(clienteCli1);
            }
        }
        return null;
    }

    /**
     * Obtiene o crea un Chofer desde el nodo JSON
     */
    public static Chofer getChofer(JsonNode node, IChoferCli1Business choferCli1Business) 
            throws BusinessException, NotFoundException {
        JsonNode choferNode = getJsonNode(node, CHOFER_NODE_ATTRIBUTES);
        if (choferNode != null) {
            String idCli1 = getString(choferNode, CHOFER_IDCLI1_ATTRIBUTES, "");
            String nombre = getString(choferNode, CHOFER_NOMBRE_ATTRIBUTES, "");
            String apellido = getString(choferNode, CHOFER_APELLIDO_ATTRIBUTES, "");
            String documento = getString(choferNode, CHOFER_DOCUMENTO_ATTRIBUTES, "");

            if (!idCli1.isEmpty() && !documento.isEmpty()) {
                ChoferCli1 choferCli1 = new ChoferCli1();
                choferCli1.setIdCli1(idCli1);
                choferCli1.setNombre(nombre.isEmpty() ? "Sin nombre" : nombre);
                choferCli1.setApellido(apellido.isEmpty() ? "Sin apellido" : apellido);
                choferCli1.setDocumento(documento);
                choferCli1.setCodigoExterno("CLI1-" + idCli1);

                return choferCli1Business.loadOrCreate(choferCli1);
            }
        }
        return null;
    }

    /**
     * Obtiene o crea un Camion desde el nodo JSON
     */
    public static Camion getCamion(JsonNode node, ICamionCli1Business camionCli1Business) 
            throws BusinessException, NotFoundException {
        JsonNode camionNode = getJsonNode(node, CAMION_NODE_ATTRIBUTES);
        if (camionNode != null) {
            String idCli1 = getString(camionNode, CAMION_IDCLI1_ATTRIBUTES, "");
            String patente = getString(camionNode, CAMION_PATENTE_ATTRIBUTES, "");
            String descripcion = getString(camionNode, CAMION_DESCRIPCION_ATTRIBUTES, "");

            if (!idCli1.isEmpty() && !patente.isEmpty()) {
                CamionCli1 camionCli1 = new CamionCli1();
                camionCli1.setIdCli1(idCli1);
                camionCli1.setPatente(patente);
                camionCli1.setDescripcion(descripcion.isEmpty() ? "Sin descripción" : descripcion);
                camionCli1.setCodigoExterno("CLI1-" + idCli1);

                return camionCli1Business.loadOrCreate(camionCli1);
            }
        }
        return null;
    }

    /**
     * Obtiene un Producto desde el nodo JSON
     */
    public static Producto getProducto(JsonNode node, IProductoCli1Business productoCli1Business) 
            throws BusinessException, NotFoundException {
        JsonNode productoNode = getJsonNode(node, PRODUCTO_NODE_ATTRIBUTES);
        if (productoNode != null) {
            String idCli1 = getString(productoNode, PRODUCTO_IDCLI1_ATTRIBUTES, "");
            String nombre = getString(productoNode, PRODUCTO_NOMBRE_ATTRIBUTES, "");
            String descripcion = getString(productoNode, PRODUCTO_DESCRIPCION_ATTRIBUTES, "");

            if (!idCli1.isEmpty() && !nombre.isEmpty()) {
                ProductoCli1 productoCli1 = new ProductoCli1();
                productoCli1.setIdCli1(idCli1);
                productoCli1.setNombre(nombre);
                productoCli1.setDescripcion(descripcion.isEmpty() ? "Sin descripción" : descripcion);
                productoCli1.setCodigoExterno("CLI1-" + idCli1);

                return productoCli1Business.load(productoCli1);
            }
        }
        return null;
    }
}