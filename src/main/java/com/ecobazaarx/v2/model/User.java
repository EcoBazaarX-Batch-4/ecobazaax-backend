package com.ecobazaarx.v2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users_tb", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(nullable = true, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime rankLevelAchievedAt;

    @Column(length = 15, unique = true)
    private String referralCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_by_user_id")
    private User referrer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private SellerApplicationStatus sellerStatus = SellerApplicationStatus.NOT_APPLICABLE;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer ecoPoints = 0;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer rankLevel = 0;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer totalOrderCount = 0;

    @Column(nullable = false, columnDefinition = "decimal(19,4) default 0.0")
    private BigDecimal lifetimeTotalCarbon = BigDecimal.ZERO;

    @Column(nullable = false, columnDefinition = "decimal(19,4) default 0.0")
    private BigDecimal lifetimeAverageCarbon = BigDecimal.ZERO;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isAccountNonExpired = true;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isAccountNonLocked = true;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isCredentialsNonExpired = true;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isEnabled = true;

    @Column(length = 100)
    private String storeName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String storeDescription;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PayoutDetails payoutDetails;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();

        this.sellerStatus = SellerApplicationStatus.NOT_APPLICABLE;
        this.ecoPoints = 0;
        this.rankLevel = 0;
        this.totalOrderCount = 0;
        this.lifetimeTotalCarbon = BigDecimal.ZERO;
        this.lifetimeAverageCarbon = BigDecimal.ZERO;
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
