package com.ecobazaarx.v2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "manufacturing_processes")
@Getter
@Setter
@NoArgsConstructor
public class ManufacturingProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal emissionPerKg;

    public ManufacturingProcess(String name, BigDecimal emissionPerKg) {
        this.name = name;
        this.emissionPerKg = emissionPerKg;
    }

}
