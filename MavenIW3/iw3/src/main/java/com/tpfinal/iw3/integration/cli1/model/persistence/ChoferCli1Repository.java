package com.tpfinal.iw3.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.integration.cli1.model.ChoferCli1;

@Repository
public interface ChoferCli1Repository extends JpaRepository<ChoferCli1, Long> {
    
    Optional<ChoferCli1> findOneByIdCli1(String dniCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_choferes (id_chofer, id_cli1, cod_cli1_temp) VALUES (:idChofer, :idCli1, false)", nativeQuery = true)
    void insertChoferCli1(@Param("idChofer") Long idChofer, @Param("idCli1") String idCli1);
    
}
    