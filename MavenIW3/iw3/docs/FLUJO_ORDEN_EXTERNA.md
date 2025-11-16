# Flujo completo: Creación de Orden Externa (CLI1)

Este documento explica con lujo de detalle el flujo de creación de una Orden externa recibida desde CLI1, cubriendo:
- Arquitectura por capas (Controller → Business → Utils/Deserializer → Repositorios/Entidades)
- Autenticación y seguridad
- Payloads esperados y tolerancia de atributos
- Sincronización de entidades relacionadas (Cliente, Chofer, Camión, Cisterna, Producto)
- Persistencia, transacciones y consistencia
- Manejo de errores y logs
- Pruebas rápidas con Swagger/Postman
- Diagrama de secuencia en texto

> Versionado: noviembre 2025 — Proyecto `MavenIW3/iw3` (Spring Boot 3.5.6, Java 17, MySQL)

---

## 1) Endpoint público de integración (CLI1)

Archivo: `integration/cli1/controllers/OrdenCli1RestController.java`

```java
@RestController
@RequestMapping(value = "${Spring.rest.prefix}", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrdenCli1RestController {

    @Autowired
    private IOrdenCli1Business ordenCli1Business;

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
```

- Ruta final: `POST /api/v1/integration/cli1/ordenes/externa`
- Recibe el JSON crudo en el cuerpo (se consume como `String`)
- Delegación a capa de negocio `IOrdenCli1Business.addExternal(...)`
- Respuesta: `201 Created` con la orden creada o `500` con detalle de error

Seguridad actual (según `SecurityConfiguration`):
- CLI1 (creación de orden) es protegido por JWT si así lo dejas configurado (recomendado)
- CLI2/CLI3 están públicos por diseño (sistemas externos que empujan datos)

Para trabajar autenticado en Swagger: botón "Authorize" y pegar el token JWT (sin la palabra `Bearer`).

---

## 2) Interfaz de negocio y su implementación

Archivos:
- `integration/cli1/model/business/interfaces/IOrdenCli1Business.java`
- `integration/cli1/model/business/implementaciones/OrdenCli1Business.java`

### Interfaz
```java
public interface IOrdenCli1Business {
    OrdenCli1 load(String numeroOrdenCli1) throws NotFoundException, BusinessException;
    List<OrdenCli1> list() throws BusinessException;
    OrdenCli1 add(OrdenCli1 orden) throws BusinessException;
    // Punto clave: entrada principal con JSON externo
    OrdenCli1 addExternal(String json) throws BusinessException;
}
```

### Implementación (núcleo del flujo)
```java
@Service
@Slf4j
public class OrdenCli1Business implements IOrdenCli1Business {

    @Autowired private OrdenCli1Repository ordenCli1DAO;
    @Autowired private IClienteCli1Business clienteCli1Business;
    @Autowired private IChoferCli1Business choferCli1Business;
    @Autowired private ICamionCli1Business camionCli1Business;
    @Autowired private ICisternaCli1Business cisternaCli1Business;
    @Autowired private IProductoCli1Business productoCli1Business;

    @Override
    @Transactional
    public OrdenCli1 addExternal(String json) throws BusinessException {
        log.info("Recibiendo orden externa CLI1");

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(OrdenCli1.class, new OrdenCli1JsonDeserializer(
                OrdenCli1.class,
                clienteCli1Business,
                choferCli1Business,
                camionCli1Business,
                cisternaCli1Business,
                productoCli1Business
        ));
        mapper.registerModule(module);

        try {
            // 1) JSON → OrdenCli1 (sincronizando entidades relacionadas)
            OrdenCli1 orden = mapper.readValue(json, OrdenCli1.class);
            log.info("Orden deserializada: {}", orden.getNumeroOrdenCli1());

            // 2) Persistencia con normalización de numeroOrden
            return add(orden);
        } catch (Exception e) {
            log.error("Error al procesar orden externa CLI1", e);
            throw new BusinessException("Error al procesar orden externa CLI1: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrdenCli1 add(OrdenCli1 orden) throws BusinessException {
        if (ordenCli1DAO.findOneByNumeroOrdenCli1(orden.getNumeroOrdenCli1()).isPresent()) {
            throw new BusinessException("Ya existe la orden CLI1 con numero=" + orden.getNumeroOrdenCli1());
        }
        try {
            // Normaliza el numero (padre Orden exige numero_orden único no nulo)
            try {
                orden.setNumeroOrden(Integer.parseInt(orden.getNumeroOrdenCli1()));
            } catch (NumberFormatException e) {
                orden.setNumeroOrden(orden.getNumeroOrdenCli1().hashCode());
            }
            return ordenCli1DAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al crear orden CLI1: " + e.getMessage());
        }
    }
}
```

