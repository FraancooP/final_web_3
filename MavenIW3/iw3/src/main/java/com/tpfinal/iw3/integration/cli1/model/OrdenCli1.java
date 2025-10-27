package com.tpfinal.iw3.integration.cli1.model;

import com.tpfinal.iw3.model.Orden;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "cli1_orders")
@PrimaryKeyJoinColumn(name = "id_order")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrdenCli1 extends Orden {

    @Column(name = "id_cli1", nullable = false, unique = true)
    private String numeroOrdenCli1;

    @Column(name = "cod_cli1_temp", columnDefinition = "tinyint default 0")
    private boolean codCli1Temp = false;
}
