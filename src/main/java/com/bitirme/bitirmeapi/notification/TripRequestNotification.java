package com.bitirme.bitirmeapi.notification;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.trip.request.TripRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_request_notifications", schema = "v1")
@Getter
@Setter
@NoArgsConstructor
public class TripRequestNotification extends Notification {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_request_id", referencedColumnName = "id")
    private TripRequest tripRequest;

    @Column(name = "trip_request_id", insertable = false, updatable = false)
    private int tripRequest_id;

    public TripRequestNotification(String message, Member recipient, TripRequest tripRequest) {
        super(message, recipient);
        this.tripRequest = tripRequest;
    }

}
