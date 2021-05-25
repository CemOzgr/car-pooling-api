package com.bitirme.bitirmeapi.trip.waypoint;

import com.vividsolutions.jts.geom.Geometry;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "trip_waypoints",
        schema = "v1",
        indexes = {
            @Index(name = "trip_waypoints_un", columnList = "address", unique = true)
        }
)
public class Waypoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    private Geometry coordinates;

    private String city;


    private String address;

    public Waypoint(Geometry coordinates, String city, String address) {
        this.coordinates = coordinates;
        this.city = city;
        this.address = address;
    }
}
