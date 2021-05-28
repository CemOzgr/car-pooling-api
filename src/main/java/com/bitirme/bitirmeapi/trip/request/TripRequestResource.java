package com.bitirme.bitirmeapi.trip.request;

import com.bitirme.bitirmeapi.security.MemberDetails;
import com.bitirme.bitirmeapi.trip.TripDto;
import com.bitirme.bitirmeapi.trip.TripService;
import com.bitirme.bitirmeapi.trip.waypoint.WaypointDto;
import com.bitirme.bitirmeapi.util.jackson.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
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
    public void insertTripRequest(@AuthenticationPrincipal MemberDetails memberDetails,
                                  @RequestBody TripRequestDto requestDto,
                                  HttpServletResponse response) {
        requestDto.setSubmitterId(memberDetails.getId());
        tripService.insertRequestToTrip(requestDto);
        response.addHeader("X-Accel-Buffering", "no");
    }

    @PatchMapping("/{requestId}/{requestAction}")
    public HttpStatus updateRequestStatus(@AuthenticationPrincipal MemberDetails principal,
                                          @PathVariable int requestId,
                                          @PathVariable Status requestAction) {
        if(requestAction.equals(Status.SUBMITTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request action type");
        }
        tripService.updateRequestStatus(principal.getId(), requestId, requestAction);
        return HttpStatus.OK;
    }

    @GetMapping("/{requestId}")
    public MappingJacksonValue loadRequest(@AuthenticationPrincipal MemberDetails principal,
                                           @PathVariable int requestId) {
        TripRequestDto requestDto = requestService.loadTripRequestDto(requestId, principal.getId());
        MappingJacksonValue value = new MappingJacksonValue(requestDto);

        if(principal.getId() == requestDto.getSubmitter().getId()) {
            value.setSerializationView(View.External.class);
        } else {
            requestDto.getTrip().getStartLocation().setCoordinates(null);
            requestDto.getTrip().getDestinationLocation().setCoordinates(null);
            value.setSerializationView(View.Internal.class);
        }

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
