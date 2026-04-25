package kz.dietrix.notification;

import kz.dietrix.auth.entity.User;
import kz.dietrix.common.exception.ResourceNotFoundException;
import kz.dietrix.notification.dto.NotificationDto;
import kz.dietrix.notification.dto.NotificationsResponse;
import kz.dietrix.notification.push.FcmPushService;
import kz.dietrix.notification.sse.SseEmitterRegistry;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserProfileService userProfileService;
    private final FcmPushService fcmPushService;
    private final SseEmitterRegistry sseRegistry;

    // ─── Public API for other services ────────────────────────────────────────

    @Transactional
    public void createNotification(User user, NotificationType type, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .build();
        notification = notificationRepository.save(notification);
        log.info("Notification created [{}] for user: {}", type, user.getEmail());

        // 1. Real-time push to open browser tabs (lightweight, no external service)
        sseRegistry.broadcast(user.getId(), toDto(notification));

        // 2. Optional FCM push (works in background; no-op if Firebase not configured)
        fcmPushService.sendToUser(user, type, title, message);
    }

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public NotificationsResponse getNotifications(boolean unreadOnly, int page, int size) {
        User user = userProfileService.getCurrentUser();
        int clampedSize = Math.min(size, 50);
        Pageable pageable = PageRequest.of(page, clampedSize);

        Page<Notification> notificationPage = unreadOnly
                ? notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(user.getId(), pageable)
                : notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        List<NotificationDto> items = notificationPage.getContent()
                .stream()
                .map(this::toDto)
                .toList();

        long unreadCount = notificationRepository.countByUserIdAndReadFalse(user.getId());

        return NotificationsResponse.builder()
                .items(items)
                .unreadCount(unreadCount)
                .build();
    }

    @Transactional
    public void markAsRead(Long id) {
        User user = userProfileService.getCurrentUser();
        Notification notification = notificationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        User user = userProfileService.getCurrentUser();
        int updated = notificationRepository.markAllAsRead(user.getId());
        log.info("Marked {} notifications as read for user: {}", updated, user.getEmail());
    }

    @Transactional
    public void deleteNotification(Long id) {
        User user = userProfileService.getCurrentUser();
        Notification notification = notificationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notificationRepository.delete(notification);
        log.info("Notification {} deleted for user: {}", id, user.getEmail());
    }

    // ─── Mapper ───────────────────────────────────────────────────────────────

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}

