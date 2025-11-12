package com.ecobazaarx.v2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, columnDefinition = "integer default 1")
    private Integer stockQuantity;

    @Column(length = 1000)
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer ecoPoints = 0;

    @Column(nullable = false, precision = 10, scale = 2, columnDefinition = "decimal(10,2) default 0.0")
    private BigDecimal cradleToWarehouseFootprint = BigDecimal.ZERO;

    @Column(nullable = false, columnDefinition = "decimal(3,2) default 0.0")
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int totalReviews = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_zone_id", nullable = false)
    private TransportZone transportZone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_manufacturing_processes",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "process_id")
    )
    private Set<ManufacturingProcess> manufacturingProcesses = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductManufacturing> manufacturing = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductMaterial> materials = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductPackaging> packaging = new HashSet<>();
}
