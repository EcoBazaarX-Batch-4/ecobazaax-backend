package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.HomepageRecommendationDto;
import com.ecobazaarx.v2.dto.ProductResponseDto;
import com.ecobazaarx.v2.model.Product;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.repository.OrderItemRepository;
import com.ecobazaarx.v2.repository.ProductRepository;
import com.ecobazaarx.v2.repository.UserRepository;
import com.ecobazaarx.v2.repository.UserViewHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final UserViewHistoryRepository viewHistoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public HomepageRecommendationDto getHomepageRecommendations() {

        Pageable top10 = PageRequest.of(0, 10);
        List<Product> topSellers = orderItemRepository.findTopSellingProductsGlobal(top10);
        List<Product> newArrivals = productRepository.findByOrderByCreatedAtDesc(top10);
        List<Product> topRated = productRepository.findByOrderByAverageRatingDesc(top10);
        List<Product> lowestCarbon = productRepository.findByOrderByCradleToWarehouseFootprintAsc(top10);
        List<Product> ecoRewards = productRepository.findByOrderByEcoPointsDesc(top10);

        return HomepageRecommendationDto.builder()
                .topSellers(mapProductListToDto(topSellers))
                .newArrivals(mapProductListToDto(newArrivals))
                .topRated(mapProductListToDto(topRated))
                .topLowestCarbon(mapProductListToDto(lowestCarbon))
                .topEcoRewards(mapProductListToDto(ecoRewards))
                .build();
    }

    private List<ProductResponseDto> mapProductListToDto(List<Product> products) {
        return products.stream()
                .map(productService::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getRecentlyViewed(UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Pageable top10 = PageRequest.of(0, 10);
        List<Product> products = viewHistoryRepository.findRecentlyViewedProductsByUserId(user.getId(), top10);

        return products.stream()
                .map(productService::mapToResponseDto)
                .collect(Collectors.toList());
    }
}
