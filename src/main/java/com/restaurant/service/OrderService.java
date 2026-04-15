package com.restaurant.service;

import com.restaurant.model.CustomerOrder;
import com.restaurant.model.OrderItem;
import com.restaurant.model.OrderStatus;
import com.restaurant.model.OrderType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final List<CustomerOrder> orders = new ArrayList<>();
    private int nextOrderId = 1;

    public synchronized CustomerOrder placeOrder(String customerName, OrderType orderType, List<OrderItem> items) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        CustomerOrder order = new CustomerOrder(nextOrderId++, customerName.trim(), orderType, items);
        orders.add(order);
        return order;
    }

    public synchronized List<CustomerOrder> getAllOrders() {
        return List.copyOf(orders);
    }

    public synchronized Optional<CustomerOrder> findOrderById(int orderId) {
        return orders.stream()
                .filter(order -> order.getOrderId() == orderId)
                .findFirst();
    }

    public synchronized boolean updateStatus(int orderId, OrderStatus status) {
        Optional<CustomerOrder> order = findOrderById(orderId);
        if (order.isEmpty()) {
            return false;
        }

        order.get().setStatus(status);
        return true;
    }
}