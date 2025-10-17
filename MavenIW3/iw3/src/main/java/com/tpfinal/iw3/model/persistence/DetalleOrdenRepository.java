package com.tpfinal.iw3.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpfinal.iw3.model.DetalleOrden;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {

    Optional<List<DetalleOrden>> findByOrdenId(Long ordenId);

}
