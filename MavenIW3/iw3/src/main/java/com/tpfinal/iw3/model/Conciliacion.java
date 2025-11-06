package com.tpfinal.iw3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para representar la conciliación de una orden finalizada (Punto 5).
 * 
 * Contiene todos los datos calculados para verificar la carga:
 * - Pesajes (inicial y final)
 * - Cálculos de neto
 * - Diferencia entre balanza y caudalímetro
 * - Promedios de temperatura, densidad y caudal
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conciliacion {
    
    // Identificación de la orden
    private Long ordenId;
    private Integer numeroOrden;
    
    // Pesajes
    private Double pesajeInicial;      // Tara del camión (kg)
    private Double pesajeFinal;         // Peso final del camión (kg)
    private Double productoCargado;     // Última masa acumulada del caudalímetro (kg)
    
    // Cálculos
    private Double netoPorBalanza;      // pesajeFinal - pesajeInicial (kg)
    private Double diferencia;          // netoPorBalanza - productoCargado (kg)
    private Double porcentajeDiferencia; // (diferencia / productoCargado) * 100 (%)
    
    // Promedios de los detalles almacenados
    private Float promedioTemperatura;  // Promedio de temperatura (°C)
    private Float promedioDensidad;     // Promedio de densidad (kg/m³)
    private Float promedioCaudal;       // Promedio de caudal (kg/h)
    
    // Preset original
    private Integer presetKg;           // Cantidad esperada a cargar (kg)
    
    // Información adicional
    private String estadoOrden;         // Estado actual de la orden
    private Integer cantidadDetalles;   // Cantidad de registros almacenados
}
