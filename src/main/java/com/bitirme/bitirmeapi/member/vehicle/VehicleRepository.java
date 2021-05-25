package com.bitirme.bitirmeapi.member.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    @Query("SELECT v FROM Vehicle v JOIN FETCH v.member WHERE v.memberId = ?1")
    Optional<Vehicle> loadWithMemberByMemberId(int memberId);

    @Transactional
    @Modifying
    @Query("UPDATE Vehicle v SET v.pictureFileName=?2 WHERE v.memberId=?1")
    void setVehiclePictureName(int memberId, String pictureFileName);

    boolean existsByMemberId(int memberId);

}
