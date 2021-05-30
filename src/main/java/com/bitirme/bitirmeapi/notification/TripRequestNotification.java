package com.bitirme.bitirmeapi.notification;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.trip.request.TripRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "trip_request_notifications", schema = "v1")
@Getter
@Setter
@NoArgsConstructor
public class TripRequestNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_request_id", referencedColumnName = "id")
    private TripRequest tripRequest;

    @Column(name = "trip_request_id", insertable = false, updatable = false)
    private int tripRequestId;

    public TripRequestNotification(String message, Member recipient, Member sender, TripRequest tripRequest) {
        super(message, recipient, sender);
        this.tripRequest = tripRequest;
        this.tripRequestId = tripRequest.getId();
    }

}
