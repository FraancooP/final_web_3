package com.tpfinal.iw3.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tpfinal.iw3.model.DetalleOrden;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {

    Optional<List<DetalleOrden>> findByOrdenId(Long ordenId);

    @Query("SELECT AVG(d.temperatura) FROM DetalleOrden d WHERE d.orden.id = :orderId")
    Double findAverageTemperatureByOrderId(Long orderId);

    @Query("SELECT AVG(d.densidad) FROM DetalleOrden d WHERE d.orden.id = :orderId")
    Double findAverageDensityByOrderId(Long orderId);

    @Query("SELECT AVG(d.caudal) FROM DetalleOrden d WHERE d.orden.id = :orderId")
    Double findAverageFlowRateByOrderId(Long orderId);

}
