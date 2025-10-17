package com.tpfinal.iw3.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name="ordenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orden {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_orden", unique=true, nullable = false)
    private Integer numeroOrden;

    @Column(name = "tara_camion")
    private Double tara;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoOrden estado = EstadoOrden.PENDIENTE_PESAJE_INICIAL;

    //Recordemos que el atributo fetch define como se carga la relacion
    //EAGER: carga inmediata, LAZY: carga diferida
    //En este caso usamos LAZY para evitar cargar toda la informacion relacionada
    //cuando no es necesario




    @OneToMany(
        mappedBy = "orden",           // Nombre del campo en DetalleOrden    
       orphanRemoval = true,          // Si quitas un detalle de la lista, se elimina de BD
        fetch = FetchType.LAZY         // No carga detalles automáticamente
    )
    private List<DetalleOrden> detalles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camion_id", nullable = false)
    private Camion camion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chofer_id", nullable = false)
    private Chofer chofer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;




    //Preset
    @Column(name = "preset_kg", nullable = false)
    private Integer presetKg;

    //Fechas de los distintos eventos de la orden
    @Column(name = "fecha_recepcion_inicial", nullable = false)
    private LocalDateTime fechaRecepcionInicial;

    @Column(name = "fecha_pesaje_inicial", nullable = false)
    private LocalDateTime fechaPesajeInicial;

    @Column(name = "fecha_inicio_carga", nullable = false)
    private LocalDateTime fechaInicioCarga;

    @Column(name = "fecha_fin_carga", nullable = false)
    private LocalDateTime fechaFinCarga;

    @Column(name = "fecha_pesaje_final", nullable = false)
    private LocalDateTime fechaPesajeFinal;








    // Últimos valores de carga (estado en tiempo real)
    @Column(name = "ultima_masa_acomulada", nullable = false)
    private Double ultimaMasaAcomulada;

    @Column(name = "ultima_densidad", nullable = false)
    private Double ultimaDensidad;

    @Column(name = "ultima_temperatura", nullable = false)
    private Double ultimaTemperatura;

    @Column(name = "ultimo_caudal", nullable = false)
    private Double ultimoCaudal;
    
    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime ultimaActualizacion;


    //Codigo externo y contraseña de activacion
    @Column(name = "codigo_externo", length = 50, unique=true, nullable = false)
    private String codigoExterno;

    @Column(name = "contra_activacion")
    private Integer contraActivacion;

}
