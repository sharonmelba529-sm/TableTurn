package com.example.tableturn.service;

import com.example.tableturn.exception.ReservationConflictException;
import com.example.tableturn.model.Reservation;
import com.example.tableturn.model.RestaurantTable;
import com.example.tableturn.repository.ReservationRepository;
import com.example.tableturn.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            RestaurantTableRepository tableRepository) {

        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
    }

    public Reservation createReservation(Reservation reservation) {

        // Check reservation time
        if (reservation.getStartTime() == null
                || reservation.getEndTime() == null
                || !reservation.getEndTime().isAfter(reservation.getStartTime())) {
            throw new RuntimeException(
                    "End time must be later than start time");
        }

        // Check that the table exists
        RestaurantTable table = tableRepository
                .findById(reservation.getTableId())
                .orElseThrow(() ->
                        new RuntimeException("Table not found"));

        // Check party size
        if (reservation.getPartySize() <= 0) {
            throw new RuntimeException(
                    "Party size must be greater than zero");
        }

        // Check whether the table can seat the group
        if (reservation.getPartySize() > table.getCapacity()) {
            throw new RuntimeException(
                    "The selected table cannot accommodate this many people");
        }

        // Check for overlapping reservations
        boolean overlap =
                reservationRepository
                        .existsByTableIdAndReservationDateAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                                reservation.getTableId(),
                                reservation.getReservationDate(),
                                "BOOKED",
                                reservation.getEndTime(),
                                reservation.getStartTime()
                        );

        if (overlap) {
            throw new ReservationConflictException(
                    "This table is already reserved during that time");
        }

        reservation.setStatus("BOOKED");
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Reservation not found"));
    }
    public Reservation cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Reservation not found"));

        reservation.setStatus("CANCELLED");

        return reservationRepository.save(reservation);
    }
}