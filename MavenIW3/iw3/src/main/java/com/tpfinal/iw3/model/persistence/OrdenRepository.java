package com.tpfinal.iw3.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.model.EstadoOrden;
import com.tpfinal.iw3.model.Orden;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {


    //Buscamos por su numero de orden
    Optional<Orden> findByNumeroOrden(Integer numeroOrden);

    Optional<Orden> findByEstado(String estado);

    Optional<Orden> findByContraActivacion(Integer contraActivacion);

    // Busca órdenes por patente del camión y estado específico, ordenadas por fecha de recepción
    // Retorna lista ordenada por fecha de recepción (FIFO: la más antigua primero)
    // Usado en CLI2 para encontrar la orden PENDIENTE_PESAJE_INICIAL por patente
    List<Orden> findByCamion_PatenteAndEstadoOrderByFechaRecepcionInicialAsc(String patente, EstadoOrden estado);
}
