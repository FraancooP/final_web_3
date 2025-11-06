package com.tpfinal.iw3.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "detalle_orden")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrden {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @Column(name = "masa_acomulada", nullable = false)
    private float masaAcomulada;

    @Column(name = "densidad", nullable = false)
    private float densidad;

    @Column(name = "temperatura", nullable = false)
    private float temperatura;

    @Column(name = "caudal", nullable = false)
    private float caudal;

    @Column(name = "estampa_tiempo", nullable = false)
    private Long estampaTiempo;  // Timestamp Unix en milisegundos



}
