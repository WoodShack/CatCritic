package com.cpan228.catcritic.model;

public enum Role {

    CAT_VIEWER("Cat Viewer"),
    CAT_OWNER("Cat Owner"),
    ADMIN("Administrator");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
