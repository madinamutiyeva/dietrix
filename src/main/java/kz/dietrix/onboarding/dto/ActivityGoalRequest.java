package kz.dietrix.onboarding.dto;

import kz.dietrix.common.reference.ActivityLevel;
import kz.dietrix.common.reference.Goal;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityGoalRequest {

    @NotNull(message = "Activity level is required")
    private ActivityLevel activityLevel;

    @NotNull(message = "Goal is required")
    private Goal goal;
}

