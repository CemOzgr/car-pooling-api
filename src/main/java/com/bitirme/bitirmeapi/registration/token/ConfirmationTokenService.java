package com.bitirme.bitirmeapi.registration.token;

import com.bitirme.bitirmeapi.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository tokenRepository;

    @Autowired
    public ConfirmationTokenService(ConfirmationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }


    public ConfirmationToken generateRandomToken(Member member) {
        String token = UUID.randomUUID().toString();
        return new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                member
        );
    }

    @Transactional
    public void saveConfirmationToken(ConfirmationToken token) {
        tokenRepository.save(token);
    }

    public ConfirmationToken loadConfirmationToken(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));
    }

    public void confirmToken(String token) {
        tokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }

}
