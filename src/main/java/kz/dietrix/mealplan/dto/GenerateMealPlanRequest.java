package kz.dietrix.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateMealPlanRequest {

    private String cuisine;
    private boolean usePantry = true;
    private String additionalNotes;
}

