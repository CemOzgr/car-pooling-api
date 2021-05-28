package com.bitirme.bitirmeapi.notification;

import com.bitirme.bitirmeapi.notification.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, SseService sseService) {
        this.notificationRepository = notificationRepository;
        this.sseService = sseService;
    }

    public List<NotificationDto> loadNotifications(int recipientId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(recipientId);
        return NotificationDtoConverter.convertToDtoFromList(notifications);
    }

    public int loadNumberOfNotificationsByRead(int recipientId, boolean read) {
        return notificationRepository.countByRecipientIdAndRead(recipientId, read);
    }

    @Transactional
    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public void sendNotification(int recipientId, NotificationDto notification) {
        sseService.sendNamedEventToMember(recipientId, notification, "notification");
    }

    @Transactional
    @PreAuthorize("@notificationRepository.existsByRecipientIdAndId(#principalId, #id)")
    public void setNotificationToRead(int id, int principalId) {
        notificationRepository.setRead(id);
    }
}
