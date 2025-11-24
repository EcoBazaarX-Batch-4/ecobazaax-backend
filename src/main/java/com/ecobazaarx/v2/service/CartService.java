package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.*;
import com.ecobazaarx.v2.model.*;
import com.ecobazaarx.v2.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final DiscountRepository discountRepository;
    private final AddressRepository addressRepository;
    private final TransportZoneRepository transportZoneRepository;
    private final TaxRateRepository taxRateRepository;
    private static final String WAREHOUSE_STATE = "Jharkhand";
    private static final String WAREHOUSE_CITY = "Ranchi";

    @Transactional
    public CartResponse addProductToCart(AddToCartRequest request, UserDetails userDetails) {
        User user = findUserByEmail(userDetails.getUsername());
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
        }

        Optional<CartItem> existingItemOpt = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalArgumentException("Not enough stock for new total quantity.");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return mapToCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCartForUser(UserDetails userDetails) {
        User user = findUserByEmail(userDetails.getUsername());
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user"));

        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItemQuantity(Long cartItemId, int newQuantity, UserDetails userDetails) {
        User user = findUserByEmail(userDetails.getUsername());
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("This item is not in your cart");
        }

        if (newQuantity <= 0) {
            cart.getItems().remove(cartItem);
            cartRepository.save(cart);
        } else {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalArgumentException("Not enough stock for quantity: " + newQuantity);
            }
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        }

        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse removeProductFromCart(Long cartItemId, UserDetails userDetails) {
        User user = findUserByEmail(userDetails.getUsername());
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("This item is not in your cart");
        }

        cart.getItems().remove(cartItem);
        cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse applyDiscount(String discountCode, UserDetails userDetails) {
        User user = findUserByEmail(userDetails.getUsername());
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Discount discount = discountRepository.findByCode(discountCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid discount code"));

        if (discount.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This discount code has expired.");
        }
        if (discount.getValidFrom().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("This discount code is not yet active.");
        }

        BigDecimal productsTotal = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (discount.getMinPurchaseAmount() != null &&
                productsTotal.compareTo(discount.getMinPurchaseAmount()) < 0) {
            throw new IllegalArgumentException("Cart total does not meet the minimum purchase amount for this discount.");
        }

        cart.setAppliedDiscount(discount);
        cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public List<DiscountResponse> getAvailableDiscounts() {
        return discountRepository.findActiveDiscounts(LocalDateTime.now()).stream()
                .map(d -> DiscountResponse.builder()
                        .code(d.getCode())
                        .amountSaved(d.getValue()) // reusing field for value/percentage
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShippingOptionDto> getShippingOptions(Long addressId, UserDetails userDetails) {
        User user = findUserByEmail(userDetails.getUsername());
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("This is not your address.");
        }

        TransportZone zone = findShippingZone(address);

        ShippingOptionDto option = ShippingOptionDto.builder()
                .transportZoneId(zone.getId())
                .name("Standard Ground (" + zone.getName() + ")")
                .cost(zone.getCost())
                .carbonFootprint(zone.getFlatCarbonFootprint())
                .build();

        return Collections.singletonList(option);
    }

    @Transactional
    public CartResponse selectShippingOption(SelectShippingRequest request, UserDetails userDetails) {
        User user = findUserByEmail(userDetails.getUsername());
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        TransportZone zone = findShippingZone(address);

        cart.setShippingAddress(address);
        cart.setSelectedTransportZone(zone);
        cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    private TransportZone findShippingZone(Address address) {
        String zoneName;
        if (address.getCountry().equalsIgnoreCase("India")) {
            if (address.getState().equalsIgnoreCase(WAREHOUSE_STATE)) {
                if (address.getCity().equalsIgnoreCase(WAREHOUSE_CITY)) {
                    zoneName = "Intra-city";
                } else {
                    zoneName = "Intra-state";
                }
            } else {
                zoneName = "Inter-state";
            }
        } else {
            zoneName = "Inter-state";
        }

        return transportZoneRepository.findByName(zoneName)
                .orElseThrow(() -> new EntityNotFoundException("Shipping zone not configured: " + zoneName));
    }

    CartResponse mapToCartResponse(Cart cart) {

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal productsTotalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal productsTotalCarbon = itemResponses.stream()
                .map(CartItemResponse::getSubtotalCarbonFootprint)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DiscountResponse discountResponse = null;
        BigDecimal amountSaved = BigDecimal.ZERO;
        Discount appliedDiscount = cart.getAppliedDiscount();

        if (appliedDiscount != null) {
            if (appliedDiscount.getDiscountType() == DiscountType.PERCENTAGE) {
                // --- FIX: Added RoundingMode to prevent ArithmeticException ---
                amountSaved = productsTotalAmount.multiply(
                        appliedDiscount.getValue().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)
                );
                // -------------------------------------------------------------
            } else if (appliedDiscount.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                amountSaved = appliedDiscount.getValue();
            }
            if (amountSaved.compareTo(productsTotalAmount) > 0) {
                amountSaved = productsTotalAmount;
            }

            discountResponse = DiscountResponse.builder()
                    .code(appliedDiscount.getCode())
                    .amountSaved(amountSaved.setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        BigDecimal shippingCost = BigDecimal.ZERO;
        BigDecimal shippingCarbon = BigDecimal.ZERO;
        AddressDto shippingAddressDto = null;

        if (cart.getShippingAddress() != null) {
            shippingAddressDto = mapToAddressDto(cart.getShippingAddress());
        }
        if (cart.getSelectedTransportZone() != null) {
            shippingCost = cart.getSelectedTransportZone().getCost();
            shippingCarbon = cart.getSelectedTransportZone().getFlatCarbonFootprint();
        }

        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal taxableAmount = productsTotalAmount.subtract(amountSaved).add(shippingCost);

        if (cart.getShippingAddress() != null) {
            TaxRate taxRate = taxRateRepository.findByName("GST").orElse(null);
            if (taxRate != null) {
                // FIX: Rounding Mode here too just in case
                taxAmount = taxableAmount.multiply(taxRate.getRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
            }
        }

        BigDecimal grandTotal = taxableAmount.add(taxAmount);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .productsTotalAmount(productsTotalAmount.setScale(2, RoundingMode.HALF_UP))
                .productsTotalCarbon(productsTotalCarbon.setScale(2, RoundingMode.HALF_UP))
                .appliedDiscount(discountResponse)
                .shippingCost(shippingCost.setScale(2, RoundingMode.HALF_UP))
                .shippingCarbon(shippingCarbon.setScale(2, RoundingMode.HALF_UP))
                .shippingAddress(shippingAddressDto)
                .taxAmount(taxAmount.setScale(2, RoundingMode.HALF_UP))
                .grandTotal(grandTotal.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        Product product = item.getProduct();
        BigDecimal quantity = new BigDecimal(item.getQuantity());
        BigDecimal subtotal = product.getPrice().multiply(quantity);
        BigDecimal subtotalCarbon = product.getCradleToWarehouseFootprint().multiply(quantity);

        return CartItemResponse.builder()
                .cartItemId(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .itemCarbonFootprint(product.getCradleToWarehouseFootprint())
                .subtotalCarbonFootprint(subtotalCarbon)
                .build();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private AddressDto mapToAddressDto(Address entity) {
        AddressDto dto = new AddressDto();
        dto.setId(entity.getId());
        dto.setLabel(entity.getLabel());
        dto.setStreet(entity.getStreet());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPostalCode(entity.getPostalCode());
        dto.setCountry(entity.getCountry());
        dto.setDefault(entity.isDefault());
        return dto;
    }

    OrderDto mapOrderToOrderDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .totalCarbonFootprint(order.getTotalCarbonFootprint())
                .orderItems(order.getOrderItems().stream()
                        .map(this::mapOrderItemToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderItemDto mapOrderItemToDto(OrderItem item) {
        return OrderItemDto.builder()
                .productId(item.getProduct().getId())                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .pricePerItem(item.getPricePerItem())
                .carbonFootprintPerItem(item.getCarbonFootprintPerItem())
                .build();
    }
}