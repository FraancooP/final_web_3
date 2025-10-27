package com.tpfinal.iw3.integration.cli1.util;

public class JsonAttributeConstants {

    // Constantes para el nodo Order
    public static final String[] ORDER_NUMBER_ATTRIBUTES = {"numero", "number", "order_number", "numeroOrden"};
    public static final String[] ORDER_PRESET_ATTRIBUTES = {"preset", "presetKg", "preset_kg"};
    public static final String[] ORDER_TEMPERATURA_ATTRIBUTES = {"temperatura", "temperature", "temp"};

    // Constantes para el nodo Cliente
    public static final String[] CLIENTE_NODE_ATTRIBUTES = {"cliente", "customer", "client"};
    public static final String[] CLIENTE_IDCLI1_ATTRIBUTES = {"id", "id_cli1", "codigo", "code"};
    public static final String[] CLIENTE_RAZON_SOCIAL_ATTRIBUTES = {"razonSocial", "razon_social", "businessName", "nombre"};
    public static final String[] CLIENTE_CONTACTO_ATTRIBUTES = {"contacto", "email", "mail"};

    // Constantes para el nodo Chofer
    public static final String[] CHOFER_NODE_ATTRIBUTES = {"chofer", "driver", "conductor"};
    public static final String[] CHOFER_IDCLI1_ATTRIBUTES = {"id", "id_cli1", "codigo", "code"};
    public static final String[] CHOFER_NOMBRE_ATTRIBUTES = {"nombre", "name", "firstName"};
    public static final String[] CHOFER_APELLIDO_ATTRIBUTES = {"apellido", "lastName", "last_name"};
    public static final String[] CHOFER_DOCUMENTO_ATTRIBUTES = {"documento", "document", "dni", "doc"};

    // Constantes para el nodo Camion
    public static final String[] CAMION_NODE_ATTRIBUTES = {"camion", "truck", "vehiculo", "vehicle"};
    public static final String[] CAMION_IDCLI1_ATTRIBUTES = {"id", "id_cli1", "codigo", "code"};
    public static final String[] CAMION_PATENTE_ATTRIBUTES = {"patente", "licensePlate", "license_plate", "plate"};
    public static final String[] CAMION_DESCRIPCION_ATTRIBUTES = {"descripcion", "description", "desc"};

    // Constantes para el nodo Producto
    public static final String[] PRODUCTO_NODE_ATTRIBUTES = {"producto", "product", "combustible", "fuel"};
    public static final String[] PRODUCTO_IDCLI1_ATTRIBUTES = {"id", "id_cli1", "codigo", "code"};
    public static final String[] PRODUCTO_NOMBRE_ATTRIBUTES = {"nombre", "name", "product"};
    public static final String[] PRODUCTO_DESCRIPCION_ATTRIBUTES = {"descripcion", "description", "desc"};
}