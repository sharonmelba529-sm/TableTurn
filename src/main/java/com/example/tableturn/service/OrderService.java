
package com.example.tableturn.service;
import com.example.tableturn.exception.ReservationConflictException;
import com.example.tableturn.model.RestaurantTable;
import com.example.tableturn.model.Order;
import com.example.tableturn.model.OrderItem;
import com.example.tableturn.repository.OrderItemRepository;
import com.example.tableturn.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantTableService tableService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            RestaurantTableService tableService) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.tableService = tableService;
    }

    @Transactional
    public Order createOrder(Long tableId, List<OrderItem> items) {

        if (tableId == null || items == null || items.isEmpty()) {
            throw new RuntimeException(
                    "Table ID and at least one food item are required");
        }
        RestaurantTable table = tableService.getTableById(tableId)
                .orElseThrow(() ->
                        new RuntimeException("Table not found"));

        if ("OCCUPIED".equals(table.getStatus())) {
            throw new ReservationConflictException(
                    "This table is already occupied");
        }

        Order order = new Order();
        order.setTableId(tableId);
        order.setOrderTime(LocalDateTime.now());
        order.setStatus("PLACED");

        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : items) {

            if (item.getItemName() == null
                    || item.getItemName().isBlank()
                    || item.getQuantity() <= 0
                    || item.getUnitPrice() < 0) {
                throw new RuntimeException(
                        "Invalid food item details");
            }

            item.setOrderId(savedOrder.getOrderId());
            item.setSubtotal(item.getQuantity() * item.getUnitPrice());

            orderItemRepository.save(item);
        }

        // Mark the table as occupied after placing the order
        tableService.markTableOccupied(tableId);

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}
