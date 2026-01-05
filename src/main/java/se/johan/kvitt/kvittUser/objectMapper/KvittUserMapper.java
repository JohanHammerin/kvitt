package se.johan.kvitt.kvittUser.objectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import se.johan.kvitt.auth.UserRole;
import se.johan.kvitt.kvittUser.dto.KvittUserCreateKvittUserRequestDTO;
import se.johan.kvitt.kvittUser.model.KvittUser;

import java.util.HashSet;
import java.util.Set;

@Component
public class KvittUserMapper {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public KvittUserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public KvittUser toEntity(KvittUserCreateKvittUserRequestDTO kvittUserCreateKvittUserRequestDTO) {

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);

        return new KvittUser(
                null,
                kvittUserCreateKvittUserRequestDTO.username(),
                passwordEncoder.encode(kvittUserCreateKvittUserRequestDTO.password()),
                true,   // accountNonExpired
                true,   // accountNonLocked
                true,   // credentialsNonExpired
                true,   // enabled
                roles // roles
        );

    }

}
