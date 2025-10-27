package com.tpfinal.iw3.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.integration.cli1.model.ClienteCli1;

@Repository
public interface ClienteCli1Repository extends JpaRepository<ClienteCli1, Long> {


    Optional<ClienteCli1> findOneByIdCli1(String idCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_clientes (id_cliente, id_cli1, cod_cli1_temp) VALUES (:idCliente, :idCli1, false)", nativeQuery = true)
    void insertClienteCli1(@Param("idCliente") Long idCliente, @Param("idCli1") String idCli1);
}
