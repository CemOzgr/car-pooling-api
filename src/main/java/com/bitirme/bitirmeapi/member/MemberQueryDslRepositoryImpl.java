package com.bitirme.bitirmeapi.member;

import com.bitirme.bitirmeapi.member.preferences.*;
import com.bitirme.bitirmeapi.member.vehicle.QVehicle;
import com.bitirme.bitirmeapi.member.vehicle.VehicleDto;
import com.bitirme.bitirmeapi.trip.QTrip;
import com.bitirme.bitirmeapi.trip.Trip;
import com.bitirme.bitirmeapi.trip.TripDto;
import com.bitirme.bitirmeapi.trip.waypoint.QWaypoint;
import com.bitirme.bitirmeapi.trip.waypoint.WaypointDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class MemberQueryDslRepositoryImpl implements MemberQueryDslRepository{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public MemberQueryDslRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public MemberDto findMemberWithPrefAndVehicle(int id) {
        QMember member = QMember.member;
        QVehicle vehicle = QVehicle.vehicle;
        QPreference preference = QPreference.preference;
        QMusicPreference musicPreference = QMusicPreference.musicPreference;
        QConversationPreference conversationPreference = QConversationPreference.conversationPreference;

        return queryFactory
                .selectFrom(member)
                    .leftJoin(preference).on(member.id.eq(preference.memberId))
                    .leftJoin(preference.music, musicPreference).on(preference.music.id.eq(musicPreference.id))
                    .leftJoin(preference.conversation, conversationPreference).on(preference.conversation.id.eq(conversationPreference.id))
                    .leftJoin(vehicle).on(member.id.eq(vehicle.memberId))
                .where(member.id.eq(id))
                .select(
                        Projections.constructor(
                            MemberDto.class,
                            member,
                            Projections.constructor(
                                PreferencesDto.class,
                                preference.smokingAllowed,
                                preference.petsAllowed,
                                musicPreference.description,
                                conversationPreference.description
                            ).skipNulls(),
                            Projections.constructor(
                                VehicleDto.class,
                                vehicle
                            ).skipNulls()
                        )
                )
                .fetchOne();
    }

    @Override
    public List<TripDto> findMemberPassengerTrips(int id) {
        QMember member = QMember.member;
        QTrip trip = QTrip.trip;
        QWaypoint start = new QWaypoint("startWaypoint");
        QWaypoint destination = new QWaypoint("destinationWaypoint");

        List<Trip> trips =  queryFactory
                .selectFrom(member)
                .leftJoin(member.passengerTrips, trip)
                .leftJoin(trip.startWaypoint, start).fetchJoin()
                .leftJoin(trip.destinationWaypoint, destination).fetchJoin()
                .where(member.id.eq(id))
                .orderBy(trip.startDate.desc())
                .select(trip)
                .fetch();

        if(!trips.stream().allMatch(Objects::isNull)) {
            return trips
                    .stream()
                    .map(t-> {
                        TripDto dto = new TripDto();
                        WaypointDto startDto = new WaypointDto(t.getStartWaypoint());
                        WaypointDto endDto = new WaypointDto(t.getDestinationWaypoint());
                        dto.setStartLocation(startDto);
                        dto.setDestinationLocation(endDto);
                        dto.setId(t.getId());
                        dto.setStartDate(t.getStartDate());
                        dto.setEndDate(t.getEndDate());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<TripDto> findMemberDriverTrips(int id) {
        QMember member = QMember.member;
        QTrip trip = QTrip.trip;
        QWaypoint start = new QWaypoint("startWaypoint");
        QWaypoint destination = new QWaypoint("destinationWaypoint");

        List<Trip> trips =  queryFactory
                .selectFrom(member)
                .leftJoin(member.trips, trip)
                .leftJoin(trip.startWaypoint, start).fetchJoin()
                .leftJoin(trip.destinationWaypoint, destination).fetchJoin()
                .where(member.id.eq(id))
                .orderBy(trip.startDate.desc())
                .select(trip)
                .fetch();

        if(!trips.stream().allMatch(Objects::isNull)) {
            return trips
                    .stream()
                    .map(t-> {
                        TripDto dto = new TripDto();
                        WaypointDto startDto = new WaypointDto(t.getStartWaypoint());
                        WaypointDto endDto = new WaypointDto(t.getDestinationWaypoint());
                        dto.setStartLocation(startDto);
                        dto.setDestinationLocation(endDto);
                        dto.setId(t.getId());
                        dto.setStartDate(t.getStartDate());
                        dto.setEndDate(t.getEndDate());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();

    }
}
