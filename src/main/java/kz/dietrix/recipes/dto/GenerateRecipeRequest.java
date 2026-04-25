package kz.dietrix.recipes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRecipeRequest {

    private String mealType;       // breakfast, lunch, dinner, snack
    private String cuisine;        // optional cuisine preference
    private Integer maxCalories;   // optional calorie limit
    private boolean usePantry;     // use pantry ingredients
    private String additionalNotes; // free-text instructions for AI

    /** If true — attach the generated recipe to today's active meal plan (creating one if missing). */
    private boolean addToPlan;
}

