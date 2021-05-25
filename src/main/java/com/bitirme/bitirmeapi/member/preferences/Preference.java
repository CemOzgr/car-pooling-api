package com.bitirme.bitirmeapi.member.preferences;

import com.bitirme.bitirmeapi.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="member_preferences",schema = "v1")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Preference {

    @Id
    @Column(name = "member_id")
    @JsonIgnore
    private int memberId;

    @Column(name = "is_smoking_allowed")
    private boolean smokingAllowed;

    @Column(name = "is_pets_allowed")
    private boolean petsAllowed;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conversation_pref_id")
    private ConversationPreference conversation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "music_pref_id")
    private MusicPreference music;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name="member_id")
    private Member member;

}
