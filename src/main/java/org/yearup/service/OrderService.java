package org.yearup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yearup.models.*;
import org.yearup.repository.OrderLineItemRepository;
import org.yearup.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final ShoppingCartService shoppingCartService;
    private final ProfileService profileService;

    public OrderService(OrderRepository orderRepository,
                        OrderLineItemRepository orderLineItemRepository,
                        ShoppingCartService shoppingCartService,
                        ProfileService profileService) {
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.shoppingCartService = shoppingCartService;
        this.profileService = profileService;
    }

    @Transactional
    public Order createOrder(int userId) {
        ShoppingCart cart = shoppingCartService.getByUserId(userId);

        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create an order from an empty cart.");
        }

        Profile profile = profileService.getById(userId);

        Order newOrder = new Order();
        newOrder.setUserId(userId);
        newOrder.setDate(LocalDateTime.now());
        newOrder.setShippingAmount(0.0);
        if (profile != null) {
            newOrder.setAddress(profile.getAddress());
            newOrder.setCity(profile.getCity());
            newOrder.setState(profile.getState());
            newOrder.setZip(profile.getZip());
        }

        Order savedOrder = orderRepository.save(newOrder);

        for (Map.Entry<Integer, ShoppingCartItem> entry : cart.getItems().entrySet()) {
            ShoppingCartItem cartItem = entry.getValue();
            Product product = cartItem.getProduct();

            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setOrderId(savedOrder.getOrderId());
            lineItem.setProductId(product.getProductId());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setSalesPrice(product.getPrice());
            lineItem.setDiscount(cartItem.getDiscountPercent());

            orderLineItemRepository.save(lineItem);
        }

        shoppingCartService.clearCart(userId);

        return savedOrder;
    }
}