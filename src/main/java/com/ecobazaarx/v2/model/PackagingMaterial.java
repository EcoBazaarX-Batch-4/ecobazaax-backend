package com.ecobazaarx.v2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "packaging_materials")
@Getter
@Setter
@NoArgsConstructor
public class PackagingMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // packaging material
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // carbon factor per kg
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal emissionPerKg;
}
