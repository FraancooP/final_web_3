package com.tpfinal.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.model.Camion;
//Repositorio para la entidad Camion


@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {
    //Buscamos camion por su patente
    Optional<Camion> findByPatente(String patente);

    //Buscamos camion por su codigo externo
    Optional<Camion> findByCodigoExterno(String codigoExterno);

    Optional<Camion> findByPatenteAndIdNot(String patente, Long id);
}