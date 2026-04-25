package kz.dietrix.notification.preferences;

import kz.dietrix.auth.entity.User;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository repository;
    private final UserProfileService userProfileService;

    @Transactional
    public NotificationPreference getOrCreate(User user) {
        return repository.findByUserId(user.getId())
                .orElseGet(() -> repository.save(NotificationPreference.builder().user(user).build()));
    }

    @Transactional
    public NotificationPreferenceDto getMine() {
        User user = userProfileService.getCurrentUser();
        return toDto(getOrCreate(user));
    }

    @Transactional
    public NotificationPreferenceDto updateMine(NotificationPreferenceDto patch) {
        User user = userProfileService.getCurrentUser();
        NotificationPreference p = getOrCreate(user);

        if (patch.getPushEnabled()    != null) p.setPushEnabled(patch.getPushEnabled());
        if (patch.getEmailEnabled()   != null) p.setEmailEnabled(patch.getEmailEnabled());
        if (patch.getMealReminders()  != null) p.setMealReminders(patch.getMealReminders());
        if (patch.getPantryExpiry()   != null) p.setPantryExpiry(patch.getPantryExpiry());
        if (patch.getWeeklyReport()   != null) p.setWeeklyReport(patch.getWeeklyReport());
        if (patch.getWaterReminders() != null) p.setWaterReminders(patch.getWaterReminders());
        if (patch.getBreakfastTime()  != null) p.setBreakfastTime(patch.getBreakfastTime());
        if (patch.getLunchTime()      != null) p.setLunchTime(patch.getLunchTime());
        if (patch.getDinnerTime()     != null) p.setDinnerTime(patch.getDinnerTime());
        if (patch.getQuietHoursStart()!= null) p.setQuietHoursStart(patch.getQuietHoursStart());
        if (patch.getQuietHoursEnd()  != null) p.setQuietHoursEnd(patch.getQuietHoursEnd());

        return toDto(repository.save(p));
    }

    private NotificationPreferenceDto toDto(NotificationPreference p) {
        return NotificationPreferenceDto.builder()
                .pushEnabled(p.isPushEnabled())
                .emailEnabled(p.isEmailEnabled())
                .mealReminders(p.isMealReminders())
                .pantryExpiry(p.isPantryExpiry())
                .weeklyReport(p.isWeeklyReport())
                .waterReminders(p.isWaterReminders())
                .breakfastTime(p.getBreakfastTime())
                .lunchTime(p.getLunchTime())
                .dinnerTime(p.getDinnerTime())
                .quietHoursStart(p.getQuietHoursStart())
                .quietHoursEnd(p.getQuietHoursEnd())
                .build();
    }
}

