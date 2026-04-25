package kz.dietrix.common.reference;

import lombok.Getter;

@Getter
public enum Goal {
    LOSE_WEIGHT("Lose weight"),
    MAINTAIN("Maintain weight"),
    GAIN_MUSCLE("Gain muscle"),
    GAIN_WEIGHT("Gain weight");

    private final String displayName;

    Goal(String displayName) {
        this.displayName = displayName;
    }
}

