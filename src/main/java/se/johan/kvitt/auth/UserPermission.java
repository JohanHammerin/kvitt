package se.johan.kvitt.auth;


public enum UserPermission {

    READ("READ"),
    WRITE("WRITE"),
    DELETE("DELETE");

    private final String userPermission;

    UserPermission(String userPermission) {
        this.userPermission = userPermission;
    }

    public String getUserPermission() {
        return userPermission;
    }
}
