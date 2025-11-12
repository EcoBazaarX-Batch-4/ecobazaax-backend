package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.ReviewRequest;
import com.ecobazaarx.v2.dto.ReviewResponse;
import com.ecobazaarx.v2.model.Product;
import com.ecobazaarx.v2.model.ProductReview;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.repository.OrderRepository;
import com.ecobazaarx.v2.repository.ProductRepository;
import com.ecobazaarx.v2.repository.ProductReviewRepository;
import com.ecobazaarx.v2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final GamificationService gamificationService;
    private static final int POINTS_FOR_REVIEW = 25;

    @Transactional
    public ReviewResponse addReview(Long productId, ReviewRequest request, UserDetails currentUser) {
        User user = findUserByEmail(currentUser.getUsername());
        Product product = findProductById(productId);

        if (!orderRepository.hasUserPurchasedProduct(user.getId(), productId)) {
            throw new AccessDeniedException("You can only review products you have purchased and received.");
        }

        if (reviewRepository.existsByProductIdAndUserId(productId, user.getId())) {
            throw new IllegalStateException("You have already submitted a review for this product.");
        }

        ProductReview review = new ProductReview(product, user, request.getRating(), request.getComment());
        ProductReview savedReview = reviewRepository.save(review);

        gamificationService.addPointsForAction(
                user,
                POINTS_FOR_REVIEW,
                "Wrote a review for product: " + product.getName()
        );

        return mapToResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviews(Long productId, Pageable pageable) {
        Page<ProductReview> reviewPage = reviewRepository.findByProductId(productId, pageable);
        return reviewPage.map(this::mapToResponse);
    }

    private void updateProductRating(Product product) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(product.getId());
        long reviewCount = reviewRepository.countByProductId(product.getId());

        if (avgRating != null) {
            product.setAverageRating(
                    BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP)
            );
        }
        product.setTotalReviews((int) reviewCount);
        productRepository.save(product);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private ReviewResponse mapToResponse(ProductReview review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .userName(review.getUser().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
