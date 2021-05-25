package com.bitirme.bitirmeapi.trip.request;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.member.MemberDto;
import com.bitirme.bitirmeapi.member.View;
import com.bitirme.bitirmeapi.security.MemberDetails;
import com.bitirme.bitirmeapi.trip.Trip;
import com.bitirme.bitirmeapi.trip.TripDto;
import com.bitirme.bitirmeapi.trip.TripService;
import com.bitirme.bitirmeapi.trip.waypoint.WaypointDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/trip-requests")
public class TripRequestResource {

    private final TripService tripService;
    private final TripRequestService requestService;

    @Autowired
    public TripRequestResource(TripService tripService, TripRequestService requestService) {
        this.tripService = tripService;
        this.requestService = requestService;
    }

    @PostMapping("")
    public HttpStatus insertTripRequest(@AuthenticationPrincipal MemberDetails memberDetails,
                                        @RequestBody TripRequestDto requestDto) {
        requestDto.setSubmitterId(memberDetails.getId());
        tripService.insertRequestToTrip(requestDto);
        return HttpStatus.CREATED;
    }

    @PatchMapping("/{requestId}/{requestAction}")
    public HttpStatus updateRequestStatus(@AuthenticationPrincipal MemberDetails memberDetails,
                                          @PathVariable int requestId,
                                          @PathVariable Status requestAction) {
        if(requestAction.equals(Status.SUBMITTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request action type");
        }
        tripService.updateRequestStatus(memberDetails.getId(), requestId, requestAction);
        return HttpStatus.OK;
    }

    @GetMapping("/{requestId}")
    public MappingJacksonValue loadRequest(@PathVariable int requestId) {
        TripRequest request = requestService.loadTripRequest(requestId);
        Trip trip = request.getTrip();
        Member submitter = request.getSubmitter();
        TripRequestDto requestDto = new TripRequestDto(request, new TripDto(trip), new MemberDto(submitter));
        MappingJacksonValue value = new MappingJacksonValue(requestDto);
        value.setSerializationView(View.External.class);

        return value;
    }

    @GetMapping("")
    public List<TripRequestDto> loadRequests(@RequestParam int memberId) {
        List<TripRequestDto> requestDtos = new ArrayList<>();
        List<TripRequest> requests = tripService.loadRequestsOfMember(memberId);

        requests.forEach(request -> {
            TripRequestDto requestDto = new TripRequestDto(request);
            TripDto tripDto = new TripDto();
            WaypointDto start = new WaypointDto(request.getTrip().getStartWaypoint());
            WaypointDto destination = new WaypointDto(request.getTrip().getDestinationWaypoint());
            tripDto.setStartLocation(start);
            tripDto.getStartLocation().setCoordinates(null);
            tripDto.setDestinationLocation(destination);
            tripDto.getDestinationLocation().setCoordinates(null);
            requestDto.setTrip(tripDto);
            requestDto.getTrip().setStartDate(request.getTrip().getStartDate());
            requestDto.getTrip().setEndDate(request.getTrip().getEndDate());
            requestDto.getTrip().setId(request.getTrip().getId());
            requestDtos.add(requestDto);
        });
        return requestDtos;
    }

    @DeleteMapping("/{requestId}")
    public void deleteRequest(@PathVariable int requestId,
                              @AuthenticationPrincipal MemberDetails principal) {
        tripService.deleteRequest(requestId, principal.getId());
    }

}
