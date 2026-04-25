package kz.dietrix.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingStatusDto {

    private int currentStep;
    private int totalSteps;
    private boolean completed;
    private String nextStep;

    public static final int TOTAL_STEPS = 3;

    public static OnboardingStatusDto of(int step, boolean completed) {
        String next = switch (step) {
            case 0 -> "basic-info";
            case 1 -> "activity-goal";
            case 2 -> "preferences";
            default -> "completed";
        };

        return OnboardingStatusDto.builder()
                .currentStep(step)
                .totalSteps(TOTAL_STEPS)
                .completed(completed)
                .nextStep(completed ? "completed" : next)
                .build();
    }
}

