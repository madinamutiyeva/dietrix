package kz.dietrix.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kz.dietrix.notification.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private boolean read;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
}

