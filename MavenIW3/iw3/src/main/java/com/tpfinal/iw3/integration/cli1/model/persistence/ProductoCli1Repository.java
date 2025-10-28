package com.tpfinal.iw3.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.integration.cli1.model.ProductoCli1;

public interface ProductoCli1Repository extends JpaRepository<ProductoCli1, Long> {

    Optional<ProductoCli1> findOneByIdCli1(String codigoCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_productos (id_producto, id_cli1, cod_cli1_temp) VALUES (:idProducto, :idCli1, false)", nativeQuery = true)
    void insertProductoCli1(@Param("idProducto") Long idProducto, @Param("idCli1") String idCli1);
    
}
