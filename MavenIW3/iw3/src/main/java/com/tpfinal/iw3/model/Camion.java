package com.tpfinal.iw3.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name="trucks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Camion {
	
	
	@Id
	private Long id;
	
	@Column(length = 10, unique=true)
	private String patente;
	
	@Column
	private String descripcion;
	
	
	
	@ElementCollection
	@Column
	private List<Cisterna> cisternas = new ArrayList<>();
	
	
	
}
