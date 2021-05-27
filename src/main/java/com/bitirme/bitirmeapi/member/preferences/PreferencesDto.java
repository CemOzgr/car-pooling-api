package com.bitirme.bitirmeapi.member.preferences;

import com.bitirme.bitirmeapi.util.jackson.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonView(View.External.class)
public class PreferencesDto {

    @NotNull
    private boolean smokingAllowed;

    @NotNull
    private boolean petsAllowed;

    @NotNull
    private String musicPreference;

    @NotNull
    private String conversationPreference;

    public PreferencesDto(Preference preference) {
        this.smokingAllowed = preference.isSmokingAllowed();
        this.petsAllowed = preference.isPetsAllowed();
        this.musicPreference = preference.getMusic().getDescription();
        this.conversationPreference = preference.getConversation().getDescription();
    }

}
