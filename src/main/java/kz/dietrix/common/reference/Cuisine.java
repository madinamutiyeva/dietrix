package kz.dietrix.common.reference;

import lombok.Getter;

@Getter
public enum Cuisine {
    ITALIAN("Italian"),
    JAPANESE("Japanese"),
    MEXICAN("Mexican"),
    INDIAN("Indian"),
    KOREAN("Korean"),
    FRENCH("French"),
    TURKISH("Turkish"),
    AMERICAN("American"),
    KAZAKH("Kazakh"),
    RUSSIAN("Russian"),
    THAI("Thai"),
    CHINESE("Chinese"),
    GEORGIAN("Georgian"),
    UZBEK("Uzbek"),
    GREEK("Greek"),
    SPANISH("Spanish"),
    INTERNATIONAL("International");

    private final String displayName;

    Cuisine(String displayName) {
        this.displayName = displayName;
    }
}

