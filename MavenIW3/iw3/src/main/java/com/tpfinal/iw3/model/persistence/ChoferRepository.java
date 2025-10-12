package com.tpfinal.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.model.Chofer;

@Repository
public interface ChoferRepository extends JpaRepository<Chofer, Long> {


    //Buscamos chofer por su documento
    Optional<Chofer> findByDocumento(String documento);

    //Buscamos chofer por su codigo externo
    Optional<Chofer> findByCodigoExterno(String codigoExterno);
}
