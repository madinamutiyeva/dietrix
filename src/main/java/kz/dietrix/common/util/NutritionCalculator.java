package kz.dietrix.common.util;

import kz.dietrix.common.reference.ActivityLevel;
import kz.dietrix.common.reference.Gender;
import kz.dietrix.common.reference.Goal;

/**
 * Utility class for calculating daily nutritional targets
 * using the Mifflin-St Jeor equation.
 */
public final class NutritionCalculator {

    private NutritionCalculator() {
    }

    /**
     * Calculate Basal Metabolic Rate (BMR) using Mifflin-St Jeor equation.
     */
    public static double calculateBMR(Gender gender, double weightKg, double heightCm, int age) {
        if (gender == Gender.MALE) {
            return 10 * weightKg + 6.25 * heightCm - 5 * age + 5;
        } else {
            return 10 * weightKg + 6.25 * heightCm - 5 * age - 161;
        }
    }

    /**
     * Calculate Total Daily Energy Expenditure (TDEE).
     */
    public static double calculateTDEE(Gender gender, double weightKg, double heightCm,
                                        int age, ActivityLevel activityLevel) {
        double bmr = calculateBMR(gender, weightKg, heightCm, age);
        return bmr * activityLevel.getMultiplier();
    }

    /**
     * Adjust calories based on user's goal.
     * Uses fixed kcal surplus/deficit per NHS & Mayo Clinic guidelines:
     *   LOSE_WEIGHT  → TDEE − 500 kcal  (~0.5 kg/week loss)
     *   MAINTAIN     → TDEE
     *   GAIN_MUSCLE  → TDEE + 300 kcal  (lean bulk)
     *   GAIN_WEIGHT  → TDEE + 500 kcal  (steady weight gain)
     * A safety floor prevents dangerously low intake (1200 F / 1500 M).
     */
    public static int calculateDailyCalories(Gender gender, double weightKg, double heightCm,
                                              int age, ActivityLevel activityLevel, Goal goal) {
        double tdee = calculateTDEE(gender, weightKg, heightCm, age, activityLevel);

        double calories = switch (goal) {
            case LOSE_WEIGHT  -> tdee - 500;
            case MAINTAIN     -> tdee;
            case GAIN_MUSCLE  -> tdee + 300;
            case GAIN_WEIGHT  -> tdee + 500;
        };

        // Safety floor: never recommend below healthy minimum
        int minCalories = (gender == Gender.MALE) ? 1500 : 1200;
        return (int) Math.round(Math.max(calories, minCalories));
    }

    /**
     * Calculate daily protein target in grams.
     */
    public static int calculateProtein(double weightKg, Goal goal) {
        double multiplier = switch (goal) {
            case LOSE_WEIGHT -> 2.0;
            case MAINTAIN -> 1.6;
            case GAIN_MUSCLE -> 2.2;
            case GAIN_WEIGHT -> 1.8;
        };
        return (int) Math.round(weightKg * multiplier);
    }

    /**
     * Calculate daily fat target in grams (25-30% of calories).
     */
    public static int calculateFat(int dailyCalories) {
        return (int) Math.round(dailyCalories * 0.275 / 9.0);
    }

    /**
     * Calculate daily carbs target in grams (remaining calories).
     */
    public static int calculateCarbs(int dailyCalories, int proteinGrams, int fatGrams) {
        int remainingCalories = dailyCalories - (proteinGrams * 4) - (fatGrams * 9);
        return Math.max(0, (int) Math.round(remainingCalories / 4.0));
    }

    /**
     * Calculate recommended daily water intake in milliliters.
     * Base: 30-35 ml per kg of body weight, adjusted for activity.
     */
    public static int calculateWaterIntake(double weightKg, ActivityLevel activityLevel) {
        double baseMl = weightKg * 33; // средний коэффициент
        double activityMultiplier = switch (activityLevel) {
            case SEDENTARY -> 1.0;
            case LIGHTLY_ACTIVE -> 1.1;
            case MODERATELY_ACTIVE -> 1.2;
            case VERY_ACTIVE -> 1.3;
            case EXTRA_ACTIVE -> 1.4;
        };
        return (int) Math.round(baseMl * activityMultiplier / 100.0) * 100; // округление до 100 мл
    }

    /**
     * Calculate BMR using revised Harris-Benedict equation (for comparison).
     */
    public static double calculateBMR_HarrisBenedict(Gender gender, double weightKg, double heightCm, int age) {
        if (gender == Gender.MALE) {
            return 88.362 + 13.397 * weightKg + 4.799 * heightCm - 5.677 * age;
        } else {
            return 447.593 + 9.247 * weightKg + 3.098 * heightCm - 4.330 * age;
        }
    }

    /**
     * Calculate macronutrient percentages.
     */
    public static int calculateProteinPercent(int proteinGrams, int totalCalories) {
        if (totalCalories == 0) return 0;
        return (int) Math.round((proteinGrams * 4.0) / totalCalories * 100);
    }

    public static int calculateCarbsPercent(int carbsGrams, int totalCalories) {
        if (totalCalories == 0) return 0;
        return (int) Math.round((carbsGrams * 4.0) / totalCalories * 100);
    }

    public static int calculateFatPercent(int fatGrams, int totalCalories) {
        if (totalCalories == 0) return 0;
        return (int) Math.round((fatGrams * 9.0) / totalCalories * 100);
    }

    /**
     * Calculate BMI (Body Mass Index).
     */
    public static double calculateBMI(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return Math.round(weightKg / (heightM * heightM) * 10.0) / 10.0;
    }

    /**
     * Get BMI category.
     */
    public static String getBmiCategory(double bmi) {
        if (bmi < 16) return "Severe underweight";
        if (bmi < 17) return "Moderate underweight";
        if (bmi < 18.5) return "Mild underweight";
        if (bmi < 25) return "Normal weight";
        if (bmi < 30) return "Overweight";
        if (bmi < 35) return "Obese class I";
        if (bmi < 40) return "Obese class II";
        return "Obese class III";
    }
}

