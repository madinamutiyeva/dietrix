package kz.dietrix.notification.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsResponse {

    private List<NotificationDto> items;
    private long unreadCount;
}

