package com.bitirme.bitirmeapi.registration.token;

import com.bitirme.bitirmeapi.member.Member;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "confirmation_tokens",schema = "v1")
@EqualsAndHashCode
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "member_id"
    )
    private Member member;

    public ConfirmationToken(String token, LocalDateTime createdAt,
                             LocalDateTime expiresAt, Member member) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.member = member;
    }
}
