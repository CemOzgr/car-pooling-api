package com.bitirme.bitirmeapi.member.rating;

import com.bitirme.bitirmeapi.member.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="member_ratings",schema = "v1")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Column(name = "submitter_id")
    private int submitterId;

    private double rating;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Rating(int submitterId, double rating) {
        this.submitterId = submitterId;
        this.rating = rating;
    }
}
