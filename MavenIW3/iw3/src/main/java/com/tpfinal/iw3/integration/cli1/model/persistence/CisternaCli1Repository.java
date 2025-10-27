package com.tpfinal.iw3.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.integration.cli1.model.CisternaCli1;

@Repository
public interface CisternaCli1Repository extends JpaRepository<CisternaCli1, Long> {

    Optional<CisternaCli1> findOneByIdCli1(String idCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_cisternas (id_cisterna, id_cli1, cod_cli1_temp) VALUES (:idCisterna, :idCli1, false)", nativeQuery = true)
    void insertCisternaCli1(@Param("idCisterna") Long idCisterna, @Param("idCli1") String idCli1);

}
