package kz.dietrix.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.dto.ApiResponse;
import kz.dietrix.notification.dto.NotificationsResponse;
import kz.dietrix.notification.preferences.NotificationPreferenceDto;
import kz.dietrix.notification.preferences.NotificationPreferenceService;
import kz.dietrix.notification.push.DeviceTokenService;
import kz.dietrix.notification.push.RegisterDeviceRequest;
import kz.dietrix.notification.sse.SseEmitterRegistry;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "User notifications, push devices and preferences")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationPreferenceService preferenceService;
    private final DeviceTokenService deviceTokenService;
    private final UserProfileService userProfileService;
    private final SseEmitterRegistry sseRegistry;

    // ─── In-app notifications ────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get notifications for current user")
    public ApiResponse<NotificationsResponse> getNotifications(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(defaultValue = "0")     int page,
            @RequestParam(defaultValue = "20")    int size) {
        return ApiResponse.success(notificationService.getNotifications(unreadOnly, page, size));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.success("Notification marked as read");
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.success("All notifications marked as read");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ApiResponse<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ApiResponse.success("Notification deleted");
    }

    // ─── Push devices (FCM) ──────────────────────────────────────────────

    @PostMapping("/devices")
    @Operation(summary = "Register an FCM device token for push notifications")
    public ApiResponse<Void> registerDevice(@Valid @RequestBody RegisterDeviceRequest request) {
        deviceTokenService.register(request);
        return ApiResponse.success("Device registered");
    }

    @DeleteMapping("/devices/{token}")
    @Operation(summary = "Unregister an FCM device token")
    public ApiResponse<Void> unregisterDevice(@PathVariable String token) {
        deviceTokenService.unregister(token);
        return ApiResponse.success("Device unregistered");
    }

    // ─── Preferences ─────────────────────────────────────────────────────

    @GetMapping("/preferences")
    @Operation(summary = "Get notification preferences")
    public ApiResponse<NotificationPreferenceDto> getPreferences() {
        return ApiResponse.success(preferenceService.getMine());
    }

    @PatchMapping("/preferences")
    @Operation(summary = "Update notification preferences (partial)")
    public ApiResponse<NotificationPreferenceDto> updatePreferences(@RequestBody NotificationPreferenceDto request) {
        return ApiResponse.success("Preferences updated", preferenceService.updateMine(request));
    }

    // ─── Test push ───────────────────────────────────────────────────────

    @PostMapping("/test")
    @Operation(summary = "Send a test push notification to the current user's devices")
    public ApiResponse<Void> testPush() {
        User user = userProfileService.getCurrentUser();
        notificationService.createNotification(
                user,
                NotificationType.SYSTEM,
                "Тестовое уведомление",
                "Если вы это видите — push работает! 🎉"
        );
        return ApiResponse.success("Test notification sent");
    }

    // ─── Real-time stream (SSE) — lightweight push, no Firebase needed ───

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Open an SSE stream — receive notifications in real time while the tab is open")
    public SseEmitter stream() {
        User user = userProfileService.getCurrentUser();
        return sseRegistry.register(user.getId());
    }
}

