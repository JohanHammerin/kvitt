package se.johan.kvitt.kvittUser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import se.johan.kvitt.kvittUser.dto.request.KvittUserCreateKvittUserRequestDTO;
import se.johan.kvitt.kvittUser.model.KvittUser;
import se.johan.kvitt.kvittUser.objectMapper.KvittUserMapper;
import se.johan.kvitt.kvittUser.repository.KvittUserRepository;

@Service
public class KvittUserService {
    private final KvittUserRepository kvittUserRepository;
    private final KvittUserMapper kvittUserMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public KvittUserService(KvittUserRepository kvittUserRepository, KvittUserMapper kvittUserMapper) {
        this.kvittUserRepository = kvittUserRepository;
        this.kvittUserMapper = kvittUserMapper;
    }

    public Mono<KvittUser> createKvittUser(KvittUserCreateKvittUserRequestDTO kvittUserCreateKvittUserRequestDTO) {


        logger.info("New KvittUser was created & saved");
        return kvittUserRepository.save(kvittUserMapper.toEntity(kvittUserCreateKvittUserRequestDTO));
    }

}
