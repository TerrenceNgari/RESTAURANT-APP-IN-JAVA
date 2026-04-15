package com.restaurant.service;

import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import com.restaurant.model.OrderItem;
import com.restaurant.model.OrderType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderServiceTest {

    @Test
    void placeOrderCalculatesTotalAmount() {
        OrderService orderService = new OrderService();
        MenuItem burger = new MenuItem("M01", "Burger", MenuCategory.MAIN_COURSE, new BigDecimal("9.90"));
        MenuItem juice = new MenuItem("D01", "Juice", MenuCategory.DRINK, new BigDecimal("3.25"));

        var order = orderService.placeOrder(
                "Chris",
                OrderType.DINE_IN,
                List.of(new OrderItem(burger, 2), new OrderItem(juice, 1))
        );

        assertEquals(new BigDecimal("23.05"), order.getTotalAmount());
    }
}