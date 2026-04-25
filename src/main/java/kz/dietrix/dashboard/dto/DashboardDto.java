package kz.dietrix.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    private String userName;
    private int dailyCalorieTarget;
    private int todayCaloriesConsumed;
    private int todayProtein;
    private int todayCarbs;
    private int todayFat;

    private int waterConsumedMl;
    private int waterTargetMl;

    private long pantryItemsCount;
    private long expiringItemsCount;

    private boolean hasMealPlan;
    private int mealPlanTotalMeals;
    private int mealPlanCompletedMeals;
    private double mealPlanProgressPercent;

    private long favoriteRecipesCount;
    private boolean onboardingCompleted;

    private double bmi;
    private String bmiCategory;

    private java.math.BigDecimal currentWeightKg;     // last logged weight, if any
    private int streakDays;                           // consecutive days with completed meals
}

