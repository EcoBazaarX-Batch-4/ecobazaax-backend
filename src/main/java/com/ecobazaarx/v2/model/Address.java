package com.ecobazaarx.v2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    // user can label them, as, "Home", "Work"
    @Column(nullable = false, length = 50)
    private String label;

    // mark one address as the default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDefault = false;
}
