package com.tpfinal.iw3.integration.cli1.model;

import com.tpfinal.iw3.model.Producto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "cli1_products")
@PrimaryKeyJoinColumn(name = "id_product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductoCli1 extends Producto {

    @Column(name = "id_cli1", nullable = false, unique = true)
    private String idCli1;

    @Column(name = "cod_cli1_temp", columnDefinition = "tinyint default 0")
    private boolean codCli1Temp = false;
}
