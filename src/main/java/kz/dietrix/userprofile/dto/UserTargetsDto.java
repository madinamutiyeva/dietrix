package kz.dietrix.userprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTargetsDto {

    // Основные целевые показатели
    private int dailyCalories;
    private int proteinGrams;
    private int carbsGrams;
    private int fatGrams;

    // Детали расчёта
    private int bmr;           // Базовый метаболизм (Mifflin-St Jeor)
    private int tdee;          // Полный суточный расход энергии
    private String formula;    // Название формулы

    // Индекс массы тела
    private double bmi;
    private String bmiCategory;

    // Рекомендация по воде (мл/день)
    private int waterMl;

    // Процентное соотношение макронутриентов
    private int proteinPercent;
    private int carbsPercent;
    private int fatPercent;

    // Входные данные (для отображения на UI)
    private String gender;
    private Integer age;
    private Double weightKg;
    private Double heightCm;
    private String activityLevel;
    private String goal;
}

