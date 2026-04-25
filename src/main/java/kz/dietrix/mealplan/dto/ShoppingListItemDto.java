package kz.dietrix.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemDto {

    private Long id;
    private String name;
    private String amount;
    private String unit;
    private boolean purchased;
}

