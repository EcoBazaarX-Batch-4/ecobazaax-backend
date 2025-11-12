package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.ProductResponseDto;
import com.ecobazaarx.v2.model.Product;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.model.Wishlist;
import com.ecobazaarx.v2.repository.ProductRepository;
import com.ecobazaarx.v2.repository.UserRepository;
import com.ecobazaarx.v2.repository.WishlistRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getWishlist(UserDetails currentUser) {
        User user = findUserByEmail(currentUser.getUsername());

        List<Wishlist> items = wishlistRepository.findByUserId(user.getId());

        return items.stream()
                .map(Wishlist::getProduct)
                .map(productService::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto addProductToWishlist(UserDetails currentUser, Long productId) {
        User user = findUserByEmail(currentUser.getUsername());
        Product product = findProductById(productId);

        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndProductId(user.getId(), productId);
        if (existing.isPresent()) {
            return productService.mapToResponseDto(product);
        }

        Wishlist wishlistItem = new Wishlist(user, product);
        wishlistRepository.save(wishlistItem);

        return productService.mapToResponseDto(product);
    }

    @Transactional
    public void removeProductFromWishlist(UserDetails currentUser, Long productId) {
        User user = findUserByEmail(currentUser.getUsername());

        Wishlist wishlistItem = wishlistRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found in wishlist"));

        wishlistRepository.delete(wishlistItem);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }
}
