package com.bitirme.bitirmeapi.member.preferences;

import com.bitirme.bitirmeapi.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final MusicPrefRepository musicRepository;
    private final ConversationRepository conversationRepository;

    @Autowired
    public PreferenceService(PreferenceRepository preferenceRepository, MusicPrefRepository musicRepository,
                             ConversationRepository conversationRepository) {
        this.preferenceRepository = preferenceRepository;
        this.musicRepository = musicRepository;
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public void savePreference(Member member, PreferencesDto preferencesDto) {

        ConversationPreference conversationPreference = conversationRepository
                .findByDescription(preferencesDto.getConversationPreference())
                    .orElseThrow(() -> new IllegalStateException("Conversation preference should be valid"));
        MusicPreference musicPreference = musicRepository.findByDescription(preferencesDto.getMusicPreference())
                    .orElseThrow(() -> new IllegalStateException("Music preference should be valid"));

        Preference preference = new Preference();
        preference.setMember(member);
        preference.setSmokingAllowed(preference.isSmokingAllowed());
        preference.setPetsAllowed(preferencesDto.isPetsAllowed());
        preference.setConversation(conversationPreference);
        preference.setMusic(musicPreference);

        preferenceRepository.save(preference);
    }

    @Transactional
    public void updatePreference(int memberId, PreferencesDto preferencesDto) {
        Preference preference = loadPreferencesOfMember(memberId);
        if(preference == null) {
            throw new IllegalStateException("Preference does not exists");
        }

        preference.setSmokingAllowed(preferencesDto.isSmokingAllowed());
        preference.setPetsAllowed(preferencesDto.isPetsAllowed());

        MusicPreference musicPreference = musicRepository.findByDescription(preferencesDto.getMusicPreference())
                .orElseThrow(() -> new IllegalStateException("Music preference should be valid"));
        preference.setMusic(musicPreference);

        ConversationPreference conversationPreference = conversationRepository
                .findByDescription(preferencesDto.getConversationPreference())
                .orElseThrow(() -> new IllegalStateException("Conversation preference should be valid"));
        preference.setConversation(conversationPreference);

        preferenceRepository.save(preference);
    }

    public Preference loadPreferencesOfMember(int memberId) {
        return preferenceRepository.findById(memberId)
                .orElse(null);
    }

}
