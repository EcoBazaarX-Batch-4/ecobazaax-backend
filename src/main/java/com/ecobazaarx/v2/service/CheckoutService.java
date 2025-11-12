package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.CartResponse;
import com.ecobazaarx.v2.dto.CheckoutRequest;
import com.ecobazaarx.v2.dto.CheckoutResponse;
import com.ecobazaarx.v2.model.*;
import com.ecobazaarx.v2.repository.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final GamificationService gamificationService;

    private static final int REFERRAL_BONUS_POINTS = 500;
    // This is our conversion rate: 100 points = 1.00 (e.g., in INR)
    private static final BigDecimal ECO_POINT_CONVERSION_RATE = new BigDecimal("0.01");

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional(rollbackFor = Exception.class)
    public CheckoutResponse placeOrder(CheckoutRequest checkoutRequest, UserDetails userDetails) throws StripeException {
        // 1. Get User and Cart
        User user = findUserByEmail(userDetails.getUsername());
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        // 2. Validate Cart
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }
        if (cart.getShippingAddress() == null) {
            throw new IllegalStateException("Please select a shipping address");
        }

        // 3. Get Final Totals (Pre-Points)
        CartResponse cartTotals = cartService.mapToCartResponse(cart);
        BigDecimal finalTotal = cartTotals.getGrandTotal();

        // 4. NEW: Eco Points Logic
        int pointsToRedeem = checkoutRequest.getEcoPointsToRedeem();
        BigDecimal pointsAmountSaved = BigDecimal.ZERO;

        if (pointsToRedeem > 0) {
            // Check if user has enough points
            if (user.getEcoPoints() < pointsToRedeem) {
                throw new IllegalStateException("Not enough Eco Points to redeem.");
            }

            // Calculate the monetary value
            pointsAmountSaved = new BigDecimal(pointsToRedeem).multiply(ECO_POINT_CONVERSION_RATE);

            // Ensure points don't save more than the grand total
            if (pointsAmountSaved.compareTo(finalTotal) > 0) {
                pointsAmountSaved = finalTotal;
                // In a V3, we would recalculate 'pointsToRedeem' here
            }

            // Subtract from the final total
            finalTotal = finalTotal.subtract(pointsAmountSaved);
        }
        // ---------------------------------

        // 5. Final Stock Check
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Not enough stock for: " + product.getName());
            }
        }

        // 6. Create Stripe Payment Intent (with the FINAL total)
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(finalTotal.multiply(new BigDecimal(100)).longValue()) // Stripe wants cents
                .setCurrency("inr")
                .setPaymentMethod(checkoutRequest.getPaymentMethodId())
                .addPaymentMethodType("card")
                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                .setConfirm(true)
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        if (!"succeeded".equals(paymentIntent.getStatus())) {
            // Payment needs 3D Secure or other steps
            return CheckoutResponse.builder()
                    .clientSecret(paymentIntent.getClientSecret())
                    .order(null)
                    .build();
        }

        // --- PAYMENT WAS SUCCESSFUL, COMMIT THE ORDER ---

        // 7. Create the Order
        Order order = createOrderFromCart(user, cart, cartTotals, pointsToRedeem, pointsAmountSaved);
        Set<OrderItem> orderItems = new HashSet<>();

        int totalEcoPointsAwarded = 0;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // 8. Create OrderItems (Snapshotting)
            OrderItem orderItem = createOrderItemFromCartItem(order, product, cartItem);
            orderItems.add(orderItem);

            // 9. Update Stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            totalEcoPointsAwarded += product.getEcoPoints() * cartItem.getQuantity();
        }
        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);

        // 10. Update User Rank
        updateUserRankStats(user, cartTotals.getProductsTotalCarbon().add(cartTotals.getShippingCarbon()));

        // 11. Update User Points & Ledger
        int finalEcoPoints = user.getEcoPoints() + totalEcoPointsAwarded - pointsToRedeem;
        user.setEcoPoints(finalEcoPoints);

        if (totalEcoPointsAwarded > 0) {
            gamificationService.addPointsForAction(user, totalEcoPointsAwarded, "Earned from Order #" + order.getId());
        }
        if (pointsToRedeem > 0) {
            gamificationService.addPointsForAction(user, -pointsToRedeem, "Redeemed on Order #" + order.getId());
        }

        // 12. Check for Referral
        if (user.getTotalOrderCount() == 1 && user.getReferrer() != null) {
            User referrer = user.getReferrer();
            gamificationService.addPointsForAction(
                    referrer,
                    REFERRAL_BONUS_POINTS,
                    "Referred new user: " + user.getName()
            );
            userRepository.save(referrer);
        }
        userRepository.save(user);

        // 13. Clear the Cart
        cart.getItems().clear();
        cart.setAppliedDiscount(null);
        cart.setShippingAddress(null);
        cart.setSelectedTransportZone(null);
        cartRepository.save(cart);

        // 14. Return the new Order
        return CheckoutResponse.builder()
                .clientSecret(null) // Payment was instant
                .order(cartService.mapOrderToOrderDto(order))
                .build();
    }

    // --- UPDATED HELPER ---
    private Order createOrderFromCart(User user, Cart cart, CartResponse totals, int pointsRedeemed, BigDecimal pointsAmountSaved) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);

        // Set the final charged amount
        order.setTotalAmount(totals.getGrandTotal().subtract(pointsAmountSaved));

        BigDecimal totalCarbon = totals.getProductsTotalCarbon().add(totals.getShippingCarbon());
        order.setTotalCarbonFootprint(totalCarbon);

        if(cart.getAppliedDiscount() != null) {
            order.setDiscountCode(cart.getAppliedDiscount().getCode());
            order.setDiscountAmount(totals.getAppliedDiscount().getAmountSaved());
        }

        // --- ADDED NEW FIELDS ---
        order.setEcoPointsRedeemed(pointsRedeemed);
        order.setEcoPointsAmount(pointsAmountSaved);
        // ----------------------

        order.setShippingAddress(cart.getShippingAddress().toString()); // Snapshot the address
        order.setShippingCost(totals.getShippingCost());
        order.setTaxAmount(totals.getTaxAmount());

        return orderRepository.save(order);
    }

    private OrderItem createOrderItemFromCartItem(Order order, Product product, CartItem cartItem) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(cartItem.getQuantity());

        item.setProductName(product.getName());
        item.setPricePerItem(product.getPrice());
        item.setCarbonFootprintPerItem(product.getCradleToWarehouseFootprint());

        return item;
    }

    // --- UPDATED HELPER (Renamed from updateUserStats) ---
    private void updateUserRankStats(User user, BigDecimal orderCarbon) {
        // This helper ONLY handles ranking, not points.

        int newTotalOrderCount = user.getTotalOrderCount() + 1;
        BigDecimal newLifetimeTotalCarbon = user.getLifetimeTotalCarbon().add(orderCarbon);

        BigDecimal newLifetimeAverageCarbon = newLifetimeTotalCarbon.divide(
                new BigDecimal(newTotalOrderCount), 4, RoundingMode.HALF_UP
        );

        int newRankLevel = calculateRankLevel(newTotalOrderCount);

        if (newRankLevel > user.getRankLevel()) {
            user.setRankLevelAchievedAt(LocalDateTime.now());
        }

        user.setTotalOrderCount(newTotalOrderCount);
        user.setLifetimeTotalCarbon(newLifetimeTotalCarbon);
        user.setLifetimeAverageCarbon(newLifetimeAverageCarbon);
        user.setRankLevel(newRankLevel);
    }

    private int calculateRankLevel(int totalOrderCount) {
        if (totalOrderCount < 5) return 0;   // 0-4 (Sprout)
        if (totalOrderCount < 15) return 1;  // 5-14 (Sapling)
        if (totalOrderCount < 30) return 2;  // 15-29 (Guardian)
        if (totalOrderCount < 50) return 3;  // 30-49 (Eco-Hero)
        if (totalOrderCount < 75) return 4;  // 50-74 (Earth-Warden)

        return 5; // 75+ (Planet-Savior)
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}