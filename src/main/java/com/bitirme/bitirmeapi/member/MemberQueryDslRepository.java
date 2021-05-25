package com.bitirme.bitirmeapi.member;

import com.bitirme.bitirmeapi.trip.TripDto;
import java.util.List;

public interface MemberQueryDslRepository {

    MemberDto findMemberWithPrefAndVehicle(int id);

    List<TripDto> findMemberPassengerTrips(int id);

    List<TripDto> findMemberDriverTrips(int id);
}
