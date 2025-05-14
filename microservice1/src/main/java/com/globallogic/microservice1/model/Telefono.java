package com.globallogic.microservice1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "telefonos")
@Getter
@Setter
@NoArgsConstructor
public class Telefono {
    
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long number;

    @Column(nullable = false)
    private Integer citycode;

    @Column(nullable = false, length = 10)
    private String contrycode;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) 
    private Usuario usuario;  
}
