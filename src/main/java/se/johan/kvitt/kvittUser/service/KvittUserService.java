package se.johan.kvitt.kvittUser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import se.johan.kvitt.kvittUser.dto.KvittUserCreateKvittUserRequestDTO;
import se.johan.kvitt.kvittUser.dto.KvittUserLoginRequestDTO;
import se.johan.kvitt.kvittUser.jwt.JwtUtils;
import se.johan.kvitt.kvittUser.model.KvittUser;
import se.johan.kvitt.kvittUser.objectMapper.KvittUserMapper;
import se.johan.kvitt.kvittUser.repository.KvittUserRepository;

@Service
public class KvittUserService {
    private final KvittUserRepository kvittUserRepository;
    private final KvittUserMapper kvittUserMapper;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public KvittUserService(KvittUserRepository kvittUserRepository, KvittUserMapper kvittUserMapper, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.kvittUserRepository = kvittUserRepository;
        this.kvittUserMapper = kvittUserMapper;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    public KvittUser createKvittUser(KvittUserCreateKvittUserRequestDTO dto) {
        KvittUser saved = kvittUserRepository.save(kvittUserMapper.toEntity(dto));
        logger.info("New KvittUser was created & saved: id={}", saved.getId());
        return saved;
    }

    public String login(KvittUserLoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        KvittUser kvittUser = kvittUserRepository.findByUsername(dto.username())
                .orElseThrow(); // borde inte hända

        return jwtUtils.generateJwtToken(kvittUser);
    }


}
