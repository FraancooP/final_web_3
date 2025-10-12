package com.tpfinal.iw3.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.model.Cliente;
//Repositorio para la entidad Cliente

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    //Buscamos cliente por su codigo externo
    Optional<Cliente> findByCodigoExterno(String codigoExterno);


    //Buscamos cliente por su razon social
    //Notar que usamos Containing para buscar que contenga el string pasado
    //y IgnoreCase para que no importe mayusculas o minusculas
    List<Cliente> findByRazonSocialContainingIgnoreCase(String razonSocial);

}
