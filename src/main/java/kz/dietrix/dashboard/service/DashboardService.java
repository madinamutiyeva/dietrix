package kz.dietrix.dashboard.service;

import kz.dietrix.auth.entity.User;
import kz.dietrix.dashboard.dto.DashboardDto;
import kz.dietrix.mealplan.dto.MealPlanDto;
import kz.dietrix.mealplan.service.MealPlanService;
import kz.dietrix.pantry.dto.PantrySummaryDto;
import kz.dietrix.pantry.service.PantryService;
import kz.dietrix.recipes.repository.FavoriteRecipeRepository;
import kz.dietrix.tracking.dto.WaterStatusDto;
import kz.dietrix.tracking.repository.WeightLogRepository;
import kz.dietrix.tracking.service.TrackingService;
import kz.dietrix.userprofile.dto.UserProfileDto;
import kz.dietrix.userprofile.dto.UserTargetsDto;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserProfileService userProfileService;
    private final PantryService pantryService;
    private final MealPlanService mealPlanService;
    private final FavoriteRecipeRepository favoriteRecipeRepository;
    private final TrackingService trackingService;
    private final WeightLogRepository weightLogRepository;

    @Transactional
    public DashboardDto getDashboard() {
        User user = userProfileService.getCurrentUser();
        UserProfileDto profile = userProfileService.getProfile();
        UserTargetsDto targets = userProfileService.getTargets();
        PantrySummaryDto pantrySummary = pantryService.getSummary();
        LocalDate today = LocalDate.now();

        DashboardDto.DashboardDtoBuilder builder = DashboardDto.builder()
                .userName(user.getName())
                .dailyCalorieTarget(targets.getDailyCalories())
                .pantryItemsCount(pantrySummary.getTotalItems())
                .expiringItemsCount(pantrySummary.getExpiringItems())
                .favoriteRecipesCount(favoriteRecipeRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).size())
                .onboardingCompleted(profile.isOnboardingCompleted())
                .bmi(targets.getBmi())
                .bmiCategory(targets.getBmiCategory());

        // ─── Today: calories + macros (plan completed + free meals) ────────
        int planCalories = mealPlanService.sumCompletedCaloriesForDate(user.getId(), today);
        int[] planMacros = mealPlanService.sumCompletedMacrosForDate(user.getId(), today);
        int freeCalories = trackingService.sumFreeMealCaloriesForDate(user.getId(), today);
        int[] freeMacros = trackingService.sumFreeMealMacrosForDate(user.getId(), today);

        builder.todayCaloriesConsumed(planCalories + freeCalories)
                .todayProtein(planMacros[0] + freeMacros[0])
                .todayCarbs(planMacros[1] + freeMacros[1])
                .todayFat(planMacros[2] + freeMacros[2]);

        // ─── Water ────────────────────────────────────────────────────────
        WaterStatusDto water = trackingService.getWaterToday();
        builder.waterConsumedMl(water.getConsumedMl())
                .waterTargetMl(water.getTargetMl());

        // ─── Weight ───────────────────────────────────────────────────────
        weightLogRepository.findLatestByUserId(user.getId())
                .ifPresent(w -> builder.currentWeightKg(w.getWeightKg()));

        // ─── Streak ───────────────────────────────────────────────────────
        builder.streakDays(mealPlanService.computeStreak(user.getId()));

        // ─── Meal plan summary ────────────────────────────────────────────
        try {
            MealPlanDto currentPlan = mealPlanService.getCurrentPlan();
            builder.hasMealPlan(true)
                    .mealPlanTotalMeals(currentPlan.getTotalMeals())
                    .mealPlanCompletedMeals(currentPlan.getCompletedMeals());

            double progress = currentPlan.getTotalMeals() > 0
                    ? (double) currentPlan.getCompletedMeals() / currentPlan.getTotalMeals() * 100
                    : 0;
            builder.mealPlanProgressPercent(Math.round(progress * 10) / 10.0);


        } catch (Exception e) {
            builder.hasMealPlan(false)
                    .mealPlanTotalMeals(0)
                    .mealPlanCompletedMeals(0)
                    .mealPlanProgressPercent(0);
        }

        return builder.build();
    }
}
