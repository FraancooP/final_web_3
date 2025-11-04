package com.tpfinal.iw3.model.persistence;

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

    // Busca una orden por patente del camión y estado específico
    // Usado en CLI2 para encontrar la orden PENDIENTE_PESAJE_INICIAL por patente
    Optional<Orden> findByCamion_PatenteAndEstado(String patente, EstadoOrden estado);
}
