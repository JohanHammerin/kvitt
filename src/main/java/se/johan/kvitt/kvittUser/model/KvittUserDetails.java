package se.johan.kvitt.kvittUser.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class KvittUserDetails implements UserDetails {
    private final KvittUser kvittUser; // Entity

    public KvittUserDetails(KvittUser kvittUser) {
        this.kvittUser = kvittUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        final Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        kvittUser.getRoles().forEach(
                userRole -> authorities.addAll(userRole.getUserAuthorities()) // Merge arrays
        );

        return Collections.unmodifiableSet(authorities); // Make List 'final' through 'unmodifiable'
    }

    @Override
    public String getPassword() {
        return kvittUser.getPassword();
    }

    @Override
    public String getUsername() {
        return kvittUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return kvittUser.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return kvittUser.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return kvittUser.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return kvittUser.isEnabled();
    }

    public KvittUser getKvittUser() {
        return kvittUser;
    }
}

