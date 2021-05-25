package com.bitirme.bitirmeapi.member;

import com.bitirme.bitirmeapi.member.rating.Rating;
import com.bitirme.bitirmeapi.trip.Trip;
import com.bitirme.bitirmeapi.trip.request.TripRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="members", schema = "v1")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"ratings", "tripRequests", "passengerTrips", "trips"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "contact_no")
    private String contactNo;

    private String password;

    @Column(name = "profile_picture_name")
    private String profilePictureName;

    @Column(name = "is_admin", updatable = false)
    private boolean admin = false;

    private Boolean enabled = false;

    private Boolean locked = false;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "member")
    private final Set<Rating> ratings = new HashSet<>();

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "driver")
    private final Set<Trip> trips = new HashSet<>();

    @OneToMany(mappedBy = "submitter")
    private final Set<TripRequest> tripRequests = new HashSet<>();

    @ManyToMany(mappedBy = "passengers")
    private final Set<Trip> passengerTrips = new HashSet<>();

    public Member(String email, String firstName, String lastName, String contactNo, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNo = contactNo;
        this.password = password;
    }

}
