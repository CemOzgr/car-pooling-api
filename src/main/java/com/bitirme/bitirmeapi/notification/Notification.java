package com.bitirme.bitirmeapi.notification;

import com.bitirme.bitirmeapi.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", schema = "v1")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
@NamedEntityGraph(
        name = "sender-notification-graph",
        attributeNodes = {
                @NamedAttributeNode("sender")
        }
)
public abstract class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String message;

    @Column(name = "is_read")
    private boolean read = false;

    @Column(name = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    private Member recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Member sender;

    @Column(name = "recipient_id", insertable = false, updatable = false)
    private int recipientId;

    @Column(name = "sender_id", insertable = false, updatable = false)
    private int senderId;

    public Notification(String message, Member recipient, Member sender) {
        this.message = message;
        this.recipient = recipient;
        this.recipientId = recipient.getId();
        this.sender = sender;
        this.senderId = sender.getId();
    }

}
