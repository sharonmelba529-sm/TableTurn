package com.example.tableturn.controller;

import com.example.tableturn.model.Order;
import com.example.tableturn.model.OrderItem;
import com.example.tableturn.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody Map<String, Object> request) {

        Long tableId = Long.valueOf(request.get("tableId").toString());

        List<Map<String, Object>> itemData =
                (List<Map<String, Object>>) request.get("items");

        List<OrderItem> items = itemData.stream().map(data -> {
            OrderItem item = new OrderItem();

            item.setItemName(data.get("itemName").toString());
            item.setQuantity(
                    Integer.parseInt(data.get("quantity").toString()));
            item.setUnitPrice(
                    Double.parseDouble(data.get("unitPrice").toString()));

            return item;
        }).toList();

        return orderService.createOrder(tableId, items);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(@PathVariable Long orderId) {
        return orderService.getOrderItems(orderId);
    }
}
