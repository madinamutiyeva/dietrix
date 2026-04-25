package kz.dietrix.notification;

import kz.dietrix.auth.entity.User;
import kz.dietrix.auth.repository.UserRepository;
import kz.dietrix.notification.preferences.NotificationPreference;
import kz.dietrix.notification.preferences.NotificationPreferenceRepository;
import kz.dietrix.pantry.entity.PantryItem;
import kz.dietrix.pantry.repository.PantryItemRepository;
import kz.dietrix.recipes.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final NotificationService    notificationService;
    private final PantryItemRepository   pantryItemRepository;
    private final RecipeRepository       recipeRepository;
    private final UserRepository         userRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    // ─── Meal reminders: every minute, matched against user-configured times ──

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void sendMealReminders() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        List<NotificationPreference> all = preferenceRepository.findAll();
        for (NotificationPreference p : all) {
            if (!p.isMealReminders()) continue;
            User user = p.getUser();
            if (matches(now, p.getBreakfastTime())) {
                notificationService.createNotification(user, NotificationType.MEAL_REMINDER,
                        "Время завтрака", "Не забудьте позавтракать 🍳");
            } else if (matches(now, p.getLunchTime())) {
                notificationService.createNotification(user, NotificationType.MEAL_REMINDER,
                        "Время обеда", "Пора пообедать 🍲");
            } else if (matches(now, p.getDinnerTime())) {
                notificationService.createNotification(user, NotificationType.MEAL_REMINDER,
                        "Время ужина", "Не пропустите ужин 🍽");
            }
        }
    }

    private boolean matches(LocalTime now, LocalTime target) {
        if (target == null) return false;
        return now.getHour() == target.getHour() && now.getMinute() == target.getMinute();
    }

    // ─── Pantry Expiry Check: every day at 09:00 ──────────────────────────────

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkPantryExpiry() {
        log.info("Running pantry expiry check...");
        LocalDate today  = LocalDate.now();
        LocalDate cutoff = today.plusDays(2);

        List<PantryItem> expiringItems = pantryItemRepository.findExpiringItems(today, cutoff);
        if (expiringItems.isEmpty()) {
            log.info("No expiring pantry items found.");
            return;
        }

        // Group by user
        Map<Long, List<PantryItem>> byUser = expiringItems.stream()
                .collect(Collectors.groupingBy(item -> item.getUser().getId()));

        byUser.forEach((userId, items) -> {
            User user = items.get(0).getUser();
            items.forEach(item -> {
                String dateStr = item.getExpirationDate() != null
                        ? item.getExpirationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        : "скоро";
                notificationService.createNotification(
                        user,
                        NotificationType.PANTRY_EXPIRY,
                        "Срок годности истекает",
                        item.getName() + " истекает " + dateStr
                );
            });
        });

        log.info("Pantry expiry notifications created for {} users", byUser.size());
    }

    // ─── Weekly Report: every Monday at 10:00 ─────────────────────────────────

    @Scheduled(cron = "0 0 10 * * MON")
    @Transactional
    public void sendWeeklyReport() {
        log.info("Running weekly report notifications...");
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        LocalDateTime weekEnd   = LocalDateTime.now();

        List<Object[]> counts = recipeRepository.countGeneratedPerUserBetween(weekStart, weekEnd);

        counts.forEach(row -> {
            Long userId = (Long) row[0];
            long count  = (long)  row[1];

            userRepository.findById(userId).ifPresent(user ->
                    notificationService.createNotification(
                            user,
                            NotificationType.WEEKLY_REPORT,
                            "Еженедельный отчёт",
                            "На этой неделе вы сгенерировали " + count + " рецепт(а/ов) с помощью AI!"
                    )
            );
        });

        log.info("Weekly report notifications sent to {} users", counts.size());
    }

    // ─── Cleanup old notifications: every day at 02:00 ───────────────────────

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupOldNotifications() {
        log.info("Running cleanup of old notifications...");
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        int deleted = notificationRepository.deleteOlderThan(cutoff);
        log.info("Deleted {} notifications older than 30 days", deleted);
    }
}

