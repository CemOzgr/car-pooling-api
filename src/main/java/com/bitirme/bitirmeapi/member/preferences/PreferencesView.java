package com.bitirme.bitirmeapi.member.preferences;

public interface PreferencesView {
    boolean smokingAllowed();
    boolean petsAllowed();
    String getMusicPreference();
    String getConversationPreference();
}
