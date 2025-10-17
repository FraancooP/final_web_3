package com.tpfinal.iw3.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name="camiones")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Camion {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//Consideramos que la patente no puede estar vacia y debe ser unica
	//ademas de no ser nula y tener un largo maximo de 10 caracteres
	//@NotBlank
	@Column(length = 10, unique=true, nullable = false)
	private String patente;
	
	@Column(columnDefinition="TEXT")
	private String descripcion;
	


	//Usamos collection table para mapear la lista de cisternas
	//ya que es una clase embebida y no una entidad
	//La tabla se llamara camion_cisternas y tendra una columna camion_id
	//que sera la clave foranea a la tabla camiones
	//y las columnas de la clase cisterna
	@ElementCollection
	@CollectionTable(name="camion_cisternas", joinColumns=@JoinColumn(name="camion_id"))
	private List<Cisterna> cisternas = new ArrayList<>();
	
	//@NotBlank
	@Column(name = "codigo_externo", length = 50, unique=true, nullable = false)
	private String codigoExterno;

	//No nos quedo claro si total debe ser persistido.
	//@Transient
	//public Integer getTotalCapacidad() {
	//	return cisternas.stream().mapToInt(Cisterna::getCapacidad).sum();
	//}
	
}
