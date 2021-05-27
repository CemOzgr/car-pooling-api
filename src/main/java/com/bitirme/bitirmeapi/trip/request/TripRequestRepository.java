package com.bitirme.bitirmeapi.trip.request;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TripRequestRepository extends JpaRepository<TripRequest, Integer> {

    @EntityGraph("trip-request-graph")
    @Query("SELECT r FROM TripRequest r WHERE r.submitter.id = ?1 AND r.expiredAt > current_timestamp")
    List<TripRequest> findBySubmitter(int submitterId);

    @Query(value = "SELECT EXISTS(" +
            "SELECT 1 FROM v1.trip_requests r " +
            "WHERE r.trip_id=?1 AND r.submitter_id=?2 " +
            "AND r.request_status=0 AND r.expired_at > current_timestamp)"
    ,nativeQuery = true)
    boolean hasSubmitterGotActiveRequestForTrip(int tripId, int submitterId);

    @EntityGraph("trip-submitter-request-graph")
    @Query("SELECT r FROM TripRequest r WHERE r.id = ?1")
    Optional<TripRequest> findDetailedById(int requestId);

    @Transactional
    @Modifying
    @Query("UPDATE TripRequest r SET r.status=?2, r.updatedAt=current_timestamp WHERE r.id=?1 ")
    void updateStatus(int requestId, Status Status);

    @EntityGraph("submitter-request-graph")
    @Query("SELECT r FROM TripRequest r " +
            "WHERE r.trip.id=?1 AND r.status=?2 AND r.expiredAt > current_timestamp " +
            "ORDER BY r.submittedAt DESC")
    List<TripRequest> findNotExpiredByTripAndStatus(int tripId, Status status);

    @Query(value="SELECT EXISTS(" +
            "SELECT FROM v1.trip_requests tr JOIN v1.trips t ON tr.trip_id=t.id " +
            "WHERE tr.id=?1 AND (tr.submitter_id=?2 OR t.driver_id=?2))",
            nativeQuery = true)
    boolean isMemberOwnerOfRequestOrDriver(int requestId, int memberId);

}
