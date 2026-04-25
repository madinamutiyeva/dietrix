package kz.dietrix.common.dto;

import jakarta.validation.constraints.*;
import kz.dietrix.common.reference.ActivityLevel;
import kz.dietrix.common.reference.Gender;
import kz.dietrix.common.reference.Goal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalorieCalculatorRequest {

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Age is required")
    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 120, message = "Age must be at most 120")
    private Integer age;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "20.0", message = "Weight must be at least 20 kg")
    @DecimalMax(value = "500.0", message = "Weight must be at most 500 kg")
    private Double weightKg;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "50.0", message = "Height must be at least 50 cm")
    @DecimalMax(value = "300.0", message = "Height must be at most 300 cm")
    private Double heightCm;

    @NotNull(message = "Activity level is required")
    private ActivityLevel activityLevel;

    @NotNull(message = "Goal is required")
    private Goal goal;
}

