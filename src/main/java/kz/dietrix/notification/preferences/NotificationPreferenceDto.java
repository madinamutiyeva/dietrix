package kz.dietrix.notification.preferences;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceDto {
    private Boolean pushEnabled;
    private Boolean emailEnabled;
    private Boolean mealReminders;
    private Boolean pantryExpiry;
    private Boolean weeklyReport;
    private Boolean waterReminders;
    private LocalTime breakfastTime;
    private LocalTime lunchTime;
    private LocalTime dinnerTime;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
}

