package se.johan.kvitt.auth;

/* UserRole: Handles Authorities
*   Should contain both ROLE + PERMISSIONS
*   Should contain a way to return SimpleGrantedAuthority (Spring Class)
*   import static - removes the class requirement for Variables (no more dots)
*       NOTE: The class still exists, but is not necessary to call
* */

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static se.johan.kvitt.auth.UserPermission.*;
import static se.johan.kvitt.auth.UserPermission.READ;

public enum UserRole {
    
    GUEST(
            UserRoleName.GUEST.getRoleName(),
            Set.of() // 0 Permissions // READ permission could be available here!
    ),
    
    USER(
            UserRoleName.USER.getRoleName(),
            Set.of(
                    READ,
                    WRITE
            )
    ),
    
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

    UserRole(String roleName, Set<UserPermission> userPermissions) {
        this.roleName = roleName;
        this.userPermissions = userPermissions;
    }

    public String getRoleName() {
        return roleName;
    }

    public Set<UserPermission> getUserPermissions() {
        return userPermissions;
    }

    // Get a LIST that Spring understands - containing both ROLE + PERMISSION
    public List<SimpleGrantedAuthority> getUserAuthorities() {

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // this == the choice made after UserRole. (e.g: UserRole.ADMIN)
        authorityList.add(new SimpleGrantedAuthority(this.roleName));
        authorityList.addAll(
                this.userPermissions.stream().map(
                        userPermission -> new SimpleGrantedAuthority(userPermission.getUserPermission())
                ).toList()
        );

        return authorityList;
    }

}
