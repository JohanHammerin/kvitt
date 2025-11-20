package se.johan.kvitt.auth;

/* UserPermission - is a simple String that will be handled by Spring Security
*   If you want to add a new Permission, simply follow domain logic e.g:
*       TODO_READ, TODO_DELETE, USER_READ, USER_DELETE etc...
* */

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
