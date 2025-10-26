package com.tpfinal.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpfinal.iw3.model.Cisterna;

public interface CisternaRepository extends  JpaRepository<Cisterna, Long>{
    Optional<Cisterna> findByCodigoExterno(String codigoExterno);

    Optional<Cisterna> findByCodigoExternoAndIdNot(String codigoExterno, Long id);

}
