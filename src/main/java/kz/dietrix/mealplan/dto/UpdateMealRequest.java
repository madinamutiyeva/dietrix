package kz.dietrix.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMealRequest {

    private Long recipeId;
    private Boolean completed;
}

