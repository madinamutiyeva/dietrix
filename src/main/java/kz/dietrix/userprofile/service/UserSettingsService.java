package kz.dietrix.userprofile.service;

import kz.dietrix.auth.entity.User;
import kz.dietrix.common.exception.BadRequestException;
import kz.dietrix.userprofile.dto.UserSettingsDto;
import kz.dietrix.userprofile.entity.UserSettings;
import kz.dietrix.userprofile.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository repository;
    private final UserProfileService userProfileService;

    @Transactional
    public UserSettings getOrCreate(User user) {
        return repository.findByUserId(user.getId())
                .orElseGet(() -> repository.save(UserSettings.builder().user(user).build()));
    }

    @Transactional
    public UserSettingsDto getMine() {
        return toDto(getOrCreate(userProfileService.getCurrentUser()));
    }

    @Transactional
    public UserSettingsDto updateMine(UserSettingsDto patch) {
        User user = userProfileService.getCurrentUser();
        UserSettings s = getOrCreate(user);

        if (patch.getTheme() != null && !patch.getTheme().isBlank()) {
            s.setTheme(parseTheme(patch.getTheme()));
        }
        if (patch.getLocale() != null && !patch.getLocale().isBlank()) {
            s.setLocale(patch.getLocale().trim());
        }
        if (patch.getUnits() != null && !patch.getUnits().isBlank()) {
            s.setUnits(parseUnits(patch.getUnits()));
        }
        return toDto(repository.save(s));
    }

    private UserSettings.Theme parseTheme(String raw) {
        try {
            return UserSettings.Theme.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid theme: " + raw + " (expected LIGHT | DARK | SYSTEM)");
        }
    }

    private UserSettings.Units parseUnits(String raw) {
        try {
            return UserSettings.Units.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid units: " + raw + " (expected METRIC | IMPERIAL)");
        }
    }

    private UserSettingsDto toDto(UserSettings s) {
        return UserSettingsDto.builder()
                .theme(s.getTheme() != null ? s.getTheme().name() : null)
                .locale(s.getLocale())
                .units(s.getUnits() != null ? s.getUnits().name() : null)
                .build();
    }
}

