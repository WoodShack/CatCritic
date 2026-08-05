package com.cpan228.catcritic.model;

public enum Role {
    ADMIN("Admin"),
    CAT_OWNER("Cat Owner"),
    CAT_VIEWER("Cat Viewer");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
