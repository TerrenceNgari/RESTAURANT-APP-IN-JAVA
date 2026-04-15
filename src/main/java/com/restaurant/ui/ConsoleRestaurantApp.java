package com.restaurant.ui;

import com.restaurant.model.CustomerOrder;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import com.restaurant.model.OrderItem;
import com.restaurant.model.OrderStatus;
import com.restaurant.model.OrderType;
import com.restaurant.service.MenuService;
import com.restaurant.service.OrderService;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleRestaurantApp {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MenuService menuService = new MenuService();
    private final OrderService orderService = new OrderService();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        boolean running = true;

        printBanner();
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> displayMenu();
                case "2" -> createOrder();
                case "3" -> displayOrders();
                case "4" -> updateOrderStatus();
                case "5" -> {
                    running = false;
                    System.out.println("Goodbye.");
                }
                default -> System.out.println("Choose a valid option.");
            }
        }
    }

    private void printBanner() {
        System.out.println("====================================");
        System.out.println("   Restaurant Food Ordering System  ");
        System.out.println("====================================");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("1. View menu");
        System.out.println("2. Place an order");
        System.out.println("3. View all orders");
        System.out.println("4. Update order status");
        System.out.println("5. Exit");
        System.out.print("Select an option: ");
    }

    private void displayMenu() {
        Map<MenuCategory, List<MenuItem>> groupedItems = new EnumMap<>(MenuCategory.class);
        for (MenuItem item : menuService.getAllItems()) {
            groupedItems.computeIfAbsent(item.category(), ignored -> new ArrayList<>()).add(item);
        }

        System.out.println();
        System.out.println("----- MENU -----");
        for (MenuCategory category : MenuCategory.values()) {
            List<MenuItem> items = groupedItems.get(category);
            if (items == null || items.isEmpty()) {
                continue;
            }

            System.out.println(category.name().replace('_', ' '));
            for (MenuItem item : items) {
                System.out.printf("  %s | %-20s | $%s%n",
                        item.code(),
                        item.name(),
                        item.price().setScale(2, RoundingMode.HALF_UP));
            }
        }
    }

    private void createOrder() {
        System.out.print("Customer name: ");
        String customerName = scanner.nextLine().trim();
        OrderType orderType = promptOrderType();
        List<OrderItem> items = new ArrayList<>();

        displayMenu();
        while (true) {
            System.out.print("Enter menu code or type done: ");
            String code = scanner.nextLine().trim();

            if (code.equalsIgnoreCase("done")) {
                break;
            }

            MenuItem menuItem = menuService.findByCode(code).orElse(null);
            if (menuItem == null) {
                System.out.println("Menu code not found.");
                continue;
            }

            int quantity = promptQuantity();
            items.add(new OrderItem(menuItem, quantity));
            System.out.printf("Added %d x %s%n", quantity, menuItem.name());
        }

        if (items.isEmpty()) {
            System.out.println("Order cancelled because no items were added.");
            return;
        }

        CustomerOrder order = orderService.placeOrder(customerName, orderType, items);
        System.out.println();
        System.out.printf("Order #%d created for %s%n", order.getOrderId(), order.getCustomerName());
        System.out.printf("Total: $%s%n", order.getTotalAmount().setScale(2, RoundingMode.HALF_UP));
    }

    private OrderType promptOrderType() {
        while (true) {
            System.out.println("Order type: 1=Dine In, 2=Takeaway, 3=Delivery");
            System.out.print("Choose order type: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    return OrderType.DINE_IN;
                }
                case "2" -> {
                    return OrderType.TAKEAWAY;
                }
                case "3" -> {
                    return OrderType.DELIVERY;
                }
                default -> System.out.println("Choose 1, 2, or 3.");
            }
        }
    }

    private int promptQuantity() {
        while (true) {
            System.out.print("Quantity: ");
            String input = scanner.nextLine().trim();
            try {
                int quantity = Integer.parseInt(input);
                if (quantity > 0) {
                    return quantity;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Enter a whole number greater than zero.");
        }
    }

    private void displayOrders() {
        List<CustomerOrder> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders have been placed yet.");
            return;
        }

        System.out.println();
        System.out.println("----- ORDERS -----");
        for (CustomerOrder order : orders) {
            System.out.printf("#%d | %s | %s | %s | $%s | %s%n",
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getOrderType(),
                    order.getStatus(),
                    order.getTotalAmount().setScale(2, RoundingMode.HALF_UP),
                    order.getCreatedAt().format(DATE_TIME_FORMATTER));
            for (OrderItem item : order.getItems()) {
                System.out.printf("  - %s x%d = $%s%n",
                        item.getMenuItem().name(),
                        item.getQuantity(),
                        item.getLineTotal().setScale(2, RoundingMode.HALF_UP));
            }
        }
    }

    private void updateOrderStatus() {
        List<CustomerOrder> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders available to update.");
            return;
        }

        displayOrders();
        System.out.print("Enter order id: ");
        String input = scanner.nextLine().trim();

        try {
            int orderId = Integer.parseInt(input);
            OrderStatus status = promptOrderStatus();
            boolean updated = orderService.updateStatus(orderId, status);
            if (updated) {
                System.out.println("Order status updated.");
            } else {
                System.out.println("Order not found.");
            }
        } catch (NumberFormatException exception) {
            System.out.println("Order id must be a number.");
        }
    }

    private OrderStatus promptOrderStatus() {
        while (true) {
            System.out.println("Status: 1=Received, 2=Preparing, 3=Ready, 4=Completed");
            System.out.print("Choose new status: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    return OrderStatus.RECEIVED;
                }
                case "2" -> {
                    return OrderStatus.PREPARING;
                }
                case "3" -> {
                    return OrderStatus.READY;
                }
                case "4" -> {
                    return OrderStatus.COMPLETED;
                }
                default -> System.out.println("Choose 1, 2, 3, or 4.");
            }
        }
    }
}