package kz.dietrix.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanDto {

    private Long id;
    private LocalDate date;
    private String status;
    private int totalMeals;
    private int completedMeals;
    private int totalCalories;
    private List<MealPlanMealDto> meals;
}
