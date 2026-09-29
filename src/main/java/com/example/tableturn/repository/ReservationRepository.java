package com.example.tableturn.repository;

import com.example.tableturn.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    boolean existsByTableIdAndReservationDateAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Long tableId,
            LocalDate reservationDate,
            String status,
            java.time.LocalTime requestedEndTime,
            java.time.LocalTime requestedStartTime
    );
}