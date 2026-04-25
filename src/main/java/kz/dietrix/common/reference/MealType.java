package kz.dietrix.common.reference;

import lombok.Getter;

@Getter
public enum MealType {
    BREAKFAST("Breakfast"),
    MAIN("Main course"),
    SNACK("Snack"),
    DESSERT("Dessert");

    private final String displayName;

    MealType(String displayName) {
        this.displayName = displayName;
    }
}

