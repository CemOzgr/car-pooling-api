package com.bitirme.bitirmeapi.trip;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Integer>, TripQueryDslRepository{

    @EntityGraph("trip-passengers-graph")
    @Query("SELECT t FROM Trip t WHERE t.id = ?1")
    Optional<Trip> findWithPassengersById(int id);

    boolean existsByIdAndDriverId(int id, int driverId);

    @Query(value = "SELECT EXISTS(" +
            "SELECT 1 FROM v1.trip_passengers tp WHERE tp.trip_id=?1 AND tp.passenger_id = ?2 " +
            ")", nativeQuery = true)
    boolean existsByIdAndPassengerId(int tripId, int passengerId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM v1.trip_passengers WHERE trip_id=?1 AND passenger_id=?2"
    , nativeQuery = true)
    void deletePassengerByTripIdAndMemberId(int tripId, int passengerId);

}
