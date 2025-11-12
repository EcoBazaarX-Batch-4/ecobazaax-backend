package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.OrderDto;
import com.ecobazaarx.v2.dto.OrderItemDto;
import com.ecobazaarx.v2.model.Order;
import com.ecobazaarx.v2.model.OrderItem;
import com.ecobazaarx.v2.model.OrderStatus;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.repository.OrderRepository;
import com.ecobazaarx.v2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<OrderDto> getMyOrderHistory(UserDetails currentUser, int page, int size) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orderPage = orderRepository.findByUserIdOrderByOrderDateDesc(user.getId(), pageable);

        return orderPage.map(this::mapOrderToOrderDto);
    }

    @Transactional(readOnly = true)
    public OrderDto getMyOrderDetails(UserDetails currentUser, Long orderId) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You do not own this order");
        }

        return mapOrderToOrderDto(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrdersAsAdmin(
            String email,
            OrderStatus status,
            Pageable pageable
    ) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (email != null && !email.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("user").get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        return orderPage.map(this::mapOrderToOrderDto);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        return mapOrderToOrderDto(savedOrder);
    }

    private OrderDto mapOrderToOrderDto(Order order) {
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
