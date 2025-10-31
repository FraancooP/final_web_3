package com.tpfinal.iw3.integration.cli1.model;

import com.tpfinal.iw3.model.Producto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cli1_productos")
@PrimaryKeyJoinColumn(name = "id_producto")
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
