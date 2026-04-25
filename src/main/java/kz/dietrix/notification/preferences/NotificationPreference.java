package kz.dietrix.notification.preferences;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "push_enabled", nullable = false)
    @Builder.Default
    private boolean pushEnabled = true;

    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private boolean emailEnabled = true;

    @Column(name = "meal_reminders", nullable = false)
    @Builder.Default
    private boolean mealReminders = true;

    @Column(name = "pantry_expiry", nullable = false)
    @Builder.Default
    private boolean pantryExpiry = true;

    @Column(name = "weekly_report", nullable = false)
    @Builder.Default
    private boolean weeklyReport = true;

    @Column(name = "water_reminders", nullable = false)
    @Builder.Default
    private boolean waterReminders = false;

    @Column(name = "breakfast_time", nullable = false)
    @Builder.Default
    private LocalTime breakfastTime = LocalTime.of(8, 0);

    @Column(name = "lunch_time", nullable = false)
    @Builder.Default
    private LocalTime lunchTime = LocalTime.of(13, 0);

    @Column(name = "dinner_time", nullable = false)
    @Builder.Default
    private LocalTime dinnerTime = LocalTime.of(19, 0);

    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;

    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;
}

