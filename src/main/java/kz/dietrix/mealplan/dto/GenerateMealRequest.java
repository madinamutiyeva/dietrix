package kz.dietrix.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateMealRequest {

    private String mealType;        // breakfast, main, snack, dessert
    private String cuisine;         // optional cuisine preference
    private Integer maxCalories;    // optional calorie limit
    private boolean usePantry;      // use pantry ingredients
    private String additionalNotes; // free-text instructions for AI
}

