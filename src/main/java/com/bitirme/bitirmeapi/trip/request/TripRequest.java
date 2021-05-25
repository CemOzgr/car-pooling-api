package com.bitirme.bitirmeapi.trip.request;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.trip.Trip;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="trip_requests",schema = "v1")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "submitter-request-graph",
                attributeNodes = {
                        @NamedAttributeNode("submitter")
                }
        ),
        @NamedEntityGraph(
                name = "trip-request-graph",
                attributeNodes = {
                        @NamedAttributeNode(value = "trip", subgraph = "trips-subgraph")
                },
                subgraphs = {
                        @NamedSubgraph(
                                name = "trips-subgraph",
                                attributeNodes = {
                                        @NamedAttributeNode("startWaypoint"),
                                        @NamedAttributeNode("destinationWaypoint")
                                }
                        )
                }
        ),
        @NamedEntityGraph(
                name = "trip-submitter-request-graph",
                attributeNodes = {
                        @NamedAttributeNode(value = "trip", subgraph = "trips-subgraph"),
                        @NamedAttributeNode("submitter")
                },
                subgraphs = {
                        @NamedSubgraph(
                                name = "trips-subgraph",
                                attributeNodes = {
                                        @NamedAttributeNode("startWaypoint"),
                                        @NamedAttributeNode("destinationWaypoint")
                                }
                        )
                }
        )
})
public class TripRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="trip_id", referencedColumnName = "id")
    @NonNull
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="submitter_id", referencedColumnName = "id")
    @NonNull
    private Member submitter;

    @Column(name = "request_status")
    @Enumerated
    private Status status;

    @Column(name = "submitted_at", insertable = false, updatable = false)
    private Date submittedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expired_at", insertable = false, updatable = false)
    private LocalDateTime expiredAt;

}
