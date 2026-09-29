package com.example.tableturn.service;

import com.example.tableturn.model.RestaurantTable;
import com.example.tableturn.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository repository;

    public RestaurantTableService(RestaurantTableRepository repository) {
        this.repository = repository;
    }

    public RestaurantTable addTable(RestaurantTable table) {
        table.setStatus("AVAILABLE");
        return repository.save(table);
    }

    public List<RestaurantTable> getAllTables() {
        return repository.findAll();
    }

    public Optional<RestaurantTable> getTableById(Long id) {
        return repository.findById(id);
    }

    public RestaurantTable updateTable(Long id, RestaurantTable updatedTable) {
        RestaurantTable existingTable = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Table not found with ID: " + id));

        existingTable.setTableNumber(updatedTable.getTableNumber());
        existingTable.setCapacity(updatedTable.getCapacity());
        existingTable.setStatus(updatedTable.getStatus());

        return repository.save(existingTable);
    }

    public void deleteTable(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Table not found with ID: " + id);
        }

        repository.deleteById(id);
    }
    public RestaurantTable markTableAvailable(Long tableId) {
        RestaurantTable table = repository.findById(tableId)
                .orElseThrow(() ->
                        new RuntimeException("Table not found with ID: " + tableId));

        table.setStatus("AVAILABLE");
        return repository.save(table);
    }
    public RestaurantTable markTableOccupied(Long tableId) {
        RestaurantTable table = repository.findById(tableId)
                .orElseThrow(() ->
                        new RuntimeException("Table not found with ID: " + tableId));

        table.setStatus("OCCUPIED");
        return repository.save(table);
    }
}