package com.tpfinal.iw3.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




//Usamos Embeddable para indicar que esta clase sera embebida en otra entidad
//En este caso sera embebida en la clase Camion
//No necesita id ya que no es una entidad independiente
//Los atributos de esta clase seran columnas en la tabla camion_cisternas
//que se crea automaticamente al usar CollectionTable en la clase Camion
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cisterna {
	
	private String nombre;

	private Integer capacidad;
	
	
	
	
	

}
