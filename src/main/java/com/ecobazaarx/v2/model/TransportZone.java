package com.ecobazaarx.v2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transport_zones")
@Getter
@Setter
@NoArgsConstructor
public class TransportZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // zones: intra-city, intra-state, inter-state
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal flatCarbonFootprint;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;
}
