package com.bitirme.bitirmeapi.trip;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.trip.request.TripRequest;
import com.bitirme.bitirmeapi.trip.waypoint.Waypoint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="trips",schema = "v1")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"tripRequests", "passengers"})
@NamedEntityGraphs({
                @NamedEntityGraph(
                        name = "trip-waypoint-graph",
                        attributeNodes = {
                                @NamedAttributeNode("startWaypoint"),
                                @NamedAttributeNode("destinationWaypoint"),
                                @NamedAttributeNode("driver")
                        }
                ) ,
                @NamedEntityGraph(
                        name = "trip-passengers-graph",
                        attributeNodes = {
                                @NamedAttributeNode("passengers"),
                                @NamedAttributeNode("startWaypoint"),
                                @NamedAttributeNode("destinationWaypoint")
                        }
                )
        }
)
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_waypoint_id", referencedColumnName = "id")
    private Waypoint startWaypoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_waypoint_id", referencedColumnName = "id")
    private Waypoint destinationWaypoint;

    @Column(name = "start_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate;

    @Column(name = "end_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Member driver;

    @Column(name = "driver_id", insertable = false, updatable = false)
    private int driverId;

    @Column(name = "initial_number_of_seats")
    private Short totalNumberOfSeats;

    @Column(name = "number_of_seats_available")
    private Short numberOfAvailableSeats;

    @Column(name = "price_of_one_seat")
    private Short priceOfOneSeat;

    @OneToMany(mappedBy = "trip")
    private Set<TripRequest> tripRequests;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            schema = "v1", name = "trip_passengers",
            joinColumns = @JoinColumn(name = "trip_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id")
    )
    private final Set<Member> passengers = new HashSet<>();

    public void addPassenger(Member passenger) {
        this.passengers.add(passenger);
    }

    @Transient
    public TripStatus getTripStatus() {
        if(LocalDateTime.now().isAfter(startDate)) return TripStatus.INACTIVE;
        if(numberOfAvailableSeats == 0) return TripStatus.FULL;
        return TripStatus.ACTIVE;
    }

}