Puntos clave:
- `ObjectMapper + SimpleModule + OrdenCli1JsonDeserializer`: patrón para deserialización custom
- `@Transactional`: garantiza ACID en la creación integral (orden + mapeos)
- Normalización de `numeroOrden`: la entidad padre `Orden` requiere columna única `numero_orden`

---

## 3) El deserializador: del JSON a la Orden y sincronización de entidades

Archivo: `integration/cli1/model/OrdenCli1JsonDeserializer.java`

Responsabilidad: convertir el JSON tolerante en distintos nombres de atributos → armar `OrdenCli1` y **sincronizar** entidades base (Cliente, Chofer, Camión, Cisterna, Producto) usando las capas `*Cli1Business`.

```java
public class OrdenCli1JsonDeserializer extends StdDeserializer<OrdenCli1> {

    // Se inyectan las business que sincronizan con el modelo base
    private IClienteCli1Business clienteCli1Business;
    private IChoferCli1Business choferCli1Business;
    private ICamionCli1Business camionCli1Business;
    private ICisternaCli1Business cisternaCli1Business;
    private IProductoCli1Business productoCli1Business;

    @Override
    public OrdenCli1 deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        OrdenCli1 orden = new OrdenCli1();
        JsonNode node = jp.getCodec().readTree(jp);

        // 1) Parseo tolerante
        String numeroOrden = JsonUtilsCli1.getString(node, ORDER_NUMBER_ATTRIBUTES, "");
        int preset = JsonUtilsCli1.getValue(node, ORDER_PRESET_ATTRIBUTES, 0);
        Double temperatura = JsonUtilsCli1.getDouble(node, ORDER_TEMPERATURA_ATTRIBUTES, null);

        // 2) Sincronización de entidades
        Cliente cliente = JsonUtilsCli1.getCliente(node, clienteCli1Business);
        Chofer chofer = JsonUtilsCli1.getChofer(node, choferCli1Business);
        Camion camion = JsonUtilsCli1.getCamion(node, camionCli1Business);
        Producto producto = JsonUtilsCli1.getProducto(node, productoCli1Business);
        Cisterna cisterna = JsonUtilsCli1.getCisterna(node, cisternaCli1Business);
        if (cisterna != null && camion != null) {
            cisterna.setCamion(camion);
            if (camion.getCisternas() != null && !camion.getCisternas().contains(cisterna)) {
                camion.getCisternas().add(cisterna);
            }
        }
        if (cliente == null || chofer == null || camion == null || producto == null) {
            throw new IOException("Faltan datos obligatorios en el JSON");
        }

        // 3) Armar Orden
        orden.setNumeroOrdenCli1(numeroOrden);
        orden.setPresetKg(preset);
        orden.setCliente(cliente);
        orden.setChofer(chofer);
        orden.setCamion(camion);
        orden.setProducto(producto);
        orden.setEstado(EstadoOrden.PENDIENTE_PESAJE_INICIAL);
        orden.setCodigoExterno("CLI1-" + numeroOrden);
        var ahora = LocalDateTime.now();
        orden.setFechaRecepcionInicial(ahora);
        orden.setNumeroOrden(0); // Se normaliza en business.add(...)
        orden.setUltimaMasaAcomulada(0.0);
        orden.setUltimaDensidad(0.0);
        orden.setUltimaTemperatura(temperatura != null ? temperatura : 0.0);
        orden.setUltimoCaudal(0.0);
        orden.setUltimaActualizacion(ahora);
        return orden;
    }
}
```

### Utilidad de parseo tolerante
Archivo: `integration/cli1/util/JsonUtilsCli1.java` y constantes `JsonAttributeConstants.java`.
- Permite aceptar payloads con distintos nombres de campos: `numero | number | order_number | numeroOrden`, etc.
- Cada `getXxx(...)` busca entre varios alias y retorna el valor detectado.

Ejemplo de constantes:
```java
public static final String[] ORDER_NUMBER_ATTRIBUTES = {"numero", "number", "order_number", "numeroOrden"};
public static final String[] PRODUCTO_NODE_ATTRIBUTES = {"producto", "product", "combustible", "fuel"};
```

---

## 4) Sincronización con el modelo base (MapperEntity + *Cli1Business)

Objetivo: que la Orden apunte a entidades del modelo principal (Cliente, Chofer, Camión, Producto, Cisterna), creando **mapeos CLI1 → Base** cuando corresponde.

