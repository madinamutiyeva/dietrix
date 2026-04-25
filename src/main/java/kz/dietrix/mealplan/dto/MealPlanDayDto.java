package kz.dietrix.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanDayDto {

    private Long id;
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private int totalCalories;
    private List<MealPlanMealDto> meals;
}

