package com.restaurant.web;

import com.restaurant.model.CustomerOrder;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import com.restaurant.model.OrderItem;
import com.restaurant.model.OrderStatus;
import com.restaurant.model.OrderType;
import com.restaurant.service.MenuService;
import com.restaurant.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Controller
public class RestaurantController {
    private final MenuService menuService;
    private final OrderService orderService;

    public RestaurantController(MenuService menuService, OrderService orderService) {
        this.menuService = menuService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String index(Model model) {
        if (!model.containsAttribute("orderForm")) {
            model.addAttribute("orderForm", buildOrderForm());
        }

        populatePage(model);
        return "index";
    }

    @PostMapping("/orders")
    public String placeOrder(@Valid @ModelAttribute("orderForm") OrderForm orderForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        List<OrderItem> orderItems = mapSelectedItems(orderForm);

        if (orderItems.isEmpty()) {
            bindingResult.reject("items", "Select at least one menu item with quantity above zero.");
        }

        if (bindingResult.hasErrors()) {
            restoreMissingCodes(orderForm);
            populatePage(model);
            return "index";
        }

        CustomerOrder order = orderService.placeOrder(orderForm.getCustomerName(), orderForm.getOrderType(), orderItems);
        redirectAttributes.addFlashAttribute("successMessage",
                "Order #" + order.getOrderId() + " created successfully for " + order.getCustomerName() + ".");
        return "redirect:/";
    }

    @PostMapping("/orders/{orderId}/status")
    public String updateStatus(@PathVariable int orderId,
                               @ModelAttribute("status") OrderStatus status,
                               RedirectAttributes redirectAttributes) {
        boolean updated = orderService.updateStatus(orderId, status);
        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderId + " status updated.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order #" + orderId + " was not found.");
        }
        return "redirect:/";
    }

    @ModelAttribute("orderTypes")
    public OrderType[] orderTypes() {
        return OrderType.values();
    }

    @ModelAttribute("orderStatuses")
    public OrderStatus[] orderStatuses() {
        return OrderStatus.values();
    }

    private void populatePage(Model model) {
        List<MenuItem> menuItems = menuService.getAllItems();
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("menuByCategory", groupByCategory(menuItems));
        model.addAttribute("orders", orderService.getAllOrders());
    }

    private Map<MenuCategory, List<MenuItem>> groupByCategory(List<MenuItem> menuItems) {
        Map<MenuCategory, List<MenuItem>> groupedItems = new EnumMap<>(MenuCategory.class);
        for (MenuItem item : menuItems) {
            groupedItems.computeIfAbsent(item.category(), ignored -> new ArrayList<>()).add(item);
        }
        return groupedItems;
    }

    private OrderForm buildOrderForm() {
        OrderForm form = new OrderForm();
        List<OrderItemForm> itemForms = new ArrayList<>();
        for (MenuItem menuItem : menuService.getAllItems()) {
            OrderItemForm itemForm = new OrderItemForm();
            itemForm.setCode(menuItem.code());
            itemForms.add(itemForm);
        }
        form.setItems(itemForms);
        return form;
    }

    private List<OrderItem> mapSelectedItems(OrderForm orderForm) {
        List<OrderItem> selectedItems = new ArrayList<>();
        for (OrderItemForm itemForm : orderForm.getItems()) {
            if (itemForm.getQuantity() <= 0) {
                continue;
            }

            menuService.findByCode(itemForm.getCode())
                    .ifPresent(menuItem -> selectedItems.add(new OrderItem(menuItem, itemForm.getQuantity())));
        }
        return selectedItems;
    }

    private void restoreMissingCodes(OrderForm orderForm) {
        List<MenuItem> menuItems = menuService.getAllItems();
        for (int index = 0; index < orderForm.getItems().size() && index < menuItems.size(); index++) {
            if (orderForm.getItems().get(index).getCode() == null || orderForm.getItems().get(index).getCode().isBlank()) {
                orderForm.getItems().get(index).setCode(menuItems.get(index).code());
            }
        }
    }
}