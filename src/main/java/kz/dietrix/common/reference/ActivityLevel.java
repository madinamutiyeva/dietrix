package kz.dietrix.common.reference;

import lombok.Getter;

@Getter
public enum ActivityLevel {
    SEDENTARY("Sedentary (little or no exercise)", 1.2),
    LIGHTLY_ACTIVE("Lightly active (1-3 days/week)", 1.375),
    MODERATELY_ACTIVE("Moderately active (3-5 days/week)", 1.55),
    VERY_ACTIVE("Very active (6-7 days/week)", 1.725),
    EXTRA_ACTIVE("Extra active (very hard exercise)", 1.9);

    private final String description;
    private final double multiplier;

    ActivityLevel(String description, double multiplier) {
        this.description = description;
        this.multiplier = multiplier;
    }
}

