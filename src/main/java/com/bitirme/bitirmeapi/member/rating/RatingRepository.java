package com.bitirme.bitirmeapi.member.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Integer>, RatingQueryDslRepository {

    @Query("SELECT r FROM Rating r JOIN FETCH r.member m WHERE m.id = ?1 AND r.submitterId = ?2 ")
    Optional<Rating> findByMemberIdAndSubmitterId(int memberId, int submitterId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.member.id = ?1 ")
    Double findAverageRatingByMember(int memberId);

    @Query(value = "SELECT EXISTS(" +
            "SELECT 1 " +
            "FROM v1.trips t JOIN v1.trip_passengers tp ON t.id = tp.trip_id " +
            "WHERE t.driver_id = ?1 AND tp.passenger_id = ?2 AND t.end_date < current_timestamp)"
    , nativeQuery = true)
    boolean isSubmitterPassenger(int memberId, int submitterId);

}
