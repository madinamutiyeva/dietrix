package kz.dietrix.common.reference;

import kz.dietrix.common.dto.ApiResponse;
import kz.dietrix.common.dto.CalorieCalculatorRequest;
import kz.dietrix.common.dto.CalorieCalculatorResponse;
import kz.dietrix.common.util.NutritionCalculator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reference")
@Tag(name = "Reference", description = "Reference data and calorie calculator")
public class ReferenceController {

    @GetMapping("/allergies")
    @Operation(summary = "Get all allergy types")
    public ApiResponse<List<Map<String, String>>> getAllergies() {
        List<Map<String, String>> allergies = Arrays.stream(Allergy.values())
                .map(a -> Map.of("code", a.name(), "name", a.getDisplayName()))
                .toList();
        return ApiResponse.success(allergies);
    }

    @GetMapping("/goals")
    @Operation(summary = "Get all fitness goals")
    public ApiResponse<List<Map<String, String>>> getGoals() {
        List<Map<String, String>> goals = Arrays.stream(Goal.values())
                .map(g -> Map.of("code", g.name(), "name", g.getDisplayName()))
                .toList();
        return ApiResponse.success(goals);
    }

    @GetMapping("/activity-levels")
    @Operation(summary = "Get all activity levels")
    public ApiResponse<List<Map<String, String>>> getActivityLevels() {
        List<Map<String, String>> levels = Arrays.stream(ActivityLevel.values())
                .map(l -> Map.of("code", l.name(), "description", l.getDescription()))
                .toList();
        return ApiResponse.success(levels);
    }

    @GetMapping("/diet-types")
    @Operation(summary = "Get all diet types")
    public ApiResponse<List<Map<String, String>>> getDietTypes() {
        List<Map<String, String>> types = Arrays.stream(DietType.values())
                .map(d -> Map.of("code", d.name(), "name", d.getDisplayName()))
                .toList();
        return ApiResponse.success(types);
    }

    @GetMapping("/genders")
    @Operation(summary = "Get all genders")
    public ApiResponse<List<Map<String, String>>> getGenders() {
        List<Map<String, String>> genders = Arrays.stream(Gender.values())
                .map(g -> Map.of("code", g.name(), "name", g.getDisplayName()))
                .toList();
        return ApiResponse.success(genders);
    }

    @GetMapping("/cuisines")
    @Operation(summary = "Get all cuisine types")
    public ApiResponse<List<Map<String, String>>> getCuisines() {
        List<Map<String, String>> cuisines = Arrays.stream(Cuisine.values())
                .map(c -> Map.of("code", c.name(), "name", c.getDisplayName()))
                .toList();
        return ApiResponse.success(cuisines);
    }

    @GetMapping("/meal-types")
    @Operation(summary = "Get all meal types")
    public ApiResponse<List<Map<String, String>>> getMealTypes() {
        List<Map<String, String>> types = Arrays.stream(MealType.values())
                .map(m -> Map.of("code", m.name(), "name", m.getDisplayName()))
                .toList();
        return ApiResponse.success(types);
    }

    /**
     * Публичный калькулятор калорий — не требует авторизации.
     * Рассчитывает КБЖУ, BMR, TDEE, BMI, воду по входным параметрам.
     */
    @PostMapping("/calculate-calories")
    @Operation(summary = "Calculate daily calories, macros, BMI (public, no auth required)",
            description = "Uses Mifflin-St Jeor and Harris-Benedict equations to calculate BMR, TDEE, " +
                    "daily calorie target, macronutrient split, BMI, and water intake recommendation.")
    public ApiResponse<CalorieCalculatorResponse> calculateCalories(
            @Valid @RequestBody CalorieCalculatorRequest request) {

        Gender gender = request.getGender();
        double weight = request.getWeightKg();
        double height = request.getHeightCm();
        int age = request.getAge();
        ActivityLevel activity = request.getActivityLevel();
        Goal goal = request.getGoal();

        // Расчёт BMR по двум формулам
        int bmrMifflin = (int) Math.round(
                NutritionCalculator.calculateBMR(gender, weight, height, age));
        int bmrHarris = (int) Math.round(
                NutritionCalculator.calculateBMR_HarrisBenedict(gender, weight, height, age));

        // TDEE и целевые калории
        int tdee = (int) Math.round(
                NutritionCalculator.calculateTDEE(gender, weight, height, age, activity));
        int calories = NutritionCalculator.calculateDailyCalories(gender, weight, height, age, activity, goal);

        // Макронутриенты
        int protein = NutritionCalculator.calculateProtein(weight, goal);
        int fat = NutritionCalculator.calculateFat(calories);
        int carbs = NutritionCalculator.calculateCarbs(calories, protein, fat);

        // BMI
        double bmi = NutritionCalculator.calculateBMI(weight, height);
        String bmiCategory = NutritionCalculator.getBmiCategory(bmi);

        // Вода
        int waterMl = NutritionCalculator.calculateWaterIntake(weight, activity);

        // Пояснения
        String calorieExplanation = buildCalorieExplanation(bmrMifflin, tdee, calories, goal);
        String proteinExplanation = buildProteinExplanation(protein, weight, goal);

        CalorieCalculatorResponse response = CalorieCalculatorResponse.builder()
                .gender(gender.getDisplayName())
                .age(age)
                .weightKg(weight)
                .heightCm(height)
                .activityLevel(activity.getDescription())
                .goal(goal.getDisplayName())
                .bmrMifflinStJeor(bmrMifflin)
                .bmrHarrisBenedict(bmrHarris)
                .tdee(tdee)
                .dailyCalories(calories)
                .proteinGrams(protein)
                .carbsGrams(carbs)
                .fatGrams(fat)
                .proteinPercent(NutritionCalculator.calculateProteinPercent(protein, calories))
                .carbsPercent(NutritionCalculator.calculateCarbsPercent(carbs, calories))
                .fatPercent(NutritionCalculator.calculateFatPercent(fat, calories))
                .bmi(bmi)
                .bmiCategory(bmiCategory)
                .waterMl(waterMl)
                .calorieExplanation(calorieExplanation)
                .proteinExplanation(proteinExplanation)
                .build();

        return ApiResponse.success(response);
    }

    private String buildCalorieExplanation(int bmr, int tdee, int target, Goal goal) {
        String adjustment = switch (goal) {
            case LOSE_WEIGHT -> "500 kcal deficit applied for weight loss (~0.5 kg/week)";
            case MAINTAIN -> "no adjustment, maintenance level";
            case GAIN_MUSCLE -> "300 kcal surplus applied for lean muscle gain";
            case GAIN_WEIGHT -> "500 kcal surplus applied for steady weight gain";
        };
        return String.format(
                "Your BMR (basal metabolic rate) is %d kcal. " +
                "With your activity level, your TDEE (total daily energy expenditure) is %d kcal. " +
                "Your target is %d kcal/day (%s).",
                bmr, tdee, target, adjustment);
    }

    private String buildProteinExplanation(int protein, double weight, Goal goal) {
        double ratio = Math.round(protein / weight * 10.0) / 10.0;
        return String.format(
                "Recommended protein: %dg/day (%.1fg per kg of body weight). " +
                "This is optimized for your goal: %s.",
                protein, ratio, goal.getDisplayName());
    }
}

