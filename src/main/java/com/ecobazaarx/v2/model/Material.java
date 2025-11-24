package com.ecobazaarx.v2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "materials")
@Getter
@Setter
@NoArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // raw material  name
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // emission factor per kg
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal emissionPerKg;

    // source for the information
    @Column(length = 255)
    private String source;

    public Material(String name, BigDecimal emissionPerKg, String source) {
        this.name = name;
        this.emissionPerKg = emissionPerKg;
        this.source = source;
    }
}
