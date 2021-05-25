package com.bitirme.bitirmeapi.member.preferences;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public interface MusicPrefRepository extends JpaRepository<MusicPreference, Integer> {

    @Query("SELECT m FROM MusicPreference m WHERE m.description = ?1 ")
    Optional<MusicPreference> findByDescription(String description);

}
