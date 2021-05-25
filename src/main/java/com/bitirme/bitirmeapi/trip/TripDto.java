package com.bitirme.bitirmeapi.trip;

import com.bitirme.bitirmeapi.member.MemberDto;
import com.bitirme.bitirmeapi.member.View;
import com.bitirme.bitirmeapi.trip.waypoint.WaypointDto;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TripDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private Integer id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private Integer driverId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonView(View.External.class)
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonView(View.External.class)
    private LocalDateTime endDate;

    @JsonView(View.External.class)
    private Short price;

    @JsonView(View.External.class)
    private Short totalNumberOfSeats;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private Short seatsAvailable;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private String status;

    @JsonView(View.External.class)
    private WaypointDto startLocation;

    @JsonView(View.External.class)
    private WaypointDto destinationLocation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties(value = {"email", "contactNo", "createdAt"})
    @JsonView(View.External.class)
    private MemberDto driver;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties(value = {"email", "createdAt", "vehicle", "preferences", "contactNo"})
    private final List<MemberDto> passengers = new ArrayList<>();

    public TripDto(Trip trip) {
        this.id = trip.getId();
        this.startDate = trip.getStartDate();
        this.endDate = trip.getEndDate();
        this.startLocation = new WaypointDto(trip.getStartWaypoint());
        this.destinationLocation = new WaypointDto(trip.getDestinationWaypoint());
        this.price = trip.getPriceOfOneSeat();
        this.totalNumberOfSeats = trip.getTotalNumberOfSeats();
        this.seatsAvailable = trip.getNumberOfAvailableSeats();
        this.status = trip.getTripStatus().name();
    }

    public TripDto(Trip trip, Double driverRating) {
        this(trip);
        this.driver = new MemberDto(trip.getDriver());
        this.driver.setAverageRating(driverRating);
    }

}
