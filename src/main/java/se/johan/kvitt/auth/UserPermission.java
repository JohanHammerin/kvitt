package se.johan.kvitt.auth;

/**
 * Representerar specifika rättigheter som en användare kan inneha i systemet.
 * Används för att kontrollera åtkomst till olika typer av operationer.
 * Dessa används inte egentligen men är ett bra framtidssäkring om jag skulle lägga till admin funktioner.
 */
public enum UserPermission {

    /** Rättighet att läsa data. */
    READ("READ"),

    /** Rättighet att skapa eller uppdatera data. */
    WRITE("WRITE"),

    /** Rättighet att ta bort data. */
    DELETE("DELETE");

    private final String userPermission;

    /**
     * Konstruktor för UserPermission.
     * * @param userPermission Strängrepresentationen av rättigheten.
     */
    UserPermission(String userPermission) {
        this.userPermission = userPermission;
    }

    /**
     * Hämtar rättigheten som en sträng.
     **@return Rättighetens namn.
     */
    public String getUserPermission() {
        return userPermission;
    }
}