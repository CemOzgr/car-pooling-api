package com.bitirme.bitirmeapi.member.preferences;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public interface ConversationRepository extends JpaRepository<ConversationPreference, Integer> {

    @Query("SELECT c FROM ConversationPreference c WHERE c.description = ?1 ")
    Optional<ConversationPreference> findByDescription(String description);
}
