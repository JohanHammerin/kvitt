package se.johan.kvitt.kvittUser.objectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import se.johan.kvitt.kvittUser.dto.request.KvittUserCreateKvittUserRequestDTO;
import se.johan.kvitt.kvittUser.model.KvittUser;

@Component
public class KvittUserMapper {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public KvittUserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public KvittUser toEntity(KvittUserCreateKvittUserRequestDTO kvittUserCreateKvittUserRequestDTO) {
        return new KvittUser(
                null,
                kvittUserCreateKvittUserRequestDTO.username(),
                passwordEncoder.encode(kvittUserCreateKvittUserRequestDTO.password())
        );
    }

}
