package com.restaurant.service;

import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MenuService {
    private final List<MenuItem> menuItems = new ArrayList<>();

    public MenuService() {
        seedMenu();
    }

    public List<MenuItem> getAllItems() {
        return menuItems.stream()
                .sorted(Comparator.comparing(MenuItem::category).thenComparing(MenuItem::name))
                .toList();
    }

    public Optional<MenuItem> findByCode(String code) {
        return menuItems.stream()
                .filter(item -> item.code().equalsIgnoreCase(code))
                .findFirst();
    }

    private void seedMenu() {
        menuItems.add(new MenuItem("S01", "Tomato Soup", MenuCategory.STARTER, new BigDecimal("4.50")));
        menuItems.add(new MenuItem("S02", "Chicken Wings", MenuCategory.STARTER, new BigDecimal("6.90")));
        menuItems.add(new MenuItem("M01", "Beef Burger", MenuCategory.MAIN_COURSE, new BigDecimal("9.90")));
        menuItems.add(new MenuItem("M02", "Grilled Tilapia", MenuCategory.MAIN_COURSE, new BigDecimal("12.50")));
        menuItems.add(new MenuItem("M03", "Vegetable Pasta", MenuCategory.MAIN_COURSE, new BigDecimal("8.75")));
        menuItems.add(new MenuItem("D01", "Fresh Juice", MenuCategory.DRINK, new BigDecimal("3.25")));
        menuItems.add(new MenuItem("D02", "Lemonade", MenuCategory.DRINK, new BigDecimal("2.80")));
        menuItems.add(new MenuItem("DS1", "Chocolate Cake", MenuCategory.DESSERT, new BigDecimal("4.20")));
    }
}