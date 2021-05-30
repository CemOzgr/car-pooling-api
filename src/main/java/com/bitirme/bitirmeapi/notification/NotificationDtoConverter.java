package com.bitirme.bitirmeapi.notification;

import com.bitirme.bitirmeapi.member.MemberDto;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationDtoConverter {

    public static NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto(
                notification.getId(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getMessage()
        );
        dto.setSender(new MemberDto(notification.getSender()));

        if(notification instanceof TripRequestNotification) {
            dto.setDetailsLink("/api/trip-requests/"+((TripRequestNotification) notification).getTripRequestId());
        }
        return dto;
    }

    public static List<NotificationDto> convertToDtoFromList(List<Notification> notifications) {
        return notifications
                .stream()
                .map(NotificationDtoConverter::convertToDto)
                .collect(Collectors.toList());
    }

}
