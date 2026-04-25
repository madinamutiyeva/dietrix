package kz.dietrix.tracking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeMealLogDto {
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 30)
    private String mealType;        // BREAKFAST | LUNCH | DINNER | SNACK

    @PositiveOrZero
    private Integer calories;
    @PositiveOrZero
    private Integer protein;
    @PositiveOrZero
    private Integer carbs;
    @PositiveOrZero
    private Integer fat;

    private LocalDate loggedOn;     // null → today
    @Size(max = 500)
    private String note;
}

