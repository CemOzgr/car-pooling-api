package com.bitirme.bitirmeapi.notification;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @EntityGraph("sender-notification-graph")
    @Query("SELECT n FROM Notification n WHERE n.recipientId=?1 ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientId(int recipientId);

    int countByRecipientIdAndRead(int recipientId, boolean read);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read=true WHERE n.id=?1")
    void setRead(int id);

    @Transactional
    boolean existsByRecipientIdAndId(int recipientId, int id);

    @Modifying
    @Transactional
    @Query(value ="DELETE FROM v1.notifications n " +
            "WHERE n.id IN (SELECT tn.id FROM v1.trip_request_notifications tn WHERE tn.trip_request_id = ?1)",
    nativeQuery = true)
    void deleteNotificationsByTripRequestId(int tripRequestId);

}
