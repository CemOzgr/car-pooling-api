package com.bitirme.bitirmeapi.trip;

import com.bitirme.bitirmeapi.member.MemberDto;
import com.bitirme.bitirmeapi.security.MemberDetails;
import com.bitirme.bitirmeapi.trip.request.TripRequestDto;
import com.bitirme.bitirmeapi.util.jackson.View;
import com.bitirme.bitirmeapi.util.pagination.PageDto;
import com.bitirme.bitirmeapi.util.pagination.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/trips")
public class TripResource {

    private final TripService tripService;

    @Autowired
    public TripResource(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/{tripId}")
    public TripDto loadTrip(@PathVariable int tripId) {
        return tripService.loadTripDetailed(tripId);
    }

    @GetMapping("")
    public MappingJacksonValue loadTrips(@RequestParam String search,
                                         @RequestParam(name = "page", defaultValue = "0") Integer pageNumber,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<TripDto> page = tripService.loadTrips(search, pageable);

        PaginationDto paginationDto = new PaginationDto(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
        MappingJacksonValue value = new MappingJacksonValue(new PageDto<>(page.getContent(), paginationDto));
        value.setSerializationView(View.External.class);
        return value;
    }
    @PostMapping("")
    public HttpStatus createTrip(@AuthenticationPrincipal MemberDetails memberDetails,
                                 @RequestBody TripDto tripDto) {
        tripDto.setDriverId(memberDetails.getId());
        tripService.createTrip(tripDto);
        return HttpStatus.CREATED;
    }

    @DeleteMapping("/{tripId}")
    public HttpStatus deleteTrip(@PathVariable int tripId) {
        tripService.deleteTrip(tripId);
        return HttpStatus.OK;
    }

    @GetMapping("/{tripId}/requests")
    public List<TripRequestDto> loadRequestsForTrip(@PathVariable int tripId) {

        return tripService.loadSubmittedRequestsForTrip(tripId)
                .stream()
                .map(request -> {
                    MemberDto submitter = new MemberDto();
                    submitter.setId(request.getSubmitter().getId());
                    submitter.setFullName(String.format("%s %s",
                            request.getSubmitter().getFirstName(),
                            request.getSubmitter().getLastName())
                    );
                    TripRequestDto requestDto = new TripRequestDto(request);
                    requestDto.setSubmitter(submitter);
                    return requestDto;
                })
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{tripId}/passengers/{passengerId}")
    public void removePassengerFromTrip(@PathVariable int tripId, @PathVariable int passengerId) {
        tripService.removePassengerFromTrip(tripId, passengerId);
    }

}
