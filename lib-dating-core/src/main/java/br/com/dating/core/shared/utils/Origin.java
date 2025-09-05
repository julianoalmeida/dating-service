package br.com.dating.core.shared.utils;

/**
 * Enumerates the possible origins of a request.
 */
public enum Origin {

    DATING_FRONTEND("DATING_FRONTEND");

    private final String publicName;

    Origin(String publicName) {
        this.publicName = publicName;
    }

    public String getPublicName() {
        return publicName;
    }
}
