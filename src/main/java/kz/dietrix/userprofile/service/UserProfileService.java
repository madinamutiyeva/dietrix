package kz.dietrix.userprofile.service;

import kz.dietrix.auth.entity.User;
import kz.dietrix.auth.repository.UserRepository;
import kz.dietrix.common.exception.ResourceNotFoundException;
import kz.dietrix.common.reference.ActivityLevel;
import kz.dietrix.common.reference.Gender;
import kz.dietrix.common.reference.Goal;
import kz.dietrix.common.util.NutritionCalculator;
import kz.dietrix.userprofile.dto.*;
import kz.dietrix.userprofile.entity.UserPreference;
import kz.dietrix.userprofile.entity.UserProfile;
import kz.dietrix.userprofile.repository.UserPreferenceRepository;
import kz.dietrix.userprofile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository profileRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Transactional
    public UserProfileDto getProfile() {
        User user = getCurrentUser();
        UserProfile profile = getOrCreateProfile(user);

        return UserProfileDto.builder()
                .id(profile.getId())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .gender(profile.getGender())
                .age(profile.getAge())
                .heightCm(profile.getHeightCm())
                .weightKg(profile.getWeightKg())
                .goal(profile.getGoal())
                .activityLevel(profile.getActivityLevel())
                .avatarUrl(profile.getAvatarUrl())
                .onboardingCompleted(profile.isOnboardingCompleted())
                .build();
    }

    @Transactional
    public UserProfileDto updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        UserProfile profile = getOrCreateProfile(user);

        if (request.getName() != null) {
            user.setName(request.getName());
            userRepository.save(user);
        }
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getAge() != null) profile.setAge(request.getAge());
        if (request.getHeightCm() != null) profile.setHeightCm(request.getHeightCm());
        if (request.getWeightKg() != null) profile.setWeightKg(request.getWeightKg());
        if (request.getGoal() != null) profile.setGoal(request.getGoal());
        if (request.getActivityLevel() != null) profile.setActivityLevel(request.getActivityLevel());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());

        profileRepository.save(profile);
        log.info("Profile updated for user: {}", user.getEmail());

        return getProfile();
    }

    @Transactional
    public UserPreferenceDto getPreferences() {
        User user = getCurrentUser();
        UserPreference pref = getOrCreatePreference(user);

        return UserPreferenceDto.builder()
                .dietType(pref.getDietType())
                .allergies(pref.getAllergies())
                .likedFoods(pref.getLikedFoods())
                .dislikedFoods(pref.getDislikedFoods())
                .cuisinePreferences(pref.getCuisinePreferences())
                .build();
    }

    @Transactional
    public UserPreferenceDto updatePreferences(UserPreferenceDto request) {
        User user = getCurrentUser();
        UserPreference pref = getOrCreatePreference(user);

        if (request.getDietType() != null) pref.setDietType(request.getDietType());
        if (request.getAllergies() != null) pref.setAllergies(request.getAllergies());
        if (request.getLikedFoods() != null) pref.setLikedFoods(request.getLikedFoods());
        if (request.getDislikedFoods() != null) pref.setDislikedFoods(request.getDislikedFoods());
        if (request.getCuisinePreferences() != null) pref.setCuisinePreferences(request.getCuisinePreferences());

        preferenceRepository.save(pref);
        log.info("Preferences updated for user: {}", user.getEmail());

        return getPreferences();
    }

    @Transactional
    public UserTargetsDto getTargets() {
        User user = getCurrentUser();
        UserProfile profile = getOrCreateProfile(user);

        log.info("getTargets() — profile id={}, goal={}, activityLevel={}, gender={}, weight={}, height={}, age={}",
                profile.getId(), profile.getGoal(), profile.getActivityLevel(),
                profile.getGender(), profile.getWeightKg(), profile.getHeightCm(), profile.getAge());

        Gender gender = profile.getGender() != null ? profile.getGender() : Gender.MALE;
        double weight = profile.getWeightKg() != null ? profile.getWeightKg() : 70;
        double height = profile.getHeightCm() != null ? profile.getHeightCm() : 170;
        int age = profile.getAge() != null ? profile.getAge() : 25;
        ActivityLevel activity = profile.getActivityLevel() != null ? profile.getActivityLevel() : ActivityLevel.MODERATELY_ACTIVE;
        Goal goal = profile.getGoal() != null ? profile.getGoal() : Goal.MAINTAIN;

        if (profile.getGoal() == null) {
            log.warn("User {} has no goal set — defaulting to MAINTAIN. Calories will be TDEE without surplus/deficit.",
                    user.getEmail());
        }

        // Расчёты
        double bmr = NutritionCalculator.calculateBMR(gender, weight, height, age);
        double tdee = NutritionCalculator.calculateTDEE(gender, weight, height, age, activity);
        int calories = NutritionCalculator.calculateDailyCalories(gender, weight, height, age, activity, goal);
        int protein = NutritionCalculator.calculateProtein(weight, goal);
        int fat = NutritionCalculator.calculateFat(calories);
        int carbs = NutritionCalculator.calculateCarbs(calories, protein, fat);
        int waterMl = NutritionCalculator.calculateWaterIntake(weight, activity);

        double bmi = NutritionCalculator.calculateBMI(weight, height);
        String bmiCategory = NutritionCalculator.getBmiCategory(bmi);

        return UserTargetsDto.builder()
                .dailyCalories(calories)
                .proteinGrams(protein)
                .carbsGrams(carbs)
                .fatGrams(fat)
                .bmr((int) Math.round(bmr))
                .tdee((int) Math.round(tdee))
                .formula("Mifflin-St Jeor")
                .bmi(bmi)
                .bmiCategory(bmiCategory)
                .waterMl(waterMl)
                .proteinPercent(NutritionCalculator.calculateProteinPercent(protein, calories))
                .carbsPercent(NutritionCalculator.calculateCarbsPercent(carbs, calories))
                .fatPercent(NutritionCalculator.calculateFatPercent(fat, calories))
                .gender(gender.name())
                .age(age)
                .weightKg(weight)
                .heightCm(height)
                .activityLevel(activity.getDescription())
                .goal(goal.getDisplayName())
                .build();
    }

    public UserProfile getOrCreateProfile(User user) {
        return profileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile profile = UserProfile.builder().user(user).build();
                    return profileRepository.save(profile);
                });
    }

    public UserPreference getOrCreatePreference(User user) {
        return preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserPreference pref = UserPreference.builder().user(user).build();
                    return preferenceRepository.save(pref);
                });
    }

}

