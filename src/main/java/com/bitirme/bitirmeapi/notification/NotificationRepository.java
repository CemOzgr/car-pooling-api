package com.bitirme.bitirmeapi.notification;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query("SELECT n FROM Notification n WHERE n.recipientId=?1 ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientId(int recipientId);

    int countByRecipientIdAndRead(int recipientId, boolean read);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read=true WHERE n.id=?1")
    void setRead(int id);

    @Modifying
    @Transactional
    boolean existsByRecipientIdAndId(int recipientId, int id);

}
