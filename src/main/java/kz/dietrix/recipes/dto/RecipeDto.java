package kz.dietrix.recipes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {

    private Long id;
    private String title;
    private String description;
    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fat;
    private Integer cookTimeMinutes;
    private String cuisine;
    private String imageUrl;
    private String mealType;
    private boolean favorite;
}

