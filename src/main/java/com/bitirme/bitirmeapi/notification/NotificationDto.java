package com.bitirme.bitirmeapi.notification;

import com.bitirme.bitirmeapi.member.MemberDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NotificationDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    private boolean isRead;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yy hh:mm:ss")
    private LocalDateTime createdAt;

    private String message;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String detailsLink;

    @JsonIgnoreProperties(value = {"email", "contactNo", "createdAt", "preferences", "vehicle"})
    private MemberDto sender;

    public NotificationDto(int id, boolean isRead, LocalDateTime createdAt, String message) {
        this.id = id;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.message = message;
    }
}
