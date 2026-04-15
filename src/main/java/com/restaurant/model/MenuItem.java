package com.restaurant.model;

import java.math.BigDecimal;

public record MenuItem(String code, String name, MenuCategory category, BigDecimal price) {
}