package com.bitirme.bitirmeapi.trip.request;

import com.bitirme.bitirmeapi.member.MemberDto;
import com.bitirme.bitirmeapi.util.jackson.View;
import com.bitirme.bitirmeapi.trip.TripDto;
import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TripRequestDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private int id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int tripId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int submitterId;

    @JsonView(View.External.class)
    private String status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yy hh:mm:ss")
    @JsonView(View.External.class)
    private Date submittedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties(value = {"email", "contactNo", "createdAt", "vehicle", "preferences"})
    @JsonView(View.Internal.class)
    private MemberDto submitter;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties(value = {"price", "totalNumberOfSeats", "seatsAvailable"})
    @JsonView(View.External.class)
    private TripDto trip;

    public TripRequestDto(TripRequest request) {
        this.id = request.getId();
        this.tripId = request.getTrip().getId();
        this.status = request.getStatus().name();
        this.submittedAt = request.getSubmittedAt();
    }

    public TripRequestDto(TripRequest request, TripDto tripDto, MemberDto memberDto) {
        this(request);
        this.submitter = memberDto;
        this.trip = tripDto;
    }


}
