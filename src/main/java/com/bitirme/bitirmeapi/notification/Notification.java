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

    @Column(name = "recipient_id", insertable = false, updatable = false)
    private int recipientId;

    public Notification(String message, Member recipient) {
        this.message = message;
        this.recipient = recipient;
    }

}
