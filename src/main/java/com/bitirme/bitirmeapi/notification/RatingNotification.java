package com.bitirme.bitirmeapi.notification;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.member.rating.Rating;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "rating_notifications", schema = "v1")
@Getter
@Setter
@NoArgsConstructor
public class RatingNotification extends Notification{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_id", referencedColumnName = "id")
    private Rating rating;

    public RatingNotification(String message, Member recipient, Rating rating) {
        super(message, recipient);
        this.rating = rating;
    }

}
