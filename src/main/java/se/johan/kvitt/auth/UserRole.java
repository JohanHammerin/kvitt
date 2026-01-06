package se.johan.kvitt.auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static se.johan.kvitt.auth.UserPermission.*;

/**
 * Representerar de olika användarrollerna i systemet och deras tillhörande rättigheter.
 * Varje roll definierar en uppsättning behörigheter som styr vad en användare får göra.
 * Som jag nämnt tidigare är detta enbart tänk för framtida implementationer.
 */
public enum UserRole {

    /** Gästroll utan specifika rättigheter. */
    GUEST(
            UserRoleName.GUEST.getRoleName(),
            Set.of()
    ),

    /** Standardanvändare med rättigheter att läsa och skriva. */
    USER(
            UserRoleName.USER.getRoleName(),
            Set.of(
                    READ,
                    WRITE
            )
    ),

    /** Administratör med fullständiga rättigheter, inklusive borttagning. */
    ADMIN(
            UserRoleName.ADMIN.getRoleName(),
            Set.of(
                    READ,
                    WRITE,
                    DELETE
            )
    );

    private final String roleName;
    private final Set<UserPermission> userPermissions;

    /**
     * Konstruktor för UserRole.
     * @param roleName Namnet på rollen.
     * @param userPermissions Uppsättningen av rättigheter som tillhör rollen.
     */
    UserRole(String roleName, Set<UserPermission> userPermissions) {
        this.roleName = roleName;
        this.userPermissions = userPermissions;
    }

    /**
     * Hämtar rollens namn.
     * @return Rollnamnet som en sträng.
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Hämtar de rättigheter som är kopplade till rollen.
     * @return Ett Set med UserPermission.
     */
    public Set<UserPermission> getUserPermissions() {
        return userPermissions;
    }

    /**
     * Transformerar rollen och dess rättigheter till en lista av SimpleGrantedAuthority.
     * Används av Spring Security för att hantera auktorisering.
     * @return En lista med authorities för rollen och dess permissions.
     */
    public List<SimpleGrantedAuthority> getUserAuthorities() {

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        authorityList.add(new SimpleGrantedAuthority(this.roleName));
        authorityList.addAll(
                this.userPermissions.stream().map(
                        userPermission -> new SimpleGrantedAuthority(userPermission.getUserPermission())
                ).toList()
        );

        return authorityList;
    }
}