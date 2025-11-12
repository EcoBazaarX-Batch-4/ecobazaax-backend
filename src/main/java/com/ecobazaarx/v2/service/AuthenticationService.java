package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.config.JwtService;
import com.ecobazaarx.v2.dto.AuthenticationResponse;
import com.ecobazaarx.v2.dto.LoginRequest;
import com.ecobazaarx.v2.dto.RegisterRequest;
import com.ecobazaarx.v2.model.Cart;
import com.ecobazaarx.v2.model.Role;
import com.ecobazaarx.v2.model.RoleName;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.repository.RoleRepository;
import com.ecobazaarx.v2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Error: Default role ROLE_CUSTOMER not found."));

        var user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        user.setRoles(Set.of(customerRole));
        user.setEcoPoints(0);
        user.setRankLevel(0);

        user.setReferralCode(generateUniqueReferralCode());

        if (request.getReferralCode() != null && !request.getReferralCode().isBlank()) {
            Optional<User> referrerOpt = userRepository.findByReferralCode(request.getReferralCode().toUpperCase());
            if (referrerOpt.isPresent()) {
                user.setReferrer(referrerOpt.get());
            }
        }

        Cart newCart = new Cart(user);
        user.setCart(newCart);

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    private String generateUniqueReferralCode() {
        String code;
        do {
            code = UUID.randomUUID().toString()
                    .replaceAll("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        } while (userRepository.existsByReferralCode(code));

        return code;
    }
}
