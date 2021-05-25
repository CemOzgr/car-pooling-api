package com.bitirme.bitirmeapi.trip.waypoint;

import com.bitirme.bitirmeapi.member.View;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WaypointDto {

    @JsonView(View.Internal.class)
    private PointDto coordinates;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private String city;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private String address;

    public WaypointDto(Waypoint waypoint) {
        if(waypoint.getCoordinates() != null) {
            this.coordinates = new PointDto(
                    waypoint.getCoordinates().getCoordinate().y,
                    waypoint.getCoordinates().getCoordinate().x
            );
        }
        this.city = waypoint.getCity();
        this.address = waypoint.getAddress();
    }

}
