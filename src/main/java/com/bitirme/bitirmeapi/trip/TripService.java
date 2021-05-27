package com.bitirme.bitirmeapi.trip;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.member.MemberDto;
import com.bitirme.bitirmeapi.member.MemberService;
import com.bitirme.bitirmeapi.member.vehicle.Vehicle;
import com.bitirme.bitirmeapi.member.vehicle.VehicleService;
import com.bitirme.bitirmeapi.trip.request.Status;
import com.bitirme.bitirmeapi.trip.request.TripRequest;
import com.bitirme.bitirmeapi.trip.request.TripRequestDto;
import com.bitirme.bitirmeapi.trip.request.TripRequestService;
import com.bitirme.bitirmeapi.trip.waypoint.PointDto;
import com.bitirme.bitirmeapi.trip.waypoint.Waypoint;
import com.bitirme.bitirmeapi.trip.waypoint.WaypointService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final WaypointService waypointService;
    private final VehicleService vehicleService;
    private final MemberService memberService;
    private final TripRequestService requestService;

    @Autowired
    public TripService(TripRepository tripRepository, WaypointService waypointService,
                       VehicleService vehicleService, MemberService memberService,
                       TripRequestService requestService) {
        this.tripRepository = tripRepository;
        this.waypointService = waypointService;
        this.vehicleService = vehicleService;
        this.memberService = memberService;
        this.requestService = requestService;
    }

    @Transactional
    @PreAuthorize("#tripDto.driverId == authentication.principal.id")
    public void createTrip(TripDto tripDto) {

        if(tripDto.getStartDate().isBefore(LocalDateTime.now().plusHours(1)) ||
                tripDto.getStartDate().isAfter(tripDto.getEndDate())) {
            throw new IllegalStateException("Invalid dates");
        }

        PointDto startPoint = tripDto.getStartLocation().getCoordinates();
        PointDto destinationPoint = tripDto.getDestinationLocation().getCoordinates();

        Waypoint startWaypoint = waypointService
                .getWaypointFromCoordinates(startPoint.getLatitude(), startPoint.getLongitude());
        Waypoint destinationWaypoint = waypointService
                .getWaypointFromCoordinates(destinationPoint.getLatitude(), destinationPoint.getLongitude());

        Waypoint fetchedStartPoint = waypointService.loadByAddress(startWaypoint.getAddress());
        Waypoint fetchedDestinationPoint = waypointService.loadByAddress(destinationWaypoint.getAddress());

        Vehicle vehicle = vehicleService.loadWithMember(tripDto.getDriverId());

        Trip trip = new Trip();
        trip.setDriver(vehicle.getMember());
        trip.setStartDate(tripDto.getStartDate());
        trip.setEndDate(tripDto.getEndDate());
        trip.setPriceOfOneSeat(tripDto.getPrice());
        trip.setTotalNumberOfSeats(tripDto.getTotalNumberOfSeats());
        trip.setNumberOfAvailableSeats(tripDto.getTotalNumberOfSeats());

        if(fetchedStartPoint == null) {
            waypointService.saveWaypoint(startWaypoint);
            trip.setStartWaypoint(startWaypoint);
        } else {
            trip.setStartWaypoint(fetchedStartPoint);
        }

        if(fetchedDestinationPoint == null) {
            waypointService.saveWaypoint(destinationWaypoint);
            trip.setDestinationWaypoint(destinationWaypoint);
        } else {
            trip.setDestinationWaypoint(fetchedDestinationPoint);
        }

        tripRepository.save(trip);
    }

    @Transactional
    @PreAuthorize("@tripService.isMemberDriverOfTrip(#tripId, authentication.principal.id)")
    public void deleteTrip(int tripId) {
        tripRepository.deleteById(tripId);
    }

    public Page<TripDto> loadTrips(String search, Pageable pageRequest) {

        TripPredicateBuilder builder = new TripPredicateBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)" +
                "(:|<|>)" +
                "((\\w+?)|([-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?))|" +
                "(\\d{2}-\\d{2}-\\d{4}));", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(search + ";");

        while(matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        BooleanExpression predicate = builder.build();
        return tripRepository.findTripDtos(predicate, pageRequest);
    }

    @Transactional
    public TripDto loadTripDetailed(int id) {
        Trip trip =  tripRepository.findWithPassengersById(id)
                .orElseThrow(() -> new IllegalStateException("resource not found"));

        TripDto tripDto = new TripDto(trip);
        MemberDto driver = memberService.loadMemberDetailed(trip.getDriverId());

        tripDto.setDriver(driver);

        List<MemberDto> passengers = trip.getPassengers()
                .stream()
                .map(MemberDto::new)
                .collect(Collectors.toList());

        tripDto.getPassengers().addAll(passengers);
        return tripDto;
    }

    @Transactional
    public void insertRequestToTrip(TripRequestDto requestDto) {

        if(tripRepository.existsByIdAndPassengerId(requestDto.getTripId(), requestDto.getSubmitterId())) {
            throw new IllegalStateException("Already a passenger");
        }

        Trip trip = tripRepository.getOne(requestDto.getTripId());

        if(trip.getDriverId() == requestDto.getSubmitterId()) {
            throw new AccessDeniedException("You are the driver");
        }

        if(!trip.getTripStatus().equals(TripStatus.ACTIVE)) {
            throw new IllegalStateException(
                    "Could not create request. Trip is ".concat(trip.getTripStatus().name().toLowerCase(Locale.ROOT)));
        }

        Member submitter = memberService.loadMember(requestDto.getSubmitterId());

        requestService.createTripRequest(trip, submitter);
    }

    @Transactional
    public void updateRequestStatus(int principalId, int requestId, Status status) {
        TripRequest request = requestService.loadTripRequest(requestId);
        Trip trip = request.getTrip();

        if(principalId != trip.getDriverId()) {
            throw new AccessDeniedException("Forbidden");
        }

        if(request.getUpdatedAt() != null || LocalDateTime.now().isAfter(request.getExpiredAt())) {
            throw new IllegalStateException("Request can't be updated.(Expired or previously answered)");
        }

        if(status.equals(Status.ACCEPTED)) {
            addPassengerToTrip(trip, request.getSubmitter());
        }

        requestService.updateRequestStatus(request, status);
    }

    @Transactional
    public void addPassengerToTrip(Trip trip, Member passenger) {
        trip.setNumberOfAvailableSeats((short) (trip.getNumberOfAvailableSeats() - 1));
        trip.addPassenger(passenger);
        tripRepository.save(trip);
    }

    @PreAuthorize("@tripService.isMemberDriverOfTrip(#tripId, authentication.principal.id)")
    @Transactional
    public void removePassengerFromTrip(int tripId, int passengerId) {
        LocalDateTime startDate = tripRepository.getOne(tripId).getStartDate();

        if(LocalDateTime.now().isAfter(startDate.minusHours(1))) {
            throw new IllegalStateException("Can't remove passenger.");
        }

        tripRepository.deletePassengerByTripIdAndMemberId(tripId, passengerId);
    }

    @PreAuthorize("#memberId == authentication.principal.id")
    public List<TripRequest> loadRequestsOfMember(int memberId) {
        return requestService.loadRequestsOfMember(memberId);
    }

    @Transactional
    public void deleteRequest(int requestId, int principalId) {
        TripRequest request = requestService.loadTripRequest(requestId);
        if(request.getSubmitter().getId() != principalId) {
            throw new AccessDeniedException("Forbidden");
        }
        requestService.deleteTripRequest(request);
    }

    @PreAuthorize("@tripService.isMemberDriverOfTrip(#tripId, authentication.principal.id)")
    public List<TripRequest> loadSubmittedRequestsForTrip(int tripId) {
        return requestService.loadRequestsForTripByStatus(tripId, Status.SUBMITTED);
    }

    public boolean isMemberDriverOfTrip(int tripId, int memberId) {
        return tripRepository.existsByIdAndDriverId(tripId, memberId);
    }

}
