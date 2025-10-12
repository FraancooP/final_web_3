package com.tpfinal.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    //Buscamos producto por su codigo externo
    Optional<Producto> findByCodigoExterno(String codigoExterno);
    //Buscamos producto por su nombre
    Optional<Producto> findByProduct(String product);
    //Buscamos producto por su nombre excluyendo un id
    Optional<Producto> findByProductAndIdNot(String product, long id);
	
}
