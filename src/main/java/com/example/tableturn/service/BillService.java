package com.example.tableturn.service;

import com.example.tableturn.model.Bill;
import com.example.tableturn.model.Order;
import com.example.tableturn.model.OrderItem;
import com.example.tableturn.repository.BillRepository;
import com.example.tableturn.repository.OrderItemRepository;
import com.example.tableturn.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final RestaurantTableService tableService;

    public BillService(
            BillRepository billRepository,
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            RestaurantTableService tableService) {

        this.billRepository = billRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.tableService = tableService;
    }

    public Bill generateBill(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        List<OrderItem> items =
                orderItemRepository.findByOrderId(orderId);

        if (items.isEmpty()) {
            throw new RuntimeException(
                    "No items found for this order");
        }

        double total = 0;

        for (OrderItem item : items) {
            total += item.getSubtotal();
        }

        Bill bill = new Bill();
        bill.setOrderId(orderId);
        bill.setTotalAmount(total);
        bill.setBillTime(LocalDateTime.now());
        bill.setStatus("GENERATED");

        Bill savedBill = billRepository.save(bill);

// Update order status after generating the bill
        order.setStatus("BILLED");
        orderRepository.save(order);

// Make the order's table available
        tableService.markTableAvailable(order.getTableId());

        return savedBill;
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }
}