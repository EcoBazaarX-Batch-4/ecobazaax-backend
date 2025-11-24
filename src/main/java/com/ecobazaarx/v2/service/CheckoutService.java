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

        // 4. Eco Points Logic
        int pointsToRedeem = checkoutRequest.getEcoPointsToRedeem();
        BigDecimal pointsAmountSaved = BigDecimal.ZERO;

        if (pointsToRedeem > 0) {
            if (user.getEcoPoints() < pointsToRedeem) {
                throw new IllegalStateException("Not enough Eco Points to redeem.");
            }
            pointsAmountSaved = new BigDecimal(pointsToRedeem).multiply(ECO_POINT_CONVERSION_RATE);
            if (pointsAmountSaved.compareTo(finalTotal) > 0) {
                pointsAmountSaved = finalTotal;
            }
            finalTotal = finalTotal.subtract(pointsAmountSaved);
        }

        // 5. Final Stock Check
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Not enough stock for: " + product.getName());
            }
        }

        // 6. Create Stripe Payment Intent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(finalTotal.multiply(new BigDecimal(100)).longValue())
                .setCurrency("inr")
                .setPaymentMethod(checkoutRequest.getPaymentMethodId())
                .addPaymentMethodType("card")
                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                .setConfirm(true)
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        if (!"succeeded".equals(paymentIntent.getStatus())) {
            return CheckoutResponse.builder()
                    .clientSecret(paymentIntent.getClientSecret())
                    .order(null)
                    .build();
        }

        // 7. Create the Order
        Order order = createOrderFromCart(user, cart, cartTotals, pointsToRedeem, pointsAmountSaved);
        Set<OrderItem> orderItems = new HashSet<>();

        int totalEcoPointsAwarded = 0;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            OrderItem orderItem = createOrderItemFromCartItem(order, product, cartItem);
            orderItems.add(orderItem);

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            totalEcoPointsAwarded += product.getEcoPoints() * cartItem.getQuantity();
        }
        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);

        // 10. Update User Stats
        updateUserRankStats(user, cartTotals.getProductsTotalCarbon().add(cartTotals.getShippingCarbon()));

        // 11. Update Points
        int finalEcoPoints = user.getEcoPoints() + totalEcoPointsAwarded - pointsToRedeem;
        user.setEcoPoints(finalEcoPoints);

        if (totalEcoPointsAwarded > 0) {
            gamificationService.addPointsForAction(user, totalEcoPointsAwarded, "Earned from Order #" + order.getId());
        }
        if (pointsToRedeem > 0) {
            gamificationService.addPointsForAction(user, -pointsToRedeem, "Redeemed on Order #" + order.getId());
        }

        // 12. Referral
        if (user.getTotalOrderCount() == 1 && user.getReferrer() != null) {
            User referrer = user.getReferrer();
            gamificationService.addPointsForAction(referrer, REFERRAL_BONUS_POINTS, "Referred new user: " + user.getName());
            userRepository.save(referrer);
        }
        userRepository.save(user);

        // 13. Clear Cart
        cart.getItems().clear();
        cart.setAppliedDiscount(null);
        cart.setShippingAddress(null);
        cart.setSelectedTransportZone(null);
        cartRepository.save(cart);

        return CheckoutResponse.builder()
                .clientSecret(null)
                .order(cartService.mapOrderToOrderDto(order))
                .build();
    }

    private Order createOrderFromCart(User user, Cart cart, CartResponse totals, int pointsRedeemed, BigDecimal pointsAmountSaved) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);
        order.setTotalAmount(totals.getGrandTotal().subtract(pointsAmountSaved));
        order.setTotalCarbonFootprint(totals.getProductsTotalCarbon().add(totals.getShippingCarbon()));

        if(cart.getAppliedDiscount() != null) {
            order.setDiscountCode(cart.getAppliedDiscount().getCode());
            order.setDiscountAmount(totals.getAppliedDiscount().getAmountSaved());
        }

        order.setEcoPointsRedeemed(pointsRedeemed);
        order.setEcoPointsAmount(pointsAmountSaved);

        Address addr = cart.getShippingAddress();
        String formattedAddress = String.format("%s: %s, %s, %s %s, %s",
                addr.getLabel(), addr.getStreet(), addr.getCity(), addr.getState(), addr.getPostalCode(), addr.getCountry());
        order.setShippingAddress(formattedAddress); // Save readable string

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

    private void updateUserRankStats(User user, BigDecimal orderCarbon) {
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
        if (totalOrderCount < 5) return 0;
        if (totalOrderCount < 15) return 1;
        if (totalOrderCount < 30) return 2;
        if (totalOrderCount < 50) return 3;
        if (totalOrderCount < 75) return 4;
        return 5;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}