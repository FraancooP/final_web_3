package com.tpfinal.iw3.integration.cli1.model.persistence;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.integration.cli1.model.CamionCli1;

@Repository
public interface CamionCli1Repository extends JpaRepository<CamionCli1, Long> {

    Optional<CamionCli1> findOneByPatenteCli1(String patenteCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_camiones (id_camion, id_cli1, cod_cli1_temp) VALUES (:idCamion, :idCli1, false)", nativeQuery = true)
    void insertCamionCli1(@Param("idCamion") Long idCamion, @Param("idCli1") String idCli1);
    
}
