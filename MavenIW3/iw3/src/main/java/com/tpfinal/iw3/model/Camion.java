package com.tpfinal.iw3.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name="camiones")
@Inheritance(strategy = InheritanceType.JOINED)
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
	


	@OneToMany(mappedBy="camion", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Cisterna> cisternas = new HashSet<>();
	
	//@NotBlank
	@Column(name = "codigo_externo", length = 50, unique=true, nullable = false)
	private String codigoExterno;

	//No nos quedo claro si total debe ser persistido.
	//@Transient
	//public Integer getTotalCapacidad() {
	//	return cisternas.stream().mapToInt(Cisterna::getCapacidad).sum();
	//}
	
}
