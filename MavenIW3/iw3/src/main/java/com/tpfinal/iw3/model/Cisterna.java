package com.tpfinal.iw3.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cisterna {
	
	
	private String nombre;
	private Integer capacidad;
	
	
	
	
	

}
