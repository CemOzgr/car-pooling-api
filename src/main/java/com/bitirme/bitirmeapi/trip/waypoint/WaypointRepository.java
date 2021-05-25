package com.bitirme.bitirmeapi.trip.waypoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface WaypointRepository extends JpaRepository<Waypoint, Integer> {
    @Query(value = "SELECT EXISTS(" +
            "   SELECT 1 FROM v1.trip_waypoints w WHERE w.address = ?1 " +
            ")", nativeQuery = true)
    boolean doesAddressExists(String address);


    @Query("SELECT w FROM Waypoint w WHERE w.address = ?1")
    Optional<Waypoint> findByAddress(String address);

}
