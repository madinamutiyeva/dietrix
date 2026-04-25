package kz.dietrix.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanMealDto {

    private Long id;
    private String mealType;
    private Long recipeId;
    private String recipeTitle;
    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fat;
    private boolean completed;
}

