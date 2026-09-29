package com.example.tableturn.controller;

import com.example.tableturn.model.Bill;
import com.example.tableturn.service.BillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping("/{orderId}")
    public Bill generateBill(@PathVariable Long orderId) {
        return billService.generateBill(orderId);
    }

    @GetMapping
    public List<Bill> getAllBills() {
        return billService.getAllBills();
    }
}