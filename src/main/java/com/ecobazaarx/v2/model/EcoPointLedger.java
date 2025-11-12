package com.ecobazaarx.v2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "eco_point_ledger")
@Getter
@Setter
@NoArgsConstructor
public class EcoPointLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int pointsChanged;

    @Column(nullable = false, length = 100)
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    public EcoPointLedger(User user, int pointsChanged, String reason) {
        this.user = user;
        this.pointsChanged = pointsChanged;
        this.reason = reason;
        this.transactionDate = LocalDateTime.now();
    }
}