Ejemplo: `ClienteCli1Business.loadOrCreate(...)`:
```java
@Transactional
public Cliente loadOrCreate(ClienteCli1 cliente) throws BusinessException, NotFoundException {
    Optional<Cliente> clienteBase = Optional.empty();
    try { clienteBase = Optional.ofNullable(clienteBaseBusiness.loadByRazonSocial(cliente.getRazonSocial())); }
    catch (NotFoundException ignored) {}

    if (clienteBase.isEmpty()) {
        try { return add(cliente); }
        catch (BusinessException e) { return clienteBaseBusiness.loadByRazonSocial(cliente.getRazonSocial()); }
    }
    mapperEntity.map(cliente, clienteBase.get());
    return clienteBase.get();
}
```

Mapper para Producto (relevante por política):
```java
@Transactional
public Producto map(ProductoCli1 productoCli1) throws BusinessException, NotFoundException {
    // Si ya está mapeado → retorna producto base
    var existente = productoCli1DAO.findOneByIdCli1(productoCli1.getIdCli1());
    if (existente.isPresent()) {
        return productoBaseBusiness.load(existente.get().getId());
    }
    // Producto debe existir previamente en catálogo → si no, se rechaza la orden
    Producto base = productoBaseBusiness.load(productoCli1.getNombre());
    productoCli1.setId(base.getId());
    productoCli1.setNombre(base.getNombre());
    productoCli1.setDescripcion(base.getDescripcion());
    productoCli1.setCodigoExterno(base.getCodigoExterno());
    productoCli1DAO.insertProductoCli1(base.getId(), productoCli1.getIdCli1());
    return base;
}
```

Notas:
- Producto: NO se auto-crea. Garantiza control del catálogo.
- Cliente/Chofer/Camión/Cisterna: se crean si no existen y se mapean.
- Todos los mapeos están encapsulados en `MapperEntity` con métodos `map(...)` específicos.

---

## 5) Entidades y persistencia (JPA)

Base abstracta `Orden` (tabla `ordenes`), con herencia JPA `JOINED`:
```java
@Entity
@Table(name="ordenes")
@Inheritance(strategy = InheritanceType.JOINED)
public class Orden {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_orden", unique=true, nullable = false)
    private Integer numeroOrden;

    @Column(name = "tara_camion")
    private Double tara;

    @Column(name = "pesaje_final")
    private Double pesajeFinal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoOrden estado = EstadoOrden.PENDIENTE_PESAJE_INICIAL;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "camion_id", nullable = false)
    private Camion camion;
    // ... cliente, chofer, producto, presetKg, fechas y últimos valores
}
```

Especialización para CLI1 (`cli1_orders`):
```java
@Entity
@Table(name = "cli1_orders")
@PrimaryKeyJoinColumn(name = "id_order")
public class OrdenCli1 extends Orden {
    @Column(name = "id_cli1", nullable = false, unique = true)
    private String numeroOrdenCli1;

    @Column(name = "cod_cli1_temp", columnDefinition = "tinyint default 0")
    private boolean codCli1Temp = false;
}
```

Repositorio JPA:
```java
@Repository
public interface OrdenCli1Repository extends JpaRepository<OrdenCli1, Long> {
    Optional<OrdenCli1> findOneByNumeroOrdenCli1(String numeroOrdenCli1);
}
```

---

## 6) Seguridad aplicada

Archivo: `security/SecurityConfiguration.java`
- Protegemos creación de órdenes CLI1 con JWT (Bearer) si así se desea.
- Endpoints públicos: CLI2/CLI3, Swagger, login, encode-password.

Uso de token en Postman/Swagger:
- Header: `Authorization: Bearer <TOKEN>`
- O en Swagger: botón "Authorize" → pegar solo el token.

---

## 7) Ejemplo de payload de orden externa (tolerante)

Cualquiera de estos nombres de atributos funciona, gracias a `JsonAttributeConstants`:

```json
{
  "numero": "10001",            // o: number, order_number, numeroOrden
  "preset": 24000,               // o: presetKg, preset_kg
  "temperatura": 18.5,           // o: temperature, temp
  "cliente": {
    "id": "CLI1-CLIENTE-001",
    "razonSocial": "Transportes Norte SA",
    "contacto": "contacto@transporte.com"
  },
  "chofer": {
    "id": "CLI1-CHOFER-001",
    "nombre": "Juan",
    "apellido": "Pérez",
    "documento": "12345678"
  },
  "camion": {
    "id": "CLI1-CAMION-001",
    "patente": "ABC123",
    "descripcion": "Scania R450"
  },
  "cisterna": {
    "id": "CLI1-CISTERNA-0001",
    "capacidad": 32000
  },
  "producto": {
    "id": "PROD-CLI1-001",
    "nombre": "Nafta Super",
    "descripcion": "Nafta sin plomo 95 octanos"
  }
}
```

