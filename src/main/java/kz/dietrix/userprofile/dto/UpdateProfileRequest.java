package kz.dietrix.userprofile.dto;

import kz.dietrix.common.reference.ActivityLevel;
import kz.dietrix.common.reference.Gender;
import kz.dietrix.common.reference.Goal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    private String name;
    private Gender gender;
    private Integer age;
    private Double heightCm;
    private Double weightKg;
    private Goal goal;
    private ActivityLevel activityLevel;
    private String avatarUrl;
}

