package com.tpfinal.iw3.integration.cli1.model.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tpfinal.iw3.integration.cli1.model.OrdenCli1;


@Repository
public interface OrdenCli1Repository extends JpaRepository<OrdenCli1, Long> {

    Optional<OrdenCli1> findOneByNumeroOrdenCli1(String numeroOrdenCli1);

}
