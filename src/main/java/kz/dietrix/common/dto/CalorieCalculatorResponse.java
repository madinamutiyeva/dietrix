package kz.dietrix.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalorieCalculatorResponse {

    // Входные данные
    private String gender;
    private int age;
    private double weightKg;
    private double heightCm;
    private String activityLevel;
    private String goal;

    // Базовый метаболизм
    private int bmrMifflinStJeor;
    private int bmrHarrisBenedict;

    // Полный суточный расход (TDEE)
    private int tdee;

    // Рекомендуемые калории (с учётом цели)
    private int dailyCalories;

    // Макронутриенты (граммы)
    private int proteinGrams;
    private int carbsGrams;
    private int fatGrams;

    // Макронутриенты (проценты)
    private int proteinPercent;
    private int carbsPercent;
    private int fatPercent;

    // BMI
    private double bmi;
    private String bmiCategory;

    // Рекомендуемое потребление воды
    private int waterMl;

    // Пояснения
    private String calorieExplanation;
    private String proteinExplanation;
}

