package kz.dietrix.onboarding.dto;

import kz.dietrix.common.reference.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicInfoRequest {

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Age is required")
    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 120, message = "Age must be at most 120")
    private Integer age;

    @NotNull(message = "Height is required")
    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height must be at most 300 cm")
    private Double heightCm;

    @NotNull(message = "Weight is required")
    @Min(value = 20, message = "Weight must be at least 20 kg")
    @Max(value = 500, message = "Weight must be at most 500 kg")
    private Double weightKg;
}

