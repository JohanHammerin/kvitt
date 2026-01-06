package se.johan.kvitt.auth;

/**
 * Definierar de tekniska namnen för systemets roller enligt Spring Securitys standardformat.
 * Dessa namn inkluderar prefixet "ROLE_" för att möjliggöra korrekt hantering av rollbaserad auktorisering.
 */
public enum UserRoleName {

    /** Den tekniska identifieraren för gästrollen. */
    GUEST("ROLE_GUEST"),

    /** Den tekniska identifieraren för användarrollen. */
    USER("ROLE_USER"),

    /** Den tekniska identifieraren för administratörsrollen. */
    ADMIN("ROLE_ADMIN");

    private final String roleName;

    /**
     * Konstruktor för UserRoleName.
     * @param roleName Rollens tekniska namn inklusive prefix.
     */
    UserRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Hämtar rollens tekniska namn.
     * @return Rollnamnet som en sträng (t.ex. "ROLE_USER").
     */
    public String getRoleName() {
        return roleName;
    }
}