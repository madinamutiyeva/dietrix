package kz.dietrix.onboarding.service;

import kz.dietrix.auth.entity.User;
import kz.dietrix.onboarding.dto.*;
import kz.dietrix.userprofile.entity.UserProfile;
import kz.dietrix.userprofile.repository.UserProfileRepository;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final UserProfileService profileService;
    private final UserProfileRepository profileRepository;

    @Transactional
    public OnboardingStatusDto getStatus() {
        User user = profileService.getCurrentUser();
        UserProfile profile = profileService.getOrCreateProfile(user);
        return OnboardingStatusDto.of(profile.getOnboardingStep(), profile.isOnboardingCompleted());
    }

    @Transactional
    public OnboardingStatusDto saveBasicInfo(BasicInfoRequest request) {
        User user = profileService.getCurrentUser();
        UserProfile profile = profileService.getOrCreateProfile(user);

        // Set directly on the managed entity
        profile.setGender(request.getGender());
        profile.setAge(request.getAge());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setOnboardingStep(Math.max(profile.getOnboardingStep(), 1));

        profileRepository.save(profile);

        log.info("Onboarding step 1 (basic-info) completed for user: {}", user.getEmail());
        return OnboardingStatusDto.of(profile.getOnboardingStep(), profile.isOnboardingCompleted());
    }

    @Transactional
    public OnboardingStatusDto saveActivityGoal(ActivityGoalRequest request) {
        User user = profileService.getCurrentUser();
        UserProfile profile = profileService.getOrCreateProfile(user);

        // Set directly on the managed entity to avoid detached-state issues
        profile.setActivityLevel(request.getActivityLevel());
        profile.setGoal(request.getGoal());
        profile.setOnboardingStep(Math.max(profile.getOnboardingStep(), 2));

        profileRepository.save(profile);

        log.info("Onboarding step 2 completed for user: {}, goal={}, activity={}",
                user.getEmail(), profile.getGoal(), profile.getActivityLevel());
        return OnboardingStatusDto.of(profile.getOnboardingStep(), profile.isOnboardingCompleted());
    }

    @Transactional
    public OnboardingStatusDto savePreferences(PreferencesRequest request) {
        User user = profileService.getCurrentUser();
        UserProfile profile = profileService.getOrCreateProfile(user);

        // Save preferences directly
        kz.dietrix.userprofile.entity.UserPreference pref = profileService.getOrCreatePreference(user);
        if (request.getDietType() != null) pref.setDietType(request.getDietType());
        if (request.getAllergies() != null) pref.setAllergies(request.getAllergies());
        if (request.getLikedFoods() != null) pref.setLikedFoods(request.getLikedFoods());
        if (request.getDislikedFoods() != null) pref.setDislikedFoods(request.getDislikedFoods());
        if (request.getCuisinePreferences() != null) pref.setCuisinePreferences(request.getCuisinePreferences());

        profile.setOnboardingStep(3);
        profile.setOnboardingCompleted(true);
        profileRepository.save(profile);

        log.info("Onboarding completed for user: {}", user.getEmail());
        return OnboardingStatusDto.of(profile.getOnboardingStep(), true);
    }
}

