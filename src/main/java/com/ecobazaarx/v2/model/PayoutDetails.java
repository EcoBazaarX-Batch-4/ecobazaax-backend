package com.ecobazaarx.v2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payout_details")
@Getter
@Setter
@NoArgsConstructor
public class PayoutDetails {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // This makes the 'id' field both PK and FK
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false, length = 11)
    private String ifscCode;

    public PayoutDetails(User user) {
        this.user = user;
    }
}
