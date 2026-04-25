package kz.dietrix.notification.push;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.notification.NotificationType;
import kz.dietrix.notification.preferences.NotificationPreference;
import kz.dietrix.notification.preferences.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sends push notifications via FCM. Gracefully degrades to no-op if Firebase is not configured
 * or user disabled push.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FcmPushService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationPreferenceService preferenceService;

    @Async
    public void sendToUser(User user, NotificationType type, String title, String body) {
        sendToUser(user, type, title, body, Map.of());
    }

    @Async
    public void sendToUser(User user, NotificationType type, String title, String body, Map<String, String> data) {
        if (!isFirebaseReady()) return;

        // Check user preferences
        NotificationPreference prefs = preferenceService.getOrCreate(user);
        if (!prefs.isPushEnabled() || !isTypeEnabled(prefs, type)) {
            log.debug("Push to {} skipped — type {} disabled or push off", user.getEmail(), type);
            return;
        }
        if (isInQuietHours(prefs)) {
            log.debug("Push to {} skipped — within quiet hours", user.getEmail());
            return;
        }

        List<DeviceToken> tokens = deviceTokenRepository.findByUserId(user.getId());
        if (tokens.isEmpty()) {
            log.debug("No FCM tokens for user {}", user.getEmail());
            return;
        }

        Map<String, String> payload = new HashMap<>(data == null ? Map.of() : data);
        payload.put("type", type.name());

        for (DeviceToken dt : tokens) {
            sendOne(dt, title, body, payload);
        }
    }

    private void sendOne(DeviceToken dt, String title, String body, Map<String, String> data) {
        try {
            Message msg = Message.builder()
                    .setToken(dt.getToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)
                    .build();
            String id = FirebaseMessaging.getInstance().send(msg);
            log.info("FCM message sent: {} → {}", id, dt.getToken().substring(0, Math.min(12, dt.getToken().length())));
        } catch (FirebaseMessagingException e) {
            MessagingErrorCode code = e.getMessagingErrorCode();
            if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT) {
                log.warn("Removing dead FCM token (code={}): {}", code, e.getMessage());
                deviceTokenRepository.delete(dt);
            } else {
                log.error("FCM send failed: {} — {}", code, e.getMessage());
            }
        } catch (Exception e) {
            log.error("Unexpected push error: {}", e.getMessage());
        }
    }

    private boolean isFirebaseReady() {
        try {
            return !FirebaseApp.getApps().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTypeEnabled(NotificationPreference p, NotificationType type) {
        return switch (type) {
            case MEAL_REMINDER  -> p.isMealReminders();
            case PANTRY_EXPIRY  -> p.isPantryExpiry();
            case WEEKLY_REPORT  -> p.isWeeklyReport();
            case RECIPE_READY, SYSTEM -> true;
        };
    }

    private boolean isInQuietHours(NotificationPreference p) {
        if (p.getQuietHoursStart() == null || p.getQuietHoursEnd() == null) return false;
        LocalTime now = LocalTime.now();
        LocalTime start = p.getQuietHoursStart();
        LocalTime end = p.getQuietHoursEnd();
        if (start.isBefore(end)) {
            return !now.isBefore(start) && now.isBefore(end);
        }
        // overnight window (e.g. 22:00–07:00)
        return !now.isBefore(start) || now.isBefore(end);
    }
}

