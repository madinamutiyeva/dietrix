package kz.dietrix.userprofile.dto;

import kz.dietrix.common.reference.ActivityLevel;
import kz.dietrix.common.reference.Gender;
import kz.dietrix.common.reference.Goal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private Gender gender;
    private Integer age;
    private Double heightCm;
    private Double weightKg;
    private Goal goal;
    private ActivityLevel activityLevel;
    private String avatarUrl;
    private boolean onboardingCompleted;
}

