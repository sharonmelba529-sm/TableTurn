package com.example.tableturn.repository;

import com.example.tableturn.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository
        extends JpaRepository<RestaurantTable, Long> {
}