package kz.dietrix.common.reference;

import lombok.Getter;

@Getter
public enum DietType {
    NONE("No specific diet"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    KETO("Keto"),
    PALEO("Paleo"),
    MEDITERRANEAN("Mediterranean"),
    LOW_CARB("Low carb"),
    HIGH_PROTEIN("High protein"),
    GLUTEN_FREE("Gluten free");

    private final String displayName;

    DietType(String displayName) {
        this.displayName = displayName;
    }
}

