package com.bitirme.bitirmeapi.member.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="conversation_preferences",schema = "v1")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ConversationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    private String description;

    @OneToMany(mappedBy = "conversation")
    private Set<Preference> preferences;
}
