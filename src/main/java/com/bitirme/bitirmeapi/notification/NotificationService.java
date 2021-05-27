package com.bitirme.bitirmeapi.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationDto> loadNotifications(int recipientId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(recipientId);
        List<NotificationDto> dtos = new ArrayList<>();

        notifications.forEach(n -> {
            NotificationDto dto = new NotificationDto(n.getId(), n.isRead(), n.getCreatedAt(), n.getMessage());
            if(n instanceof TripRequestNotification) {
                dto.setDetailsLink("/api/trip-requests/"+((TripRequestNotification) n).getTripRequest_id());
            }
            dtos.add(dto);
        });
        return dtos;
    }

    public int loadNumberOfNotificationsByRead(int recipientId, boolean read) {
        return notificationRepository.countByRecipientIdAndRead(recipientId, read);
    }

    @Transactional
    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    @Transactional
    @PreAuthorize("@notificationRepository.existsByRecipientIdAndId(#principalId, #id)")
    public void setNotificationToRead(int id, int principalId) {
        notificationRepository.setRead(id);
    }
}
