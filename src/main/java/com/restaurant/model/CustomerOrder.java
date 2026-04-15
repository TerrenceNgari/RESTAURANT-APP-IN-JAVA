package com.restaurant.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerOrder {
    private final int orderId;
    private final String customerName;
    private final OrderType orderType;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private OrderStatus status;

    public CustomerOrder(int orderId, String customerName, OrderType orderType, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderType = orderType;
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.RECEIVED;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}