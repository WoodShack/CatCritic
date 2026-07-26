package com.cpan228.catcritic.model;

public enum Breed {
    DOMESTIC_SHORTHAIR("Domestic Shorthair"),
    DOMESTIC_LONGHAIR("Domestic Longhair"),
    MAINE_COON("Maine Coon"),
    SIAMESE("Siamese"),
    PERSIAN("Persian"),
    RAGDOLL("Ragdoll"),
    BENGAL("Bengal"),
    BRITISH_SHORTHAIR("British Shorthair"),
    SPHYNX("Sphynx"),
    ABYSSINIAN("Abyssinian"),
    SCOTTISH_FOLD("Scottish Fold"),
    RUSSIAN_BLUE("Russian Blue"),
    OTHER("Other");

    private final String label;

    Breed(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
