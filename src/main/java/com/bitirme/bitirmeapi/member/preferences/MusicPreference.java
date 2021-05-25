package com.bitirme.bitirmeapi.member.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="music_preferences",schema = "v1")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MusicPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @OneToMany(mappedBy = "music")
    private Set<Preference> preferences;

}