---

## 8) Diagrama de secuencia (texto)

```
Cliente externo (CLI1)
    → POST /api/v1/integration/cli1/ordenes/externa (JSON)
Controller (OrdenCli1RestController)
    → IOrdenCli1Business.addExternal(json)
Business (OrdenCli1Business)
    → Crea ObjectMapper + registra OrdenCli1JsonDeserializer
    → mapper.readValue(json, OrdenCli1.class)
Deserializer (OrdenCli1JsonDeserializer)
    → JsonUtilsCli1 extrae atributos tolerantes
    → Llama *Cli1Business.loadOrCreate / map para Cliente, Chofer, Camión, Cisterna, Producto
    → Construye OrdenCli1 con estado PENDIENTE_PESAJE_INICIAL
    ← Retorna OrdenCli1 al Business
Business (OrdenCli1Business)
    → Normaliza numeroOrden (padre Orden exige unique not null)
    → ordenCli1DAO.save(orden)
    ← Retorna OrdenCli1 persistida
Controller
    ← Response 201 Created con la orden
DB (MySQL)
    ↔ Inserta en ordenes y cli1_orders (herencia JOINED)
```

---

## 9) Errores comunes y validaciones

- Producto inexistente: se lanza `NotFoundException` desde `MapperEntity.map(ProductoCli1)`. La política exige que los productos existan previamente en el catálogo.
- Duplicado de `numeroOrdenCli1`: `BusinessException` en `OrdenCli1Business.add(...)`.
- Falta de datos obligatorios: `IOException` en el deserializador si faltan Cliente/Chofer/Camión/Producto.
- Integridad de herencia JPA: `numero_orden` del padre `Orden` se calcula/normaliza para cumplir constraint único.
- Transacciones: anotación `@Transactional` evita órdenes parciales o mapeos inconsistentes.

---

## 10) Pruebas rápidas

- Swagger UI: `http://localhost:8081/swagger-ui.html`
  - Autorizar con JWT
  - Probar `POST /api/v1/integration/cli1/ordenes/externa`
- Postman:
  - Headers: `Content-Type: application/json`, `Authorization: Bearer <token>`
  - Body: JSON de ejemplo (arriba)

Logs útiles:
- `logging.level.com.tpfinal.iw3.integration.cli1=DEBUG` (`application.properties`)
- Hikari/SQL visibles con `spring.jpa.show-sql=true`

---

## 11) Resumen de responsabilidades por clase

- `OrdenCli1RestController`: expone endpoint de integración; delega a negocio.
- `IOrdenCli1Business` / `OrdenCli1Business`: orquesta deserialización, sincronización y persistencia.
- `OrdenCli1JsonDeserializer`: parsea JSON tolerante, sincroniza entidades, arma `OrdenCli1` con estado inicial.
- `JsonUtilsCli1` + `JsonAttributeConstants`: utilitarios de parseo tolerante y extracción tipada.
- `*Cli1Business` (Cliente, Chofer, Camión, Cisterna, Producto): políticas de load-or-create y mapeo a entidades base.
- `MapperEntity`: crea mapeos CLI1 → Base (insertando filas en tablas de vínculo según corresponda).
- `Orden` / `OrdenCli1`: modelo JPA con herencia JOINED; `OrdenCli1` extiende `Orden` y agrega `numeroOrdenCli1`.
- `OrdenCli1Repository`: repositorio Spring Data JPA para persistencia y consultas.
- `SecurityConfiguration`: define qué endpoints requieren autenticación JWT y cuáles son públicos.

---

## 12) Qué sigue (mejoras sugeridas)

- Manejo de errores HTTP más expresivo (400/404/409 con cuerpo JSON estandarizado) usando `@ControllerAdvice`.
- Validación de payload con `@Valid` y DTOs dedicados si se estandariza el formato externo.
- Idempotencia mediante una tabla de request-ids o control de reintentos.
- Métricas y trazas (Micrometer/Zipkin) para auditoría de integraciones.
- Test de contrato con Postman/Newman o Spring Cloud Contract.

---

¿Dudas o deseas que agreguemos un ejemplo real de request/response capturado desde Swagger para tu entorno actual? Puedo incluirlo y anexar capturas si hace falta.
