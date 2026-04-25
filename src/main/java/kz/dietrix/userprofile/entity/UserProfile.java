package kz.dietrix.userprofile.entity;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import kz.dietrix.common.reference.ActivityLevel;
import kz.dietrix.common.reference.Gender;
import kz.dietrix.common.reference.Goal;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

    @Column(name = "height_cm")
    private Double heightCm;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Enumerated(EnumType.STRING)
    private Goal goal;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level")
    private ActivityLevel activityLevel;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "onboarding_completed")
    @Builder.Default
    private boolean onboardingCompleted = false;

    @Column(name = "onboarding_step")
    @Builder.Default
    private int onboardingStep = 0;
}

