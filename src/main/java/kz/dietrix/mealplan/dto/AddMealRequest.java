package kz.dietrix.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMealRequest {

    private Long recipeId;
    private String mealType;  // BREAKFAST, MAIN, SNACK, DESSERT
}

