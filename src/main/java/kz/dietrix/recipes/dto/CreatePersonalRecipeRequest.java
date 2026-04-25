package kz.dietrix.recipes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonalRecipeRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String instructions;
    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fat;
    private Integer cookTimeMinutes;
    private String cuisine;
    private String imageUrl;
    private String mealType;
    private String dietType;
    private List<IngredientInput> ingredients;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientInput {
        private String name;
        private String amount;
        private String unit;
    }
}

