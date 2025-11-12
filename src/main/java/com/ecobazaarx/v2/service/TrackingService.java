package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.model.Product;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.model.UserViewHistory;
import com.ecobazaarx.v2.repository.ProductRepository;
import com.ecobazaarx.v2.repository.UserRepository;
import com.ecobazaarx.v2.repository.UserViewHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final UserViewHistoryRepository viewHistoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void logProductView(Long productId, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        UserViewHistory newView = new UserViewHistory(user, product);
        viewHistoryRepository.save(newView);
    }
}
