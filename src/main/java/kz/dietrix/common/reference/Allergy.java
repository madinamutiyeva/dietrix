package kz.dietrix.common.reference;

import lombok.Getter;

@Getter
public enum Allergy {
    GLUTEN("Gluten"),
    DAIRY("Dairy"),
    EGGS("Eggs"),
    NUTS("Tree nuts"),
    PEANUTS("Peanuts"),
    SOY("Soy"),
    FISH("Fish"),
    SHELLFISH("Shellfish"),
    WHEAT("Wheat"),
    SESAME("Sesame"),
    SULFITES("Sulfites"),
    LACTOSE("Lactose"),
    FRUCTOSE("Fructose");

    private final String displayName;

    Allergy(String displayName) {
        this.displayName = displayName;
    }
}

