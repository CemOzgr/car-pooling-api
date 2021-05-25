package com.bitirme.bitirmeapi.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer>, MemberQueryDslRepository {

    @Transactional
    @Query("SELECT m FROM Member m WHERE m.email = ?1")
    Optional<Member> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE Member m SET m.enabled=TRUE WHERE m.id = ?1")
    void enableMember(int id);

    @Transactional
    @Modifying
    @Query("UPDATE Member m SET m.profilePictureName=?2 WHERE m.id=?1")
    void setProfilePictureName(int id, String ProfilePictureName);

    @Query(value="SELECT EXISTS(" +
                "SELECT 1 " +
                    "FROM v1.trip_passengers tp JOIN v1.trips t ON tp.trip_id=t.id " +
                    "WHERE " +
                        "((t.driver_id=?1 AND tp.passenger_id=?2) OR (t.driver_id=?2 AND tp.passenger_id=?1)) " +
                        "AND t.end_date < CURRENT_TIMESTAMP" +
                ")", nativeQuery = true)
    boolean existsActiveByTripDriverIdAndTripPassengerId(int driverId, int passengerId);

}
